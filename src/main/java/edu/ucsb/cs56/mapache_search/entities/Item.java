package edu.ucsb.cs56.mapache_search.entities;

/**
 * Item object, represents the item level in SearchResult
 * returned by
 * Google Custom Search JSON API
 * <a href="https://developers.google.com/custom-search/v1/introduction">https://developers.google.com/custom-search/v1/introduction</a>
 */

public class Item {
    private String kind;
    private String title;
    private String htmlTitle;
    private String link;
    private String displayLink;
    private String htmlFormattedUrl;
    private int rating;
    private String snippet;
    private String htmlSnippet;

    public String getKind() {
        return kind;
    }
    public String getTitle() {
        return title;
    }
    public String getHtmlTitle() {
        return htmlTitle;
    }
    public String getLink() {
        return link;
    }
    public String getDisplayLink() {
        return displayLink;
    }
    public String getHtmlFormattedUrl() {
        return htmlFormattedUrl;
    }

    public int getRating(){
        return rating;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setHtmlTitle(String htmlTitle) {
        this.htmlTitle = htmlTitle;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public void setDisplayLink(String displayLink) {
        this.displayLink = displayLink;
    }
    public void setHtmlFormattedUrl(String htmlFormattedUrl) {
        this.htmlFormattedUrl = htmlFormattedUrl;
    }

    public void setRating(int rating){
        this.rating = rating;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getHtmlSnippet() {
        return htmlSnippet;
    }

    public void setHtmlSnippet(String htmlSnippet) {
        this.htmlSnippet = htmlSnippet;
    }
}
