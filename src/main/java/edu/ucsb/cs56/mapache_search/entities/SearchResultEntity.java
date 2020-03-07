package edu.ucsb.cs56.mapache_search.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SearchResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


    private String link;

    private String htmlTitle;

    private String displayLink;

    private long votecount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }
    
    public String getHtmlTitle() { return htmlTitle; }
    public void setHtmlTitle(String htmlTitle) { this.htmlTitle = htmlTitle; }

    public String getDisplayLink() { return displayLink; }
    public void setDisplayLink(String displayLink) { this.displayLink = displayLink; }

    public Long getVotecount() { return votecount; }
    public void setVotecount(Long votecount) { this.votecount = votecount; }

    @Override
    public String toString() {
        return "SearchResultEntity [url=" + link + "]";
    }

    
}