package edu.ucsb.cs56.mapache_search.stackexchange.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Site {
    @JsonProperty("site_url")
    private String siteUrl;

    public String getSiteUrl() {
        return siteUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }
}
