package edu.ucsb.cs56.mapache_search.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JSONUtils {
    private static Logger logger = LoggerFactory.getLogger(JSONUtils.class);

    public static <T> T deserialize(Class<T> cls, String json) throws IOException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            return  objectMapper.readValue(json, cls);
        } catch (JsonProcessingException jpe) {
            logger.error("JsonProcessingException", jpe);
            return null;
        }

    }
}
