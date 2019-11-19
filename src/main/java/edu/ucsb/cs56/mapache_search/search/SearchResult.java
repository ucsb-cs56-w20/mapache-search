package edu.ucsb.cs56.mapache_search.search;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SearchResult object, represents the top level JSON object returned by the
 * Google Custom Search JSON API <a href=
 * "https://developers.google.com/custom-search/v1/introduction">https://developers.google.com/custom-search/v1/introduction</a>
 */

public class SearchResult {

    private static Logger logger = LoggerFactory.getLogger(SearchResult.class);

    private String kind;
    private List<Item> items;

    public String getKind() {
        return this.kind;
    }

    public List<Item> getItems() {
        return this.items;
    }

    /**
     * Create a CoursePage object from json representation
     * 
     * @param json String of json returned by API endpoint {@code /classes/search}
     * @return a new CoursePage object
     * @throws IOException
     * @see <a href=
     *      "https://developer.ucsb.edu/content/academic-curriculums">https://developer.ucsb.edu/content/academic-curriculums</a>
     */
    public static SearchResult fromJSON(String json) throws IOException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    
            SearchResult sr = objectMapper.readValue(json, SearchResult.class);
            return sr;
        } catch (JsonProcessingException jpe) {
            logger.error("JsonProcessingException:" + jpe);
            return null;
        }
        
    }
}