package com.oracle.testapi.testapi.hello;

import java.io.IOException;

import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NewFeddApiController {

	private static final String INDEX = "testindexv2";
	@Autowired
	private RestHighLevelClient restHighLevelClient;
	
	@Autowired
	public FeedApiRestService feedApiRestService;
	
	/**
	 * Use case :
	 * List all the events happened on Lead with id=1 (No Child)
	 * @return
	 */
	@RequestMapping("/revisedUseCase1")
	public SearchHit[] useCase1() {

		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		BoolQueryBuilder qb2 = QueryBuilders.boolQuery();
		QueryBuilder matchPhrase1 = QueryBuilders.termQuery("object.id", "1");
		QueryBuilder matchPhrase2 = QueryBuilders.termQuery("object.type", "Lead");

		qb.must(matchPhrase1);
		qb.must(matchPhrase2);
		qb2.filter(qb);
		searchSourceBuilder.query(qb2);
		searchSourceBuilder.sort(SortBuilders.fieldSort("published").order(SortOrder.DESC));
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
	 * Use Cases :
	 * List all the events happened on Lead with id = 1
	 * @return
	 */
	@RequestMapping("/revisedUseCase2")
	public SearchHit[] useCase2() {

		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder qb = QueryBuilders.boolQuery();
		BoolQueryBuilder qb2 = QueryBuilders.boolQuery();
		QueryBuilder matchPhrase1 = QueryBuilders.termQuery("target.id", "1");
		QueryBuilder matchPhrase2 = QueryBuilders.termQuery("target.type", "Lead");

		qb.must(matchPhrase1);
		qb.must(matchPhrase2);
		qb2.filter(qb);
		searchSourceBuilder.query(qb2);
		searchSourceBuilder.sort(SortBuilders.fieldSort("published").order(SortOrder.DESC));
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
	 * All Appointment under lead 1 with start time greater than tme T
	 * @return
	 */
	@RequestMapping("/revisedUseCase3")
	public SearchHit[] useCase3() {
		
		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		//BoolQueryBuilder qb = QueryBuilders.boolQuery();
		BoolQueryBuilder qb2 = QueryBuilders.boolQuery();
		QueryBuilder matchPhrase1 = QueryBuilders.termQuery("target.id", "1");
		QueryBuilder matchPhrase2 = QueryBuilders.termQuery("target.type", "Lead");
		QueryBuilder matchPhrase3 = QueryBuilders.termQuery("object.type", "Appointment");

		QueryBuilder qb = QueryBuilders.boolQuery()
				.must(matchPhrase1)
				.must(matchPhrase2)
				.must(matchPhrase3)
				.must(QueryBuilders.nestedQuery("attributes",
						QueryBuilders.boolQuery()
						.must(QueryBuilders.termQuery("attributes.name", "StartTime"))
						.must(QueryBuilders.rangeQuery("attributes.valueDateTime").gte("1570491827")),
						ScoreMode.None));
		qb2.filter(qb);
		searchSourceBuilder.query(qb2);
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
	 * List all the Appointments of type "Meeting" 
	 * on Lead with id=1 with StartDate greater than today(14/07/19)
	 * @return
	 */
	@RequestMapping("/revisedUseCase4")
	public SearchHit[] useCase4() {

		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		QueryBuilder matchPhrase1 = QueryBuilders.termQuery("target.type", "Lead");
		QueryBuilder matchPhrase2 = QueryBuilders.termQuery("target.id", "1");
		QueryBuilder matchPhrase3 = QueryBuilders.termQuery("object.type", "Appointment");
		BoolQueryBuilder qb2 = QueryBuilders.boolQuery();
		QueryBuilder qb = QueryBuilders.boolQuery().must(matchPhrase1).must(matchPhrase2).must(matchPhrase3)
				.must(QueryBuilders.nestedQuery("allAttributes",
						QueryBuilders.boolQuery().must(QueryBuilders.termQuery("allAttributes.name", "Type"))
								.must(QueryBuilders.termQuery("allAttributes.valueString", "Meeting")),
						ScoreMode.None))
				.must(QueryBuilders.nestedQuery("allAttributes",
						QueryBuilders.boolQuery().must(QueryBuilders.termQuery("allAttributes.name", "StartTime"))
								.must(QueryBuilders.rangeQuery("allAttributes.valueDateTime").gte("1570491827")),
						ScoreMode.None));

		qb2.filter(qb);
		searchSourceBuilder.query(qb2);
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
     * List all the Notes on Lead with id=1
     *  with Note content contains "Cat"
     * @return
     */
	@RequestMapping("/revisedUseCase5")
	public SearchHit[] useCase5() {

		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder qb2 = QueryBuilders.boolQuery();
		
		QueryBuilder matchPhrase1 = QueryBuilders.termQuery("target.type", "Lead");
		QueryBuilder matchPhrase2 = QueryBuilders.termQuery("object.id", "1");
		QueryBuilder matchPhrase3 = QueryBuilders.termQuery("object.type", "Appointment");
		QueryBuilder qb = QueryBuilders.boolQuery()
										.must(matchPhrase1)
										.must(matchPhrase2)
										.must(matchPhrase3)
										.must(QueryBuilders.nestedQuery("changedAttributes",
											QueryBuilders.boolQuery()
											.must(QueryBuilders.termQuery("changedAttributes.name", "Note"))
											.must(QueryBuilders.matchQuery("changedAttributes.valueString", "Cat")),ScoreMode.None));

		qb2.filter(qb);
		searchSourceBuilder.query(qb2);
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
	
	@PostMapping("/feedApi/saveBulkFeeds/{count}")
	public void saveBulkDataForTesting(@PathVariable("count") int count) {
		feedApiRestService.saveBulkFeeds(count);
	}
	
	@RequestMapping("/checkIndexExists/{indexName}")
	public String indexExists(@PathVariable("indexName") String index) {
		GetIndexRequest request = new GetIndexRequest(index);
		boolean isExists=false;
		try {
			isExists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(isExists) {
			return "Elastic instance already present";
		}else {
			return "Elastic instance needs to be created";
		}
	}
	
	public void updateVersion(String objectId, String objectName, String targetId, String targetName) {
		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		QueryBuilder matchPhrase1 = QueryBuilders.termQuery("target.name", "Lead");
		QueryBuilder matchPhrase2 = QueryBuilders.termQuery("target.id", "100");
		QueryBuilder matchPhrase3 = QueryBuilders.termQuery("object.name", "Appointment");
		QueryBuilder matchPhrase4 = QueryBuilders.termQuery("object.id", "100");
		QueryBuilder matchPhrase5 = QueryBuilders.termQuery("extracontext.version", "latest");
		BoolQueryBuilder qb2 = QueryBuilders.boolQuery();

		QueryBuilder qb = QueryBuilders.boolQuery().must(matchPhrase1).must(matchPhrase2).must(matchPhrase3)
				.must(matchPhrase4).must(matchPhrase5);
		qb2.filter(qb);
		searchSourceBuilder.query(qb2);
		searchRequest.indices(INDEX);
		searchRequest.source(searchSourceBuilder);

		SearchResponse searchResponse = null;
		try {
			searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
