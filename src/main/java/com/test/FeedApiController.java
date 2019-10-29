package com.test;

import java.io.IOException;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeedApiController {
	
	private static final String INDEX = "dv_feed_index";
	@Autowired
	private RestHighLevelClient restHighLevelClient;
	
	/**
	 * All the list use cases are done as part of the below methods
	 */
	
	
	/**
	 * Use case 1 : 
	 * to list all the events happened on lead 
	 * with id=100 and version is latest
	 * @return
	 */
	@RequestMapping("/useCase1")
	public SearchHit[] useCase1() {
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
		searchRequest.indices(INDEX);
		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse = null;
		try {
			searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return searchResponse != null ? searchResponse.getHits().getHits() : null;

	}
	
	/**
	 * List all the historical events happened 
	 * on Lead record with id=100, 
	 * No related object events
	 * @return
	 */
	@RequestMapping("/useCase2")
	public SearchHit[] useCase2() {
		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		QueryBuilder matchPhrase1 = QueryBuilders.matchPhraseQuery("object.type", "Lead");
		QueryBuilder matchPhrase2 = QueryBuilders.matchPhraseQuery("object.id", "100");
		qb.must(matchPhrase1);
		qb.must(matchPhrase2);

		searchSourceBuilder.query(qb);
		searchRequest.indices(INDEX);
		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse = null;
		try {
			searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return searchResponse != null ? searchResponse.getHits().getHits() : null;
	}

	/**
	 * List all the Appointments on Lead
	 *  with id=100 
	 *  with StartDate greater than today(14/07/19)
	 * @return
	 */
	@RequestMapping("/useCase3")
	public SearchHit[] useCase3() {
		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		
		QueryBuilder matchPhrase1 = QueryBuilders.matchPhraseQuery("oscm:extracontext.object", "Lead");
		QueryBuilder matchPhrase2 = QueryBuilders.matchPhraseQuery("oscm:extracontext.recordId", "100");
		QueryBuilder matchPhrase3 = QueryBuilders.matchPhraseQuery("object.type", "Appointment");
		QueryBuilder qb = QueryBuilders.boolQuery()
										.must(matchPhrase1)
										.must(matchPhrase2)
										.must(matchPhrase3)
										.must(QueryBuilders.nestedQuery("object.osca:attributes",
											QueryBuilders.boolQuery()
											.must(QueryBuilders.termQuery("object.osca:attributes.name", "StartDate"))
											.must(QueryBuilders.rangeQuery("object.osca:attributes.valueDate").gte("14/07/19")),ScoreMode.None));

		searchSourceBuilder.query(qb);
		searchRequest.indices(INDEX);
		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse = null;
		try {
			searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return searchResponse != null ? searchResponse.getHits().getHits() : null;
	}
	
	/**
	 *  List all the Appointments of type "Meeting"
	 *  on Lead with id=100 with
	 *  StartDate greater than today(14/07/19)
	 * @return
	 */
	@RequestMapping("/useCase4")
	public SearchHit[] useCase4() {
		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		
		QueryBuilder matchPhrase1 = QueryBuilders.matchPhraseQuery("oscm:extracontext.object", "Lead");
		QueryBuilder matchPhrase2 = QueryBuilders.matchPhraseQuery("oscm:extracontext.recordId", "100");
		QueryBuilder matchPhrase3 = QueryBuilders.matchPhraseQuery("object.type", "Appointment");
		QueryBuilder qb = QueryBuilders.boolQuery()
				.must(matchPhrase1)
				.must(matchPhrase2)
				.must(matchPhrase3)
				.must(QueryBuilders.nestedQuery("object.osca:attributes",
						QueryBuilders.boolQuery()
						.must(QueryBuilders.termQuery("object.osca:attributes.name", "StartDate"))
						.must(QueryBuilders.termQuery("object.osca:attributes.name", "StartDate")),
						ScoreMode.None))
				.must(QueryBuilders.nestedQuery("object.osca:attributes",
					QueryBuilders.boolQuery()
					.must(QueryBuilders.termQuery("object.osca:attributes.name", "StartDate"))
					.must(QueryBuilders.rangeQuery("object.osca:attributes.valueDate").gte("14/07/19")),ScoreMode.None));
		
		searchSourceBuilder.query(qb);
		searchRequest.indices(INDEX);
		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse = null;
		try {
			searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return searchResponse != null ? searchResponse.getHits().getHits() : null;
	}
	
	/**
	 *  List all the Task on Lead with
	 *  id=100 with DueDate greater
	 *  than or equals today(16/07/19) 
	 * @return
	 */
	@RequestMapping("/useCase5")
	public SearchHit[] useCase5(){

		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		
		QueryBuilder matchPhrase1 = QueryBuilders.matchPhraseQuery("oscm:extracontext.object", "Lead");
		QueryBuilder matchPhrase2 = QueryBuilders.matchPhraseQuery("oscm:extracontext.recordId", "100");
		QueryBuilder matchPhrase3 = QueryBuilders.matchPhraseQuery("object.type", "Task");
		QueryBuilder qb = QueryBuilders.boolQuery()
										.must(matchPhrase1)
										.must(matchPhrase2)
										.must(matchPhrase3)
										.must(QueryBuilders.nestedQuery("object.osca:attributes",
											QueryBuilders.boolQuery()
											.must(QueryBuilders.termQuery("object.osca:attributes.name", "DueDate"))
											.must(QueryBuilders.rangeQuery("object.osca:attributes.valueDate").gte("16/07/19")),ScoreMode.None));

		searchSourceBuilder.query(qb);
		searchRequest.indices(INDEX);
		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse = null;
		try {
			searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return searchResponse != null ? searchResponse.getHits().getHits() : null;
	}
	
	/**
	 * List all the Notes on Lead 
	 * with id=100 with Note 
	 * content contains "Cat"
	 * @return
	 */
	@RequestMapping("/useCase6")
	public SearchHit[] useCase6() {
		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		
		QueryBuilder matchPhrase1 = QueryBuilders.matchPhraseQuery("oscm:extracontext.object", "Lead");
		QueryBuilder matchPhrase2 = QueryBuilders.matchPhraseQuery("oscm:extracontext.recordId", "100");
		QueryBuilder matchPhrase3 = QueryBuilders.matchPhraseQuery("object.type", "Note");
		QueryBuilder qb = QueryBuilders.boolQuery()
										.must(matchPhrase1)
										.must(matchPhrase2)
										.must(matchPhrase3)
										.must(QueryBuilders.nestedQuery("object.osca:attributes",
											QueryBuilders.boolQuery()
											.must(QueryBuilders.termQuery("object.osca:attributes.name", "Note"))
											.must(QueryBuilders.matchQuery("object.osca:attributes.valueString", "Cat")),ScoreMode.None));

		searchSourceBuilder.query(qb);
		searchRequest.indices(INDEX);
		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse = null;
		try {
			searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return searchResponse != null ? searchResponse.getHits().getHits() : null;
	}
	
	/**
	 * List all appointments with Start date >= "14/07/19"
	 * and  all Tasks with DueDate >= "16/07/19" 
	 * for a Lead record 100
	 * @return
	 */
	@RequestMapping("/useCase7")
	public SearchHit[] useCase7() {
		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		
		QueryBuilder matchPhrase1 = QueryBuilders.matchPhraseQuery("oscm:extracontext.object", "Lead");
		QueryBuilder matchPhrase2 = QueryBuilders.matchPhraseQuery("oscm:extracontext.recordId", "100");
		QueryBuilder matchPhrase3 = QueryBuilders.matchPhraseQuery("object.type", "Task");
		QueryBuilder matchPhrase4 = QueryBuilders.matchPhraseQuery("object.type", "Appointment");
		
		QueryBuilder qb = QueryBuilders.boolQuery()
				                        .must(matchPhrase1)
				                        .must(matchPhrase2)
				                        .must(QueryBuilders.boolQuery()
				                        		.should(QueryBuilders.boolQuery()
				                        				.must(matchPhrase3)
				                        				.must(QueryBuilders.nestedQuery("object.osca:attributes",
				    											QueryBuilders.boolQuery()
				    											.must(QueryBuilders.termQuery("object.osca:attributes.name", "DueDate"))
				    											.must(QueryBuilders.rangeQuery("object.osca:attributes.valueDate").gte("16/07/19")),ScoreMode.None))
				                        	    ).should(QueryBuilders.boolQuery()
				                        				.must(matchPhrase4)
				                        				.must(QueryBuilders.nestedQuery("object.osca:attributes",
				                    					QueryBuilders.boolQuery()
				                    					.must(QueryBuilders.termQuery("object.osca:attributes.name", "StartDate"))
				                    					.must(QueryBuilders.rangeQuery("object.osca:attributes.valueDate").gte("14/07/19")),ScoreMode.None))));
		
		searchSourceBuilder.query(qb);
		searchRequest.indices(INDEX);
		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse = null;
		try {
			searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return searchResponse != null ? searchResponse.getHits().getHits() : null;
	}
}
