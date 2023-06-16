
package com.akwabasystems.asakusa.model;

import java.util.stream.Stream;


public enum MediaType {

    IMAGE,

    TEXT,

    RTF,

    PDF,

    WORD,

    EXCEL,
    
    POWERPOINT,

    AUDIO,

    VIDEO ,

    IOS_APP;
    
    @Override
    public String toString() {
        return name().toLowerCase();
    }
    
    public static MediaType fromString(String name) {
        return Stream.of(MediaType.values())
                .filter(type -> (type != null && type.toString().equalsIgnoreCase(name)))
                .findFirst()
                .orElse(MediaType.TEXT);
    }
    
}
