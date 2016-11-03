package com.angkorteam.mbaas.server.wicket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.RequestBodyEntity;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.http.MediaType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 9/24/16.
 */
public class ElasticSearch {

    public static void main(String[] args) throws UnirestException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        HttpRequestWithBody requestWithBody = Unirest.put("http://192.168.1.103:9200/get-together/new-events/4");
        HttpRequestWithBody requestBody = requestWithBody.header("content-type", MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Late Night with Elasticsearch");
        body.put("date", DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date()));
        RequestBodyEntity requestBodyEntity = requestBody.body(gson.toJson(body));
        System.out.println(gson.toJson(gson.fromJson(requestBodyEntity.asString().getBody(), Object.class)));
    }

}
