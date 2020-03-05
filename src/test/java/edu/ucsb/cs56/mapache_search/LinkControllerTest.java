package edu.ucsb.cs56.mapache_search;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.security.Principal;

import edu.ucsb.cs56.mapache_search.controllers.LinkController;
import edu.ucsb.cs56.mapache_search.membership.AuthControllerAdvice;
import edu.ucsb.cs56.mapache_search.preview.PreviewProviderService;
import edu.ucsb.cs56.mapache_search.repositories.SearchResultRepository;
import edu.ucsb.cs56.mapache_search.repositories.UserRepository;
import edu.ucsb.cs56.mapache_search.repositories.VoteRepository;
import edu.ucsb.cs56.mapache_search.search.SearchService;
import edu.ucsb.cs56.mapache_search.stackexchange.StackExchangeQueryService;

import org.aspectj.lang.annotation.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;



@RunWith(SpringRunner.class)
@WebMvcTest(LinkController.class)
public class LinkControllerTest {

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
    public void testLinkControllerRedirect() throws Exception {
        OAuth2User principal = OAuthUtils.createOAuth2User(
                "Randall Munroe", "randall@companyname.website");

        mvc.perform(MockMvcRequestBuilders.get("/link")
            .param("url", "https://www.google.com")
            .with(authentication(OAuthUtils.getOauthAuthenticationFor(principal))))
            .andExpect(redirectedUrl("https://www.google.com"));
         
    }

}