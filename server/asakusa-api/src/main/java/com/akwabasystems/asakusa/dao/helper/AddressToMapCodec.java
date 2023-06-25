
package com.akwabasystems.asakusa.dao.helper;

import com.akwabasystems.asakusa.model.Address;
import com.datastax.oss.driver.api.core.type.codec.MappingCodec;
import com.datastax.oss.driver.api.core.type.codec.TypeCodecs;
import com.datastax.oss.driver.api.core.type.reflect.GenericType;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;


/**
 * A codec that handles the conversion between the Address type of the User class
 * and the corresponding <code>map<text,text></code> type defined in the
 * Cassandra schema.
 */
public class AddressToMapCodec extends MappingCodec<Map<String,String>, Address>{
    
    public AddressToMapCodec() {
        super(TypeCodecs.mapOf(TypeCodecs.TEXT, TypeCodecs.TEXT), 
                GenericType.of(Address.class));
    }
    
    @Nullable
    @Override
    protected Address innerToOuter(@Nullable Map<String,String> value) {
        if (value == null) {
            return null;
        }
        
        Address address = new Address();
        address.setStreet((String)value.get("street"));
        address.setCity((String)value.get("city"));
        address.setStateOrProvince((String)value.get("state_or_province"));
        address.setPostalCode((String)value.get("postal_code"));
        address.setCountry((String)value.get("country"));
        
        return address;
    }
    
    @Nullable
    @Override
    protected Map<String,String> outerToInner(@Nullable Address address) {
        if (address == null) {
            return null;
        }
        
        Map<String,String> value = new HashMap<>();
        value.put("street", address.getStreet());
        value.put("city", address.getCity());
        value.put("state_or_province", address.getStateOrProvince());
        value.put("postal_code", address.getPostalCode());
        value.put("country", address.getCountry());
        
        return value;
    }
    
}
