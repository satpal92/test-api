package com.oracle.testapi.testapi.hello;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class FeedApiDao {
	private static final String INDEX = "testindexv2";

	@Autowired
	private RestHighLevelClient restHighLevelClient;

	@Autowired
	private ObjectMapper objectMapper;

	public void saveBulkFeeds(List<FeedApi> feedApis) {

		BulkRequest bulkRequest = new BulkRequest();

		feedApis.forEach(feedApi -> {

			IndexRequest indexRequest = new IndexRequest(INDEX)
					                    .source(objectMapper.convertValue(feedApi, Map.class));

			//bulkRequest.add(indexRequest);
			try {
				restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		
	}
}
