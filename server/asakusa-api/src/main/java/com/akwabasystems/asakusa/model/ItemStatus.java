
package com.akwabasystems.asakusa.model;

import java.util.stream.Stream;


public enum ItemStatus {

    TODO,

    IN_PROGRESS,
    
    PAUSED,
    
    BLOCKED,
    
    IN_TESTING,
    
    DONE,
    
    COMPLETED,
    
    VERIFIED,
    
    UNVERIFIED,
    
    ACTIVE,

    VALID,
    
    INVALID,
    
    INACTIVE,
    
    EXPIRED;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
    
    public static ItemStatus fromString(String status) {
        return Stream.of(ItemStatus.values())
                .filter(item -> (item != null && item.toString().equalsIgnoreCase(status)))
                .findFirst()
                .orElse(ItemStatus.TODO);
    }

}
