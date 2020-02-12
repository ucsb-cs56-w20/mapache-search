package edu.ucsb.cs56.mapache_search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

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

    private static final String SEARCH_ENDPOINT =
        "https://www.googleapis.com/customsearch/v1?key={key}&cx={searchId}&q={query}&alt={outputFormat}&start={start}";

    public String getJSON(SearchParameters params, String apiKey) {
        logger.info("apiKey=" + apiKey);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        int startIndex = ((params.getPage() - 1) * 10) + 1;

        Map<String, String> uriVariables =
            Map.of(
                "key", apiKey,
                "searchId", searchId,
                "query", params.getQuery(),
                "outputFormat", "json",
                "start", Integer.toString(startIndex)
            );

        String retVal = "";
        try {
            ResponseEntity<String> re =
                restTemplate.exchange(SEARCH_ENDPOINT, HttpMethod.GET, entity, String.class, uriVariables);
            retVal = re.getBody();
        } catch (HttpClientErrorException e) {
            retVal = "{\"error\": \"401: Unauthorized\"}";
        }
        logger.info("from GoogleSearchService.getJSON: " + retVal);
        return retVal;
    }

}
