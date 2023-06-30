
package com.akwabasystems.asakusa.dao.helper;

import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodecs;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import org.json.JSONObject;
import org.springframework.lang.Nullable;


/**
 * A codec that handles the conversion between the JSONObject type and its
 * corresponding <code>text</code> type defined in the Cassandra schema.
 */
public class JSONObjectToTextCodec extends MappingCodec<String, JSONObject>{
    
    public JSONObjectToTextCodec() {
        super(TypeCodecs.TEXT, GenericType.of(JSONObject.class));
    }
    
    @Nullable
    @Override
    protected JSONObject innerToOuter(@Nullable String value) {
        if (value == null) {
            return null;
        }
        
        JSONObject json = new JSONObject();
        
        try {
            
            json = new JSONObject(value);
            
        } catch (Exception exception) {
            // No need to rethrow the exception since and empty JSON object
            // will be returned
        }
        
        return json;
    }
    
    @Nullable
    @Override
    protected String outerToInner(@Nullable JSONObject json) {
        return (json == null) ? null : json.toString();
    }
    
}
