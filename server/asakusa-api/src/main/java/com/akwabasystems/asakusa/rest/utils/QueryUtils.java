
package com.akwabasystems.asakusa.rest.utils;

import java.util.Map;


/**
 * A collection of utility methods used to process incoming HTTP requests and 
 * extract their required query parameters
 */
public class QueryUtils {
    
    public static final String ERROR_MESSAGE = "'%s' is a required field";
    
    
    /**
     * Populates the given map with the value of the specified field, if present
     * 
     * @param map           the source map from which to get the field value
     * @param parameterMap  the destination map to populate
     * @param field         the field to add to the destination map
     */
    public static void populateMapIfPresent(Map<String,Object> map, 
                                            Map<String,Object> parameterMap, 
                                            String field) {
        Object value = map.get(field);
        
        if (value != null) {
            parameterMap.put(field, value);
        }
    }

    
    /**
     * Populates the given map with the value of the specified required field
     * 
     * @param map           the source map from which to get the field value
     * @param parameterMap  the destination map to populate
     * @param field         the field to add to the destination map
     * @throws Exception if the value if null
     */
    public static void populateMapRequired(Map<String,Object> map, 
                                           Map<String,Object> parameterMap, 
                                           String field) throws Exception {
        Object value = map.get(field);
        
        if (value == null) {
            throw new Exception(String.format(ERROR_MESSAGE, field));
        }
        
        parameterMap.put(field, value);
    }
    

    /**
     * Retrieves the value of the specified required field
     * 
     * @param map           the source map from which to read the value
     * @param field         the field whose value to retrieve
     * @return the value of the specified required field
     * @throws Exception if the value is null
     */
    public static Object getValueRequired(Map<String,Object> map,
                                          String field) throws Exception {
        Object value = map.get(field);
        
        if (value == null) {
            throw new Exception(String.format(ERROR_MESSAGE, field));
        }
        
        return value;
    }
    
    
    /**
     * Retrieves the value of the specified field, if present.
     * 
     * @param map           the source map from which to read the value
     * @param field         the field whose value to retrieve
     * @return the value of the specified field if present; otherwise, returns false
     */
    public static Object getValueIfPresent(Map<String,Object> map, String field) {
        return map.containsKey(field) ? map.get(field) : null;
    }
    
    
    /**
     * Retrieves the value of the specified field, or returns the given default value if not present.
     * 
     * @param map           the source map from which to read the value
     * @param field         the field whose value to retrieve
     * @param defaultValue  the default value if the value doesn't exist
     * @return the value of the specified field if present; otherwise, returns the specified default value
     */
    public static Object getValueWithDefault(Map<String,Object> map, String field, String defaultValue) {
        return map.containsKey(field) ? map.get(field) : defaultValue;
    }
    
}
