
package com.akwabasystems.asakusa.config;

import com.akwabasystems.asakusa.dao.helper.AddressToMapCodec;
import com.akwabasystems.asakusa.dao.helper.JSONObjectToTextCodec;
import com.akwabasystems.asakusa.model.Address;
import com.akwabasystems.asakusa.model.Gender;
import com.akwabasystems.asakusa.model.ItemPriority;
import com.akwabasystems.asakusa.model.ItemStatus;
import com.akwabasystems.asakusa.model.Role;
import com.datastax.oss.driver.api.core.CqlIdentifier;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.type.codec.ExtraTypeCodecs;
import com.datastax.oss.driver.api.core.type.codec.TypeCodec;
import java.net.InetSocketAddress;
import javax.net.ssl.SSLContext;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;


@Configuration
@EnableCassandraRepositories(basePackages = {"com.akwabasystems.asakusa"})
public class CassandraConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(CassandraConfiguration.class);
    
    @Value("${cassandra.contact-point}")
    private String cassandraHost;

    @Value("${cassandra.port}")
    private int cassandraPort;

    @Value("${cassandra.keyspace-name}")
    private String keyspace;

    @Value("${cassandra.local-data-center}")
    private String localDataCenterName;

    @Value("${aws.access-key}")
    private String username;

    @Value("${aws.secret-key}")
    private String password;
    
    public CassandraConfiguration() {}
    
    public CassandraConfiguration(String cassandraHost, 
                                  int port, 
                                  String localDataCenterName, 
                                  String keyspace) {
        super();
        this.cassandraHost = cassandraHost;
        this.cassandraPort = port;
        this.localDataCenterName = localDataCenterName;
        this.keyspace = keyspace;
    }
    
    @Bean
    public CqlIdentifier keyspaceName() {
        return CqlIdentifier.fromCql(getKeyspace());
    }
    
    @Bean
    public CqlSession cqlSession() {
        CqlSession session = null;

        /** 
         * Register the Gender codec to encode and decode the "gender" field 
         * of the User class
         */
        TypeCodec<Gender> genderCodec = ExtraTypeCodecs.enumNamesOf(Gender.class);

        /**
         * Register the MapToAddress codec to encode and decode the Address field
         * of the User class, which is then converted to a <code>map<text,text></code>
         * in the Cassandra schema.
         */
        TypeCodec<Address> addressCodec = new AddressToMapCodec();

        /**
         * Register the JSONObjectToText codec to encode and decode JSONObject 
         * fields to their corresponding <code>text</code> representations 
         * in the Cassandra schema.
         */
        TypeCodec<JSONObject> jsonCodec = new JSONObjectToTextCodec();

        /** 
         * Register the Role codec to encode and decode the "roles" field 
         * of the UserCredentials class
         */
        TypeCodec<Role> roleCodec = ExtraTypeCodecs.enumNamesOf(Role.class);

       /** 
        * Register the codecs to encode and decode the "ItemStatus" 
        * and "ItemPriority" enums
        */
        TypeCodec<ItemStatus> statusCodec = ExtraTypeCodecs.enumNamesOf(ItemStatus.class);
        TypeCodec<ItemPriority> priorityCodec = ExtraTypeCodecs.enumNamesOf(ItemPriority.class);
            
        try {

            session = CqlSession.builder()
                    .addContactPoint(new InetSocketAddress(getCassandraHost(), getCassandraPort()))
                    .withSslContext(SSLContext.getDefault())
                    .withLocalDatacenter(getLocalDataCenterName())
                    .withAuthCredentials(getUsername(), getPassword())
                    .withKeyspace(CqlIdentifier.fromCql(getKeyspace()))
                    .addTypeCodecs(
                        genderCodec, 
                        addressCodec, 
                        jsonCodec, 
                        roleCodec,
                        statusCodec,
                        priorityCodec
                    )
                    .build();

            logger.info(String.format("[Cassandra] Session initialized - keyspace: %s, contact-point: %s%n",
                    session.getKeyspace().get(), getCassandraHost()));

        } catch (Exception ex) {
            logger.info("Exception occurred while creating Cassandra session: " + ex.getMessage());
        }

        return session;
    }
    
    public String getCassandraHost() {
        return cassandraHost;
    }

    public void setCassandraHost(String cassandraHost) {
        this.cassandraHost = cassandraHost;
    }

    public int getCassandraPort() {
        return cassandraPort;
    }

    public void setCassandraPort(int port) {
        this.cassandraPort = port;
    }

    public String getKeyspace() {
        return keyspace;
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }

    public String getLocalDataCenterName() {
        return localDataCenterName;
    }

    public void setLocalDataCenterName(String localDataCenterName) {
        this.localDataCenterName = localDataCenterName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
