package edu.ucsb.cs56.mapache_search;

public class SearchParameters {
    private String query;
    private int page = 1;

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
}
