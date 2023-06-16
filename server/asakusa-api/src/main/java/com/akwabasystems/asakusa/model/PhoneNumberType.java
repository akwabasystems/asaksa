
package com.akwabasystems.asakusa.model;

import java.util.stream.Stream;


public enum PhoneNumberType {

    HOME,
    
    WORK,
    
    MOBILE,
    
    IPHONE,
    
    OTHER;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
    
    public static PhoneNumberType fromString(String type) {        
        return Stream.of(values())
                .filter(item -> (item != null && item.toString().equalsIgnoreCase(type)))
                .findFirst()
                .orElse(PhoneNumberType.OTHER);
    }
    
}
