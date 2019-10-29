package com.test;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class Test {

	public static void main(String[] args) {
		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		QueryBuilder matchPhrase1 = QueryBuilders.matchPhraseQuery("oscm:extracontext.object", "Lead");
		QueryBuilder matchPhrase2 = QueryBuilders.matchPhraseQuery("oscm:extracontext.recordId", "100");
		QueryBuilder matchPhrase3 = QueryBuilders.matchPhraseQuery("oscm:extracontext.version", "latest");
		qb.must(matchPhrase1);
		qb.must(matchPhrase2);
		qb.must(matchPhrase3);

		searchSourceBuilder.query(qb);
		System.out.println(qb);
	}
}
