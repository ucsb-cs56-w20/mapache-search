package edu.ucsb.cs56.mapache_search;

public class SearchParameters {
    private final String query;
    private final int page;

    private SearchParameters(String query, int page) {
        this.query = query;
        this.page = page;
    }

    public Builder editor() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String query = "";
        private int page = 1;

        private Builder(SearchParameters params) {
            query = params.query;
            page = params.page;
        }

        private Builder() {}

        public SearchParameters build() {
            return new SearchParameters(query, page);
        }

        public Builder setQuery(String query) {
            this.query = query;
            return this;
        }

        public Builder setPage(int page) {
            if (page < 1 || page > 10) {
                throw new IllegalArgumentException("Page must be a value between 1 and 10 inclusive");
            }
            this.page = page;
            return this;
        }
    }

    public String getQuery() {
        return query;
    }

    public int getPage() {
        return page;
    }

    public String toURLQuery() {
        return String.format("?query=%s&page=%d", query, page);
    }

}
