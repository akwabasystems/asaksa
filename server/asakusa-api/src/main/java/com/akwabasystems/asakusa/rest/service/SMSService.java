
package com.akwabasystems.asakusa.rest.service;

import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import java.util.Locale;
import java.util.ResourceBundle;
import lombok.extern.java.Log;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@Log
public class SMSService {

    @Value("${twilio.accountId}")
    protected String accountId;
    
    @Value("${twilio.authToken}")
    protected String authToken;
    
    @Value("${twilio.fromPhone}")
    protected String fromPhone;
    
    private TwilioRestClient client;
    
    
    private void initializeRestClient() {
        client = new TwilioRestClient.Builder(accountId, authToken).build();
    }
    
    
    /**
     * Sends the provided message to the specified phone number
     * 
     * @param phone     the phone to which to send the message
     * @param body      the message body
     */
    public void sendMessageToPhone(String phone, String body) {
        if (client == null) {
            initializeRestClient();
        }
        
        new MessageCreator(
            new PhoneNumber(phone),
            new PhoneNumber(fromPhone),
            body)
        .create(client);
    }
    
    
    /**
     * Generates an SMS code with the specified number of digits
     * 
     * @param digits    the number of digits in the code
     * @return an SMS code with the specified number of digits
     */
    public String generateSMSCode(int digits) {
        if (digits <= 0) {
            return "0";
        }

        int code = (int)(Math.floor(Math.random() * Math.pow(10, digits)));
        String formattedCode = Integer.toString(code);
            
        if (formattedCode.length() < digits) {
            formattedCode = StringUtils.leftPad(formattedCode, digits, "0");
        }
            
        return formattedCode;
    }
    

    /**
     * Generates the body for the phone verification SMS message
     * 
     * @param locale    the user's locale
     * @param code      the phone verification code
     * @return the body for the phone verification SMS message
     */
    public String generateBodyForPhoneVerificationCode(Locale locale, String code) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        return String.format(bundle.getString("sms.phoneVerificationCode"), code);
    }
    
    
    @Override
    public String toString() {
        return "SMS Service - version 1.0";
    }
    
}
