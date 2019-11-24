package edu.ucsb.cs56.mapache_search.stackexchange.objects;

import edu.ucsb.cs56.mapache_search.utils.JSONUtils;

import java.io.IOException;
import java.util.List;

public class Sites {
    private List<Site> items;

    public List<Site> getItems() {
        return items;
    }

    public void setItems(List<Site> items) {
        this.items = items;
    }

    public static Sites fromJSON(String json) throws IOException {
        return JSONUtils.deserialize(Sites.class,json);
    }
}
