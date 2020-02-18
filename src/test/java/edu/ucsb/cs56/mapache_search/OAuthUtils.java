package edu.ucsb.cs56.mapache_search;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;


/**
 * Utility methods for testing OAuth protected endpoints.
 * <a href="https://github.com/mark-hoogenboom/spring-boot-oauth-testing">
 * https://github.com/mark-hoogenboom/spring-boot-oauth-testing
 * </a>
 */

public class OAuthUtils {

    public static OAuth2User createOAuth2User(String name, String email) {

        Map<String, Object> authorityAttributes = new HashMap<>();
        authorityAttributes.put("key", "value");

        GrantedAuthority authority = new OAuth2UserAuthority(authorityAttributes);

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "1234567890");
        attributes.put("name", name);
        attributes.put("email", email);
        attributes.put("id", 123456789);


        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        authorities.add(authority);

        return new DefaultOAuth2User(authorities, attributes, "sub");
    }

    public static Authentication getOauthAuthenticationFor(OAuth2User principal) {

        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

        String authorizedClientRegistrationId = "my-oauth-client";

        return new OAuth2AuthenticationToken(principal, authorities, authorizedClientRegistrationId);
    }
}