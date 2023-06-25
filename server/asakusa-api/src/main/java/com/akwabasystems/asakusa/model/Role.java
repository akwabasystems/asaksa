
package com.akwabasystems.asakusa.model;

import java.util.stream.Stream;


public enum Role {
    
    GUEST,

    USER,
    
    ADMIN;

    
    @Override
    public String toString(){
        return name();
    }
    
    public static Role fromString(String role) {
        return Stream.of(values())
                .filter(item -> item.toString().equalsIgnoreCase(role))
                .findFirst()
                .orElse(Role.GUEST);
    }
    
}
