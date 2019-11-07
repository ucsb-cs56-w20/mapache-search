package edu.ucsb.cs56.mapache_search.search;

import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.Data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Item object, represents the item level in SearchResult 
 * returned by 
 * Google Custom Search JSON API
 * <a href="https://developers.google.com/custom-search/v1/introduction">https://developers.google.com/custom-search/v1/introduction</a>
 */

@Data
public class Item {
    private String kind;
    private String title;
    private String htmlTitle;
    public String getKind() {
        return kind;
    }
    public String getTitle() {
        return title;
    }
    public String getHtmlTitle() {
        return htmlTitle;
    }
}