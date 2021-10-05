package com.nashtech.trainingassignment.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nashtech.trainingassignment.DAO.CampaignDAO;
import com.nashtech.trainingassignment.DAO.DatabaseConnector;
import com.nashtech.trainingassignment.model.Campaigns;
import com.nashtech.trainingassignment.utils.HttpRequest;
import com.nashtech.trainingassignment.utils.PageAction;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;

public class CampaignService extends TikTokComponent {
	private CampaignDAO camDAO;
	private final String PATH = "/open_api/v1.2/campaign/get";
	private static final ObjectMapper objMapper = new ObjectMapper();

	public CampaignService(String advertiser_id, String token) {
		super(advertiser_id, token);
		this.camDAO = CampaignDAO.getInstance();
	}

	public ArrayList<Campaigns> dataMapping(String response) {
		JSONObject Jobject = new JSONObject(response);
		JSONObject Jdata = Jobject.getJSONObject("data");
		JSONArray Jarray = Jdata.getJSONArray("list");
		ArrayList<Campaigns> listCampaign = new ArrayList<Campaigns>();
		for (int i = 0; i < Jarray.length(); i++) {
			JSONObject jObj = Jarray.getJSONObject(i);
			try {
				Campaigns campaign = objMapper.readValue(jObj.toString(), Campaigns.class);
				listCampaign.add(campaign);
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
		return listCampaign;
	}
	
	
	public ArrayList<Campaigns> getData() {
		String response = HttpRequest.getApi(advertiser_id, token, PATH, "1");
		ArrayList<Campaigns> listCampaign = dataMapping(response);
		int totalPage = PageAction.getTotalPage(response);
		if (totalPage > 1) {
			for(int i=2; i<= totalPage; i++) {
				String nextResponse = HttpRequest.getApi(advertiser_id, token, PATH, String.valueOf(i));
				listCampaign.addAll(dataMapping(nextResponse));
			}
		}
		return listCampaign;
	}

	public String saveData() {
		return camDAO.saveData(getData());
	}

}