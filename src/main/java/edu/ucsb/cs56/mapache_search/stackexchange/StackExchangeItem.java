package edu.ucsb.cs56.mapache_search.stackexchange;

import edu.ucsb.cs56.mapache_search.entities.Item;
import edu.ucsb.cs56.mapache_search.stackexchange.objects.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;

public class StackExchangeItem {
    private static Logger logger = LoggerFactory.getLogger(StackExchangeItem.class);

    private final Item item;
    private Question question;

    public StackExchangeItem(Item item) {
        this.item = item;
    }


    public Item getItem() {
        return item;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public int getQuestionId() {
        String link = item.getLink();
        try {
            URL url = new URL(link);

            // /questions/{id}
            return Integer.parseInt(url.getPath().split("/")[2]);
        } catch (MalformedURLException e) {
            logger.error("Malformed URL", e);
            return -1;
        }
    }
}
