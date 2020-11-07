package io.eluv.json;


import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;



/**
 * Json serialization utility with jackson.
 *
 */

public class Json {

    private final ObjectMapper mapper;

    public Json() {
            this(false, false, false);
    }
    
    public Json(
        boolean failOnUnknown,
        boolean serializeNull,
        boolean indent) {

        mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        mapper.configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
            failOnUnknown);
        mapper.setSerializationInclusion(serializeNull
            ? Include.ALWAYS : Include.NON_NULL);

        mapper.configure(SerializationFeature.INDENT_OUTPUT, indent);

    }

    public <T> T deserialize(byte[] json, Class<T> aClass)
    throws JsonParseException, JsonMappingException, IOException {
        return mapper.readValue(json, aClass);
    }

    public <T> T deserialize(String json, Class<T> aClass)
    throws JsonParseException, JsonMappingException, IOException {
        return mapper.readValue(json, aClass);
    }

    public byte[] serialize(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsBytes(obj);
    }
    
    public ObjectMapper getMapper() {
        return mapper;
    }

} 