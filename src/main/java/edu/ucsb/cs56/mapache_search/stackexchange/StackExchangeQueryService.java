package edu.ucsb.cs56.mapache_search.stackexchange;

import edu.ucsb.cs56.mapache_search.stackexchange.objects.Questions;

import java.util.List;

public interface StackExchangeQueryService {
    Questions findQuestions(String site, List<Integer> questionIds);
}
