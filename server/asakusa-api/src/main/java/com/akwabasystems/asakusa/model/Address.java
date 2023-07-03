
package com.akwabasystems.asakusa.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Address {
    
    private String street;
    private String city;
    private String stateOrProvince;
    private String postalCode;
    private String country;
    
    public static Address withDefaults() {
        Address address = new Address();
        address.setStreet("");
        address.setCity("");
        address.setStateOrProvince("CA");
        address.setPostalCode("");
        address.setCountry("USA");
        
        return address;
    }
    
}
