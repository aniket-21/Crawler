package com.autodesk.crawler;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.*;

/**
 * Created by gadrea on 12/9/2015.
 */
public class RestClient {

    private HttpURLConnection conn;
    private URL url;

    public HttpURLConnection getConnection(){
        return conn;
    }

    public String makeGetCall(String strUrl) throws IOException {
        //Set connection
        url = new URL(strUrl);
        conn = (HttpURLConnection)url.openConnection();

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output;
        String response = "";
        System.out.println("Output from Server .... \n");
        while ((output = br.readLine()) != null) {
            System.out.println(output);
            response = response + output;
        }

        return response;
    }

    public String makeGetCall(String strUrl, String postData) throws IOException {
        //Set connection
        url = new URL(strUrl);
        conn = (HttpURLConnection)url.openConnection();

        if(!postData.trim().isEmpty()){
            // Create the form content
            OutputStream out = conn.getOutputStream();
            Writer writer = new OutputStreamWriter(out, "UTF-8");
            writer.write(postData);
            writer.close();
            out.close();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output;
        String response = "";
        System.out.println("Output from Server .... \n");
        while ((output = br.readLine()) != null) {
            System.out.println(output);
            response = response + output;
        }

        return response;
    }

    public void makePostOrPutCall(String strUrl, String method, String[] paramName, String[] paramVal) throws IOException {

        if(!isPostOrPut(method))
            return;

        url = new URL(strUrl);
        conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");

        // Create the form content
        OutputStream out = conn.getOutputStream();
        Writer writer = new OutputStreamWriter(out, "UTF-8");
        for (int i = 0; i < paramName.length; i++) {
            writer.write(paramName[i]);
            writer.write("=");
            writer.write(URLEncoder.encode(paramVal[i], "UTF-8"));
            writer.write("&");
        }
        writer.close();
        out.close();
    }

    public String makePostOrPutCall(String strUrl, String method, String postData) throws IOException {

        if(!isPostOrPut(method))
            return "";

        url = new URL(strUrl);
        conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("content-type","application/json");

        if(!postData.trim().isEmpty()){
            // Create the form content
            OutputStream out = conn.getOutputStream();
            Writer writer = new OutputStreamWriter(out, "UTF-8");
            writer.write(postData);
            writer.close();
            out.close();
        }


        try{
            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED && conn.getResponseCode() != HttpURLConnection.HTTP_OK ) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return "";
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output;
        String response = "";
        System.out.println("Output from Server .... \n");
        while ((output = br.readLine()) != null) {
            System.out.println(output);
            response = response + output;
        }

        return response;
    }



    public boolean isPostOrPut(String method){
        if(method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT")) return true;
        return false;
    }


    public JsonObject getJsonObject(String jsonString){
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(jsonString);
        return  element.getAsJsonObject();
    }
}
