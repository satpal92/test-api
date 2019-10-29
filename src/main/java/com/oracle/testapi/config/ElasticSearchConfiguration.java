package com.oracle.testapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

@Configuration
public class ElasticSearchConfiguration extends AbstractFactoryBean {

	private static final Logger LOG = LoggerFactory.getLogger(ElasticSearchConfiguration.class);
	@Value("${spring.data.elasticsearch.cluster-nodes}")
	private String clusterNodes;
	@Value("${spring.data.elasticsearch.cluster-name}")
	private String clusterName;
	private RestHighLevelClient restHighLevelClient;

	@Override
	public void destroy() {
		try {
			if (restHighLevelClient != null) {
				restHighLevelClient.close();
			}
		} catch (final Exception e) {
			LOG.error("Error closing ElasticSearch client: ", e);
		}
	}

	@Override
	public Class getObjectType() {
		return RestHighLevelClient.class;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	@Override
	protected Object createInstance() throws Exception {
		return buildClient();
	}

	private RestHighLevelClient buildClient() {
		try {
			restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost("blr2433616.in.oracle.com", 9200, "http")));
			//restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return restHighLevelClient;
	}
}
