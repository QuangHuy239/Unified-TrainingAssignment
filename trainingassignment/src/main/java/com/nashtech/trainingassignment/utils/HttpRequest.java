package com.nashtech.trainingassignment.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

public class HttpRequest {
	private HttpRequest() {
	}

	public static HttpRequest getInstance() {
		return SingletonHttpRequest.INSTANCE;
	}

	private static class SingletonHttpRequest {
		private static final HttpRequest INSTANCE = new HttpRequest();
	}

	private static final Logger logger = Logger.getLogger(HttpRequest.class);

	private static String buildUrl(String path) throws URISyntaxException {
		URI uri = new URI("https", "business-api.tiktok.com", path, "", "");
		return uri.toString();
	}

	public String getApi(String advertiser_id, String token, String PATH, String page) {
		OkHttpClient client = new OkHttpClient().newBuilder().build();
		URIBuilder ub;
		URL url = null;
		try {
			ub = new URIBuilder(buildUrl(PATH));
			ub.addParameter("advertiser_id", advertiser_id);
			ub.addParameter("page", page);
			url = ub.build().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		Request request = new Request.Builder().url(url).method("GET", null).addHeader("Access-Token", token).build();
		Response response;
		try {
			response = client.newCall(request).execute();
			logger.info("Get data from " + url + " success");
			return response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("Get data from " + url + " failed");
			return null;
		}
	}
}
