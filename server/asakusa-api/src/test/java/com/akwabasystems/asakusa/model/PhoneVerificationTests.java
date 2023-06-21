
package com.akwabasystems.asakusa.model;

import com.akwabasystems.asakusa.BaseTestSuite;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;


public class PhoneVerificationTests extends BaseTestSuite {

    @Test
    public void testPhoneVerificationCodeCreation() {
        PhoneNumberVerification verification = new PhoneNumberVerification();
        verification.setId(UUID.randomUUID());
        verification.setPhoneNumber("+18005551212");
        verification.setCode("202110");
        
        assertThat(verification.getPhoneNumber()).isEqualTo("+18005551212");
        assertThat(verification.getCode()).isEqualTo("202110");
    }

    
    @Test
    public void testPhoneVerificationCodequality() {
        PhoneNumberVerification verification = new PhoneNumberVerification();
        verification.setId(UUID.randomUUID());
        verification.setPhoneNumber("+18005551212");
        verification.setCode("202110");
        
        assertThat(verification.equals(verification)).isTrue();
        assertThat(verification.equals(new Object())).isFalse();
        
        PhoneNumberVerification anotherVerification = new PhoneNumberVerification();
        
        assertThat(verification.equals(anotherVerification)).isFalse();
        
        anotherVerification.setId(verification.getId());
        anotherVerification.setPhoneNumber(verification.getPhoneNumber());
        anotherVerification.setCode(verification.getCode());

        assertThat(verification.equals(anotherVerification)).isTrue();
    }
    
    
    @Test
    public void testPhoneVerificationHashCodeEquality() {
        PhoneNumberVerification verification = new PhoneNumberVerification();
        verification.setId(UUID.randomUUID());
        verification.setPhoneNumber("+18005551212");
        verification.setCode("202110");
        
        PhoneNumberVerification anotherVerification = new PhoneNumberVerification();
        
        assertThat(verification.equals(anotherVerification)).isFalse();
        assertThat(verification.hashCode() == anotherVerification.hashCode()).isFalse();
        
        anotherVerification.setId(verification.getId());
        anotherVerification.setPhoneNumber(verification.getPhoneNumber());
        anotherVerification.setCode(verification.getCode());
        
        assertThat(verification.hashCode() == anotherVerification.hashCode()).isTrue();
    }
    
}
