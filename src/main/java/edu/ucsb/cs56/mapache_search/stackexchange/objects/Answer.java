package edu.ucsb.cs56.mapache_search.stackexchange.objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Answer {
    private ShallowUser owner;
    @JsonProperty("creation_date")
    private Date creationDate;
    @JsonProperty("last_edit_date")
    private Date lastEditDate;
    private int score;
    private String link;
    private String body;

    public ShallowUser getOwner() {
        return owner;
    }

    public void setOwner(ShallowUser owner) {
        this.owner = owner;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastEditDate() {
        return lastEditDate;
    }

    public void setLastEditDate(Date lastEditDate) {
        this.lastEditDate = lastEditDate;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
