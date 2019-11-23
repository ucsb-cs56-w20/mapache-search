package edu.ucsb.cs56.mapache_search.stackexchange.objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class Questions {
    private static Logger logger = LoggerFactory.getLogger(Questions.class);

    private List<Question> items;

    public List<Question> getItems() {
        return items;
    }

    public Questions setItems(List<Question> items) {
        this.items = items;
        return this;
    }
    public static Questions fromJSON(String json) throws IOException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            return  objectMapper.readValue(json, Questions.class);
        } catch (JsonProcessingException jpe) {
            logger.error("JsonProcessingException", jpe);
            return null;
        }

    }
}
