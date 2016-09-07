package com.angkorteam.mbaas.server.wicket;

import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.MultipartBody;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.JSONObject;
import org.springframework.http.MediaType;

import java.io.File;
import java.util.*;

/**
 * Created by socheat on 9/3/16.
 */
public class ApplicationJson {
    public static void main(String[] args) throws UnirestException {
        Gson gson = new Gson();
        HttpRequestWithBody requestBody = Unirest.post("http://demo.ddns.net:9080/api/javascript/hello");
        requestBody.header("content-type", MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> body = new HashMap<>();
        {
            body.put("list_datetime_required", Arrays.asList(DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(new Date())));
        }
        {
            body.put("boolean_enum_required", true);
        }
        {
            body.put("list_string_enum_required", Arrays.asList("people", "student"));
        }
        {
            body.put("datetime_enum_required", "2016-09-02T01:00:00+07:00");
        }
        {
            body.put("list_long_enum_required", Arrays.asList(1, 2, 3));
        }
        {
            body.put("boolean_enum_optional", true);
        }
        {
            body.put("character_enum_optional", 'c');
        }
        {
            body.put("date_enum_optional", "2016-09-02");
        }
        {
            body.put("datetime_enum_optional", "2016-09-02T01:00:00+07:00");
        }
        {
            body.put("double_enum_optional", 1d);
        }
        {
            body.put("long_enum_optional", 1d);
        }
        {
            body.put("time_enum_optional", "01:00:00");
        }
        requestBody.body(gson.toJson(body));
        System.out.println(requestBody.asString());
    }

}
