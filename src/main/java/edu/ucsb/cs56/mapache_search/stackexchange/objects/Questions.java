package edu.ucsb.cs56.mapache_search.stackexchange.objects;

import edu.ucsb.cs56.mapache_search.utils.JSONUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class Questions {
    private static Logger logger = LoggerFactory.getLogger(Questions.class);

    private List<Question> items;

    public List<Question> getItems() {
        return items;
    }

    public Questions setItems(List<Question> items) {
        this.items = items;
        return this;
    }
    public static Questions fromJSON(String json) throws IOException {
        return JSONUtils.deserialize(Questions.class, json);
    }
}
