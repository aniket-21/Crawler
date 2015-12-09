package com.autodesk.crawler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by gadrea on 12/9/2015.
 */
public class RestClient {

    private HttpURLConnection conn;
    private URL url;

    public HttpURLConnection getConnection(){
        return conn;
    }

    public void makeGetCall(String strUrl) throws IOException {
        //Set connection
        strUrl = URLEncoder.encode(strUrl,"UTF-8");
        url = new URL(strUrl);
        conn = (HttpURLConnection)url.openConnection();
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

    public boolean isPostOrPut(String method){
        if(method.equalsIgnoreCase("POST") || method.equalsIgnoreCase("PUT")) return true;
        return false;
    }
}
