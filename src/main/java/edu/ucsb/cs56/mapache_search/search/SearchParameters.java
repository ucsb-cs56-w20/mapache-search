package edu.ucsb.cs56.mapache_search.search;

public class SearchParameters {
    private String query;
    private int page = 1;
    private String website = " ";
    private String lastUpdated = " ";
    private boolean sortByUpvotes = false;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getWebsite(){
        return website;
    }

    public void setWebsite(String website){
        this.website = website;
    }

    public String getLastUpdated(){
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated){
        this.lastUpdated = lastUpdated;
    }

    public boolean getSortByUpvotes(){
        return sortByUpvotes;
    }

    public void setSortByUpvotes(boolean sortByUpvotes){
        this.sortByUpvotes = sortByUpvotes;
    }
}
