
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
    
}
