
package com.akwabasystems.asakusa.model;


public enum MembershipType {


    FREE {
        
        @Override
        public String getPlanName() {
            return "asakusa-free";
        }
        
        @Override
        public double getRate() {
            return 0;
        }

    },


    PREMIUM {
        
        @Override
        public String getPlanName() {
            return "asakusa-premium";
        }
        
        @Override
        public double getRate() {
            return 29.99;
        }

    };

    
    @Override
    public String toString() {
        return name().toLowerCase();
    }
    

    /**
     * Returns the membership type whose plan matches the specified plan ID
     *
     * @param plan              the ID of the plan for which to return the membership type
     * @return the account type whose name matches the specified type
     */
    public static MembershipType fromPlan(String plan) {
        return (plan != null && plan.equalsIgnoreCase("PREMIUM")) ?
                MembershipType.PREMIUM : MembershipType.FREE;
    }
    
    
    /**
     * Returns a string representation of the plan name for this Membership type
     *
     * @return a string representation of the plan name for this Membership type
     */
    public abstract String getPlanName();
    
    
    /**
     * Returns the monthly subscription rate for this Membership type
     *
     * @return the monthly subscription rate for this Membership type
     */
    public abstract double getRate();

}
