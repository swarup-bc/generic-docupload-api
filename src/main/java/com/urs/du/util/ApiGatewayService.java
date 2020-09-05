package com.urs.du.util;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public class ApiGatewayService {
	
	public static final Logger logger=Logger.getLogger(ApiGatewayService.class);
	

	public boolean callPostApi(HttpPost post)
			throws IOException, ClientProtocolException, JsonParseException, JsonMappingException {
		logger.info("Processing request:"+post.getURI());
		CloseableHttpClient client = HttpClientBuilder.create().build();
		CloseableHttpResponse response = client.execute(post);
		post.releaseConnection();
		if(response.getStatusLine().getStatusCode()!=200) {
			logger.error("API failed. Error code:"+response.getStatusLine().getStatusCode()+" Message:"+response.getStatusLine());
			return false;
		}
		else{
			logger.info("API invoked successfully. ResponseCode:"+response.getStatusLine().getStatusCode()+" Message:"+response.getStatusLine());
			return true;
		}
		
		
	}

	
}
