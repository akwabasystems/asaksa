
package com.akwabasystems.asakusa.dao.impl;

import com.akwabasystems.asakusa.BaseTestSuite;
import com.akwabasystems.asakusa.dao.UserDao;
import com.akwabasystems.asakusa.model.Address;
import com.akwabasystems.asakusa.model.Gender;
import com.akwabasystems.asakusa.model.Role;
import com.akwabasystems.asakusa.model.User;
import com.akwabasystems.asakusa.model.UserCredentials;
import com.akwabasystems.asakusa.model.UserPreferences;
import com.akwabasystems.asakusa.repository.AsakusaRepository;
import com.akwabasystems.asakusa.repository.RepositoryMapper;
import com.akwabasystems.asakusa.utils.PasswordUtils;
import com.akwabasystems.asakusa.utils.TestUtils;
import com.akwabasystems.asakusa.utils.Timeline;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.PagingIterable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class UserDaoTests extends BaseTestSuite {

    @Autowired
    private CqlSession cqlSession;
    
    @Autowired
    private AsakusaRepository repository;
    
    private RepositoryMapper mapper;
    
    
    @BeforeAll
    public void setup() {
        mapper = RepositoryMapper.builder(cqlSession).build();
        repository.createTables();
    }
    
    
    @Test
    public void testDaoInitialization() {
        UserDao userDao = mapper.userDao();
        assertThat(userDao).isNotNull();
    }
    
    
    @Test
    public void testCreateUserWithInvalidAttributes() {
        UserDao userDao = mapper.userDao();
        User user = new User();
        
        assertThatThrownBy(() -> userDao.create(user, null, new HashSet<Role>()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("error.invalidParameters");
        
        user.setUserId("jsmith");
        
        assertThatThrownBy(() -> userDao.create(user, "jsmith01".toCharArray(), new HashSet<Role>()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("error.invalidParameters");
    }
    
    
    @Test
    public void testCreateAndRetrieveUser() throws Exception {
        UserDao userDao = mapper.userDao();
        
        User user = TestUtils.defaultUser();
        user.setGender(Gender.MALE);
        User newUser = userDao.create(user, "jsmith01".toCharArray(), new HashSet<Role>());
        
        assertThat(newUser).isNotNull();
        assertThat(newUser.getUserId()).isEqualTo(user.getUserId());
        assertThat(newUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(newUser.getNickname()).isNull();
        assertThat(newUser.isEmailVerified()).isTrue();
        assertThat(newUser.getGender()).isEqualTo(Gender.MALE);
        assertThat(newUser.getAddress()).isNotNull();
        
        User userById = userDao.findById(user.getUserId());
        
        assertThat(userById).isNotNull();
        assertThat(userById.getEmail()).isEqualTo(user.getEmail());
        
        Optional<User> userByEmail = userDao.findByEmail(user.getEmail());
        
        assertThat(userByEmail.isEmpty()).isFalse();
        assertThat(userByEmail.get()).isNotNull();
        assertThat(userByEmail.get().getUserId()).isEqualTo(user.getUserId());
        
        boolean userDeleted = userDao.delete(user);
        assertThat(userDeleted).isTrue();
        
    }
    
    
    @Test
    public void testCreateUserWithMostAttributes() throws Exception {
        UserDao userDao = mapper.userDao();
        
        User user = TestUtils.defaultUser();
        user.setGivenName("Joan");
        user.setNickname("therealjoan");
        user.setPreferredUsername("therealjoan");
        
        ZonedDateTime birthDate = ZonedDateTime.of(2000, 1, 1, 
                1, 0, 0, 0, ZoneId.systemDefault());
        user.setBirthDate(Timeline.toUTCFormat(birthDate));
        
        Address address = new Address();
        address.setStreet("1 Main Street");
        address.setCity("San Jose");
        address.setStateOrProvince("CA");
        address.setPostalCode("95109");
        address.setCountry("USA");
        user.setAddress(address);
        
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        
        User newUser = userDao.create(user, "joansmith01".toCharArray(), roles);
        
        assertThat(newUser.getNickname()).isEqualTo("therealjoan");
        assertThat(newUser.getPreferredUsername()).isEqualTo("therealjoan");
        assertThat(newUser.isEmailVerified()).isTrue();
        assertThat(newUser.getPhoneNumber()).isEqualTo("+1-800-555-1212");
        assertThat(newUser.isPhoneNumberVerified()).isTrue();
        assertThat(newUser.getBirthDate()).isNotNull();
        assertThat(newUser.getAddress()).isNotNull();
        assertThat(newUser.getAddress().getStreet()).isEqualTo("1 Main Street");
        
        boolean userDeleted = userDao.delete(user);
        assertThat(userDeleted).isTrue();
        
    }
    
    
    @Test
    public void testCreateUserWithExistingId() throws Exception {
        UserDao userDao = mapper.userDao();
        
        User user = TestUtils.defaultUser();
        user.setGender(Gender.MALE);
        
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        roles.add(Role.ADMIN);
        
        User newUser = userDao.create(user, "tsmith01".toCharArray(), roles);
        
        assertThat(newUser).isNotNull();
        assertThat(newUser.getUserId()).isEqualTo(user.getUserId());
        assertThat(newUser.getEmail()).isEqualTo(user.getEmail());

        final User userWithId = new User(user.getUserId(), "Ken", 
            "Smith", "ksmith01@example.com");
        
        assertThatThrownBy(() -> userDao.create(userWithId, "ksmith01".toCharArray(), new HashSet<Role>()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("error.userAlreadyExists");
        
        final User userWithEmail = new User("ksmith-" + TestUtils.randomID(), 
                "Ken", "Smith", user.getEmail());
        
        assertThatThrownBy(() -> userDao.create(userWithEmail, "ksmith01".toCharArray(), new HashSet<Role>()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("error.emailAlreadyExists");
        
        boolean userDeleted = userDao.delete(user);
        assertThat(userDeleted).isTrue();
    }
    
    
    @Test
    public void testUpdateUser() throws Exception {
        UserDao userDao = mapper.userDao();
        
        User user = TestUtils.defaultUser();
        user.setGivenName("Bob");
        user.setGender(Gender.MALE);
        userDao.create(user, "bsmith01".toCharArray(), new HashSet<Role>());
        
        User userById = userDao.findById(user.getUserId());
        assertThat(userById).isNotNull();
        assertThat(userById.getUserId()).isEqualTo(user.getUserId());
        assertThat(userById.getEmail()).isEqualTo(user.getEmail());
        assertThat(userById.getGivenName()).isEqualTo("Bob");
        assertThat(userById.getGender()).isEqualTo(Gender.MALE);

        user.setGivenName("Barbara");
        user.setGender(Gender.FEMALE);
        user.setNickname("missbarb");
        userDao.update(user);
        
        userById = userDao.findById(user.getUserId());
        
        assertThat(userById).isNotNull();
        assertThat(userById.getUserId()).isEqualTo(user.getUserId());
        assertThat(userById.getEmail()).isEqualTo(user.getEmail());
        assertThat(userById.getGivenName()).isEqualTo("Barbara");
        assertThat(userById.getNickname()).isEqualTo("missbarb");
        assertThat(userById.getGender()).isEqualTo(Gender.FEMALE);
        
        boolean userDeleted = userDao.delete(user);
        assertThat(userDeleted).isTrue();
        
    }
    
    
    @Test
    public void testFindAllUsers() throws Exception {
        UserDao userDao = mapper.userDao();
        
        User user1 = TestUtils.defaultUser();
        User user2 = TestUtils.defaultUser();
        userDao.create(user1, "password01".toCharArray(), new HashSet<Role>());
        userDao.create(user2, "password02".toCharArray(), new HashSet<Role>());
        
        PagingIterable<User> users = userDao.findAll();
        int count = users.all().size();
        assertThat(count >= 2).isTrue();
        
        boolean userDeleted = userDao.delete(user1);
        userDeleted = userDao.delete(user2);
        assertThat(userDeleted).isTrue();
        
    }


    @Test
    public void testGetUserCredentials() throws Exception {
        UserDao userDao = mapper.userDao();

        User user = TestUtils.defaultUser();
        user.setGivenName("Ken");
        user.setGender(Gender.MALE);
        
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        roles.add(Role.ADMIN);
        
        User newUser = userDao.create(user, "ksmith01".toCharArray(), roles);
        
        assertThat(newUser).isNotNull();
        
        UserCredentials credentials = userDao.getCredentials(user.getUserId());
        
        assertThat(credentials).isNotNull();
        assertThat(credentials.getRoles().size()).isEqualTo(2);
        
        boolean userDeleted = userDao.delete(user);
        assertThat(userDeleted).isTrue();
    }
    
    
    @Test
    public void testUpdateUserCredentials() throws Exception {
        UserDao userDao = mapper.userDao();
        
        User user = TestUtils.defaultUser();
        user.setGender(Gender.MALE);
        
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        
        userDao.create(user, "ksmith01".toCharArray(), roles);
        
        User userById = userDao.findById(user.getUserId());
        assertThat(userById).isNotNull();
        
        UserCredentials credentials = userDao.getCredentials(userById.getUserId());
        
        assertThat(credentials).isNotNull();
        assertThat(PasswordUtils.matches("ksmith01".toCharArray(), credentials.getPassword())).isTrue();
        assertThat(credentials.getRoles()).contains(Role.USER);
        
        credentials.setPassword(PasswordUtils.hash("ksmith02".toCharArray()));
        roles.add(Role.ADMIN);
        credentials.setRoles(roles);
        userDao.updateCredentials(credentials);
        
        credentials = userDao.getCredentials(userById.getUserId());
        
        assertThat(PasswordUtils.matches("ksmith02".toCharArray(), credentials.getPassword())).isTrue();
        assertThat(credentials.getRoles().size()).isEqualTo(2);
        assertThat(credentials.getRoles()).contains(Role.ADMIN);
        
        boolean userDeleted = userDao.delete(user);
        assertThat(userDeleted).isTrue();
    }
    
    
    @Test
    public void testDeleteInvalidUser() {
        UserDao userDao = mapper.userDao();
        User user = new User("unknown", "FirstName", "LastName", "username");
        
        assertThatThrownBy(() -> userDao.delete(user))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("error.invalidUserId");
        
    }
    
    @Test
    public void testSaveUserPreferences() throws Exception {
        UserDao userDao = mapper.userDao();
        
        User user = TestUtils.defaultUser();
        userDao.create(user, "jsmith01".toCharArray(), new HashSet<>());

        Map<String,Object> settings = new HashMap<>();
        settings.put("preferredLanguage", "en");
        settings.put("timezone", "America/Los_Angeles");

        UserPreferences preferences = new UserPreferences(user.getUserId(), 
                new JSONObject(settings));

        userDao.savePreferences(preferences);
        
        UserPreferences userPreferences = userDao.getPreferences(user.getUserId());
        assertThat(userPreferences).isNotNull();
        
        JSONObject currentSettings = userPreferences.getSettings();
        assertThat(currentSettings.getString("preferredLanguage")).isEqualTo("en");

        currentSettings.put("timezone", "Asia/Tokyo");
        currentSettings.put("enableNotifications", true);
        userDao.savePreferences(userPreferences);
        
        userPreferences = userDao.getPreferences(user.getUserId());
        
        assertThat(userPreferences.getSettings().getString("timezone")).isEqualTo("Asia/Tokyo");
        assertThat(userPreferences.getSettings().getBoolean("enableNotifications")).isTrue();

        boolean userDeleted = userDao.delete(user);
        assertThat(userDeleted).isTrue();
    }
}
