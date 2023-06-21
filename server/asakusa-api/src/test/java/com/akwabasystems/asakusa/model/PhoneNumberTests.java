
package com.akwabasystems.asakusa.model;

import com.akwabasystems.asakusa.BaseTestSuite;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;


public class PhoneNumberTests extends BaseTestSuite {

    @Test
    public void testPhoneNumberCreation() {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setId(UUID.randomUUID());
        phoneNumber.setCountryCode("1");
        phoneNumber.setNumber("5551212");
        
        assertThat(phoneNumber.getType()).isEqualTo(PhoneNumberType.MOBILE);
        assertThat(phoneNumber.getStatus()).isEqualTo(ItemStatus.UNVERIFIED);
        assertThat(phoneNumber.getCountryCode()).isEqualTo("1");
        assertThat(phoneNumber.getNumber()).isEqualTo("5551212");
    }

    
    @Test
    public void testPhoneNumberEquality() {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setId(UUID.randomUUID());
        phoneNumber.setCountryCode("1");
        phoneNumber.setNumber("5551212");
        
        assertThat(phoneNumber.equals(phoneNumber)).isTrue();
        assertThat(phoneNumber.equals(new Object())).isFalse();
        
        PhoneNumber anotherNumber = new PhoneNumber();
        assertThat(phoneNumber.equals(anotherNumber)).isFalse();
        
        anotherNumber.setId(phoneNumber.getId());
        anotherNumber.setCountryCode(phoneNumber.getCountryCode());
        anotherNumber.setNumber(phoneNumber.getNumber());
        assertThat(anotherNumber.equals(phoneNumber)).isTrue();
    }
    
    
    @Test
    public void testPhoneNumberHashCode() {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setId(UUID.randomUUID());
        phoneNumber.setCountryCode("1");
        phoneNumber.setNumber("5551212");
        
        PhoneNumber anotherNumber = new PhoneNumber();
        
        assertThat(phoneNumber.hashCode() == anotherNumber.hashCode()).isFalse();
        
        anotherNumber.setId(phoneNumber.getId());
        anotherNumber.setCountryCode(phoneNumber.getCountryCode());
        anotherNumber.setNumber(phoneNumber.getNumber());
        
        assertThat(phoneNumber.hashCode() == anotherNumber.hashCode()).isTrue();
    }
    
}
