
package com.akwabasystems.asakusa.model;


public enum Gender {

    FEMALE {
        @Override
        public String getDefaultAvatar() {
            return "https://s3-us-west-1.amazonaws.com/akwaba/assets/avatar-female.png";
        }
    },
    

    MALE {
        @Override
        public String getDefaultAvatar() {
            return "https://s3-us-west-1.amazonaws.com/akwaba/assets/avatar-male.png";
        }
    },
    
    UNSPECIFIED {
        @Override
        public String getDefaultAvatar() {
            return "https://s3-us-west-1.amazonaws.com/akwaba/assets/avatar-female.png";
        }
    };

    
    @Override
    public String toString(){
        return name().toLowerCase();
    }
    
    public abstract String getDefaultAvatar();
    
    public static Gender fromString(String gender) {
        return (gender != null && gender.equalsIgnoreCase("male")) ? Gender.MALE : 
               (gender != null && gender.equalsIgnoreCase("female")) ? Gender.FEMALE : Gender.UNSPECIFIED;
    }
    
}
