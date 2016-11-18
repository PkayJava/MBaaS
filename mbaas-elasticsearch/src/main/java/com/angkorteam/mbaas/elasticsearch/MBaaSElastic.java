package com.angkorteam.mbaas.elasticsearch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptService;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.Date;
import java.util.Map;

/**
 * Created by socheat on 9/24/16.
 */
public class MBaaSElastic {

    private static final Gson GSON;

    private static final Type MAP;

    private static final CloseableHttpClient CLIENT;

    static {
        GSON = new GsonBuilder().setPrettyPrinting().create();
        MAP = new TypeToken<Map<String, Object>>() {
        }.getType();
        CLIENT = HttpClients.createDefault();
    }

    public static ElasticResponse createDocument(String server, String index, String type, Map<String, Object> document) throws IOException {
        HttpPost http = new HttpPost(server + "/" + index + "/" + type);
        http.setEntity(new StringEntity(GSON.toJson(document), ContentType.APPLICATION_JSON));
        CloseableHttpResponse response = CLIENT.execute(http);
        HttpEntity httpEntity = response.getEntity();
        String json = IOUtils.toString(httpEntity.getContent(), "UTF-8");
        response.close();
        http.releaseConnection();
        return GSON.fromJson(json, ElasticResponse.class);
    }

    public static ElasticResponse modifyDocument(String server, String index, String type, String id, Map<String, Object> document) throws IOException {
        HttpPut http = new HttpPut(server + "/" + index + "/" + type);
        http.setEntity(new StringEntity(GSON.toJson(document), ContentType.APPLICATION_JSON));
        CloseableHttpResponse response = CLIENT.execute(http);
        HttpEntity httpEntity = response.getEntity();
        String json = IOUtils.toString(httpEntity.getContent(), "UTF-8");
        response.close();
        http.releaseConnection();
        return GSON.fromJson(json, ElasticResponse.class);
    }

    public static ElasticResponse searchDocument(String server, String index, String type, String id) throws UnirestException, IOException {
        org.apache.http.client.methods.HttpGet http = new org.apache.http.client.methods.HttpGet(server + "/" + index + "/" + type + "/" + id);
        CloseableHttpResponse response = CLIENT.execute(http);
        HttpEntity httpEntity = response.getEntity();
        String json = IOUtils.toString(httpEntity.getContent(), "UTF-8");
        response.close();
        http.releaseConnection();
        return GSON.fromJson(json, ElasticResponse.class);
    }

    public static ElasticResponse searchDocument(String server, String index, String type, ElasticSearchRequest query) throws UnirestException, IOException {
        HttpGet http = new HttpGet(server + "/" + index + "/" + type + "/_search");
        http.setEntity(new StringEntity(GSON.toJson(query), ContentType.APPLICATION_JSON));
        CloseableHttpResponse response = CLIENT.execute(http);
        HttpEntity httpEntity = response.getEntity();
        String json = IOUtils.toString(httpEntity.getContent(), "UTF-8");
        response.close();
        http.releaseConnection();
        return GSON.fromJson(json, ElasticResponse.class);
    }


    public static ElasticResponse refresh(String server, String index) throws UnirestException {
        GetRequest request = Unirest.get(server + "/" + index + "/_refresh");
        String json = request.asString().getBody();
        return GSON.fromJson(json, ElasticResponse.class);
    }

    public static ElasticResponse refresh(String server) throws UnirestException {
        GetRequest request = Unirest.get(server + "/_refresh");
        String json = request.asString().getBody();
        return GSON.fromJson(json, ElasticResponse.class);
    }


    public static void main(String args[]) throws IOException, UnirestException {
        String server = "http://192.168.1.103:9200";

        Settings settings = Settings.settingsBuilder()
                .put("cluster.name", "elasticsearch").build();

        TransportClient client = TransportClient.builder().settings(settings).build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.1.103"), 9300));

        

        client.close();
    }

    public static void main1(String args[]) throws IOException, UnirestException {
        String server = "http://192.168.1.103:9200";

        Settings settings = Settings.settingsBuilder()
                .put("cluster.name", "elasticsearch").build();

        TransportClient client = TransportClient.builder().settings(settings).build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.1.103"), 9300));

        String id = null;
        {
            XContentBuilder builder = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("user", "kimchy")
                    .field("postDate", new Date())
                    .field("message", "trying out Elasticsearch")
                    .endObject();

            String json = builder.string();
            System.out.println(json);

            IndexRequestBuilder requestBuilder = client.prepareIndex("twitter", "tweet");
            XContentBuilder source = XContentFactory.jsonBuilder()
                    .startObject()
                    .field("user", "kimchy")
                    .field("postDate", new Date())
                    .field("message", "trying out Elasticsearch")
                    .endObject();
            IndexResponse response = requestBuilder.setSource(source).get();
            id = response.getId();
            System.out.println(response.getId());
        }
        {

            UpdateResponse updateResponse = client.prepareUpdate("twitter", "tweet", id)
                    .setDoc(XContentFactory.jsonBuilder()
                            .startObject()
                            .field("gender", "male")
                            .endObject())
                    .get();
            System.out.println(updateResponse);
        }

        {
            GetResponse response = client.prepareGet("twitter", "tweet", id).get();
            System.out.println(response.getSourceAsString());
        }


        {
            DeleteResponse response = client.prepareDelete("twitter", "tweet", id).get();
            System.out.println(response.isFound());
        }

        client.close();

    }

}