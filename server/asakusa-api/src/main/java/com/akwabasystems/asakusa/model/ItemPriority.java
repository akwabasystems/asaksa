
package com.akwabasystems.asakusa.model;


public enum ItemPriority {

    LOW,

    MEDIUM,
    
    HIGH;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
    
    public static ItemPriority fromString(String priority) {
        return (priority != null && priority.equalsIgnoreCase("medium")) ? ItemPriority.MEDIUM : 
               (priority != null && priority.equalsIgnoreCase("high")) ? ItemPriority.HIGH : 
                ItemPriority.LOW;
        
    }

}
