package com.autodesk.crawler;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
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
    private String clusterName = "gosearch";
    private String nodeName = "gosearch-1";
    private String indexName = "devportal";
    private String type = "docs";


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

    public IndexResponse createIndex(String URL, String tag, String level, String text) throws IOException{
        XContentBuilder json = jsonBuilder()
                .startObject()
                .field("url",URL)
                .field("tag",tag)
                .field("level", level)
                .field("text", text)
                .endObject();

        IndexResponse response = client.prepareIndex(indexName, type)
                .setSource(json)
                .get();

        return response;
    }

    public void stopNode(){
        node.close();
    }
}
