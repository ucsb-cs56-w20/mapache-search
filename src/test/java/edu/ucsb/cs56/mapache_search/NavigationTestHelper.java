package edu.ucsb.cs56.mapache_search;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;
import org.springframework.security.core.Authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;


public class NavigationTestHelper {

    public static void hasNavBar(MockMvc mvc, String url) throws Exception  {
        mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.TEXT_HTML))
        .andExpect(status().isOk())
        .andExpect(xpath("//nav").exists());

    }

    public static void hasFooter(MockMvc mvc, String url) throws Exception  {
        mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.TEXT_HTML))
        .andExpect(status().isOk())
        .andExpect(xpath("//footer").exists());

    }

    public static void hasNavBar(MockMvc mvc, String url, Authentication mockAuthentication) throws Exception  {
        mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.TEXT_HTML)
        .with(authentication(mockAuthentication)))
        .andExpect(status().isOk())
        .andExpect(xpath("//nav").exists());
    }

    public static void hasFooter(MockMvc mvc, String url, Authentication mockAuthentication) throws Exception  {
        mvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.TEXT_HTML)
        .with(authentication(mockAuthentication)))
        .andExpect(status().isOk())
        .andExpect(xpath("//footer").exists());

    }
}
