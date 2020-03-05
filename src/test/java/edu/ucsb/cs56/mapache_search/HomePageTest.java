package edu.ucsb.cs56.mapache_search;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.ucsb.cs56.mapache_search.controllers.SearchController;
import edu.ucsb.cs56.mapache_search.membership.AuthControllerAdvice;
import edu.ucsb.cs56.mapache_search.preview.PreviewProviderService;
import edu.ucsb.cs56.mapache_search.stackexchange.StackExchangeQueryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import edu.ucsb.cs56.mapache_search.controllers.SearchController;
import edu.ucsb.cs56.mapache_search.membership.AuthControllerAdvice;
import edu.ucsb.cs56.mapache_search.repositories.SearchResultRepository;
import edu.ucsb.cs56.mapache_search.repositories.UserRepository;
import edu.ucsb.cs56.mapache_search.repositories.VoteRepository;
import edu.ucsb.cs56.mapache_search.search.SearchService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;


@RunWith(SpringRunner.class)
@WebMvcTest(SearchController.class)
public class HomePageTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthControllerAdvice aca;

    @MockBean
    private ClientRegistrationRepository crr;

    @MockBean
    private SearchService searchService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private SearchResultRepository searchRepository;

    @MockBean
    private VoteRepository voteRepository;

    @MockBean
    private PreviewProviderService pps;

    @MockBean
    private StackExchangeQueryService seqs;

    @Test
    public void getHomePage_ContentType() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    @Test
    public void getHomePage_hasCorrectTitle() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.TEXT_HTML)).andExpect(status().isOk())
                .andExpect(xpath("//title").string("Mapache Search - It's Spanish for \"raccoon\""));
    }

    @Test
    public void getHomePage_SearchInputNotExists() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/").accept(MediaType.TEXT_HTML))
            .andExpect(status().isOk())
            .andExpect(xpath("/html/body/div/form/div/div/input").doesNotExist());
    }

    @Test
    public void getHomePage_BootstrapLoaded() throws Exception {
        BootstrapTestHelper.bootstrapIsLoaded(mvc, "/");
    }

    @Test
    public void getHomePage_hasNavBar() throws Exception {
        NavigationTestHelper.hasNavBar(mvc, "/");
    }

    @Test
    public void getHomePage_hasFooter() throws Exception {
        NavigationTestHelper.hasFooter(mvc, "/");
    }

}