package com.autodesk.crawler;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.node.Node;
import static org.elasticsearch.common.xcontent.XContentFactory.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.elasticsearch.node.NodeBuilder.*;


/**
 * Created by gadrea on 12/9/2015.
 */
public class ElasticSearch {

    private Node node;
    private Client client;

    //Move these to config file later
    private final String hostName = "107.167.191.105";
    private final String port = "9200";
    private final String clusterName = "gosearch";
    private final String indexName = "devportal";
    private final String type = "docs";

    private RestClient rest;
    private String urlGoSearch;


    ElasticSearch(){
        //Rest Client
        rest = new RestClient();
        urlGoSearch = "http://" + hostName + ":" + port + "/" + indexName + "/" + type;
    }


    public Client createNodeClient(){
        //Node
       node = nodeBuilder()
               .client(true)
               .clusterName(clusterName).node();

        client =  node.client();
        return client;
    }

    public Client createTransportClient(String hostname) throws UnknownHostException{
        Settings settings = Settings
                .settingsBuilder()
                .put("cluster.name", clusterName)
                .build();
        client = TransportClient.builder().settings(settings).build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(hostname), 9300));
        return client;
    }

    public void createIndex(String URL, String tag, int level, String text) throws IOException{

        if(text.isEmpty())
            return;

        XContentBuilder json = jsonBuilder()
                .startObject()
                .field("url",URL)
                .field("tag",tag)
                .field("level", level)
                .field("text", text)
                .endObject();

        //Using Rest API
        rest.makePostOrPutCall(urlGoSearch,"Post",json.string());
    }

    private JsonArray getResultsByURL (String URL) throws IOException{
        //make get call
        String res = rest.makeGetCall(urlGoSearch + "/_search?size=5000&q=url:\"" + URL + "\"");
        JsonObject jsonRes = rest.getJsonObject(res);
        jsonRes = jsonRes.getAsJsonObject("hits");
        return jsonRes.getAsJsonArray("hits");
    }

    public void stopNode(){
        node.close();
    }



    public void bulkDeleteByURL(String URL) throws IOException {
        String postData = "";

        //Get data based on URL to be deleted
        JsonArray jsonArray = getResultsByURL(URL);
        JsonObject jsonObj = null;
        for(JsonElement element : jsonArray){
            //System.out.println(element.getAsJsonObject().get("_id").getAsString());
            postData = postData + "{\"delete\":{\"_index\":\"" + indexName + "\",\"_type\":\"" + type + "\",\"_id\":\"" + element.getAsJsonObject().get("_id").getAsString() + "\"}}";
            postData = postData + "\n";
        }

        //Using Rest API
        rest.makePostOrPutCall(urlGoSearch + "/_bulk","Post",postData);
    }


}
