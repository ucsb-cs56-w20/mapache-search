package edu.ucsb.cs56.mapache_search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;
import org.springframework.security.core.Authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

public class BootstrapTestHelper {
    private static Logger logger = LoggerFactory.getLogger(BootstrapTestHelper.class);

    public static final String bootstrapCSSXpath = "//head/link[@rel='stylesheet' and @href='https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css']";

    public static final String[] bootstrapJSurls = { "https://code.jquery.com/jquery-3.3.1.min.js",
            "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js",
            "https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js", };

    public static void bootstrapIsLoaded(MockMvc mvc, String url) throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
                .andExpect(xpath(bootstrapCSSXpath).exists());
        for (String s : bootstrapJSurls) {
            String jsXPath = String.format("//script[@src='%s']", s);
            mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
                    .andExpect(xpath(jsXPath).exists());
        }

    }

    public static void bootstrapIsLoaded(MockMvc mvc, String url, Authentication mockAuthentication) throws Exception {
        logger.info("bootstrapIsLoaded test helper, check for CSS ...");
        mvc.perform(
                MockMvcRequestBuilders.get(url).accept(MediaType.TEXT_HTML).with(authentication(mockAuthentication)))
                .andExpect(status().isOk()).andExpect(xpath(bootstrapCSSXpath).exists());
        logger.info("bootstrapIsLoaded test helper, check for JS ...");
        for (String s : bootstrapJSurls) {
            String jsXPath = String.format("//script[@src='%s']", s);
            mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.TEXT_HTML)
                    .with(authentication(mockAuthentication))).andExpect(status().isOk())
                    .andExpect(xpath(jsXPath).exists());
        }

    }
}