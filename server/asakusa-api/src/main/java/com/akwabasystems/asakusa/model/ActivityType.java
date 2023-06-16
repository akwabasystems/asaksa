
package com.akwabasystems.asakusa.model;

import java.util.stream.Stream;


public enum ActivityType {

    NONE,
    
    PROJECT,
    
    TASK,
    
    DISCUSSION,

    DISCUSSION_LIKE;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
    
    public static ActivityType fromString(String type) {
        return Stream.of(ActivityType.values())
                .filter(item -> (item != null && item.name().equalsIgnoreCase(type)))
                .findFirst()
                .orElse(ActivityType.NONE);
    }

}
