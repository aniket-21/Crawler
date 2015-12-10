package com.autodesk.crawler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gadrea on 12/10/2015.
 */
public class SearchCore {

    public String[] getUrls(int noOfUrls) throws IOException {

        String getUrlApi = "http://gosear.ch/_ah/api/gosearch/v1/urls";
        String postData = "{\"limit\": \"" + noOfUrls + "\"}";
        List<String> arrayURLs = new ArrayList();

        RestClient client = new RestClient();
        String res = client.makePostOrPutCall(getUrlApi,"Post",postData);

        //Parse Json and Return URLs
        JsonObject json = client.getJsonObject(res);
        JsonArray array = json.getAsJsonArray("urls");

        for(JsonElement element : array){
            arrayURLs.add(element.getAsString());
        }

        return arrayURLs.toArray(new String[arrayURLs.size()]);
    }


    public void updateURLStatus(String url, String title) throws IOException {
        String getUrlApi = "http://gosear.ch/_ah/api/gosearch/v1/update/status";
        String postData = "{\"is_done\":true,\"title\":\"" + title + "\",\"url\": \"" + url + "\"}";

        RestClient client = new RestClient();
        String res = client.makePostOrPutCall(getUrlApi,"Post",postData);
        System.out.println("Updated URL " +  url);
    }
}
