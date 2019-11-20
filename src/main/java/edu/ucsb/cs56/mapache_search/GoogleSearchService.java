package edu.ucsb.cs56.mapache_search;

import java.security.Principal;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import edu.ucsb.cs56.mapache_search.repositories.UserRepository;

/**
 * Service object that wraps the Google Custom Search API
 */
@Service
public class GoogleSearchService implements SearchService {

    private Logger logger = LoggerFactory.getLogger(GoogleSearchService.class);
    private String searchId = "001539284272632380888:kn5n6ubsr7x";

    //We can't have OAuth2AuthenticationToken token as argument, otherwise it'll give us instantiate bean error
    public GoogleSearchService() {
    }

    /*public GoogleSearchService(String apiKey, String searchId) {
        this.apiKey = apiKey;
        logger.info("apiKey=" + apiKey);

        this.searchId = searchId;
        logger.info("searchId=" + searchId);
    }*/

    public String getJSON(String query, String apiKey) {
        logger.info("apiKey=" + apiKey);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.set("ucsb-api-version", "1.0");
        //headers.set("ucsb-api-key", this.apiKey);

        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        String uri = "https://www.googleapis.com/customsearch/v1";
        String params = String.format("?key=%s&cx=%s&q=%s&alt=json",
            apiKey, searchId, query);
        String url = uri + params;
        logger.info("url=" + url);

        String retVal="";
        try {
            ResponseEntity<String> re = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
             MediaType contentType = re.getHeaders().getContentType();
            HttpStatus statusCode = re.getStatusCode();
            retVal = re.getBody();
        } catch (HttpClientErrorException e) {
            retVal = "{\"error\": \"401: Unauthorized\"}";
        }
        logger.info("from GoogleSearchService.getJSON: " + retVal);
        return retVal;
    }

}
