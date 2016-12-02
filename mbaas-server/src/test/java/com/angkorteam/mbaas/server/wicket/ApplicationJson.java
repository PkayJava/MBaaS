package com.angkorteam.mbaas.server.wicket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        HttpRequestWithBody requestBody = Unirest.post("http://demo.ddns.net:9080/api/javascript/hello");
        requestBody = requestBody.header("content-type", MediaType.APPLICATION_JSON_VALUE);
        requestBody = requestBody.header("user-agent", "hello");
        requestBody = requestBody.header("authorization", "hello");
        requestBody = requestBody.header("dob", DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(new Date()));
        HttpRequest httpRequest = requestBody.queryString("game", Arrays.asList(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(new Date()), DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(new Date())));
        httpRequest.queryString("sessionId", UUID.randomUUID().toString());
        Map<String, Object> body = new HashMap<>();
        {
            body.put("list_datetime_required", Arrays.asList(DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.format(new Date())));
            body.put("list_datetime_optional", Arrays.asList(DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.format(new Date())));
        }
        {
            body.put("boolean_enum_required", true);
            body.put("boolean_enum_optional", true);
        }
        {
            body.put("list_string_enum_required", Arrays.asList("people", "student"));
            body.put("list_string_enum_optional", Arrays.asList("student"));
        }
        {
            body.put("datetime_enum_required", "2016-09-02T01:00:00+07:00");
            body.put("datetime_enum_optional", "2016-09-02T01:00:00+07:00");
        }
        {
            body.put("list_long_enum_required", Arrays.asList(1, 2, 3));
            body.put("list_long_enum_optional", Arrays.asList(1, 2, 3));
        }
        {
            body.put("list_boolean_required", Arrays.asList(true, true));
            body.put("list_boolean_optional", Arrays.asList(true, true));
        }
        {
            body.put("list_double_enum_required", Arrays.asList(1, 1.5, 2));
            body.put("list_double_enum_optional", Arrays.asList(1, 1.5, 2));
        }
        {
            body.put("double_required", 3);
            body.put("double_optional", 3);
        }
        {
            body.put("time_enum_required", "01:00:00");
            body.put("time_enum_optional", "01:00:00");
        }
        {
            body.put("long_enum_required", 1);
            body.put("long_enum_optional", 1);
        }
        {
            body.put("list_date_required", Arrays.asList(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(new Date())));
            body.put("list_date_optional", Arrays.asList(DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(new Date())));
        }
        {
            body.put("list_date_enum_required", Arrays.asList("2016-09-02"));
            body.put("list_date_enum_optional", Arrays.asList("2016-09-02"));
        }
        {
            body.put("list_long_required", Arrays.asList(1, 2, 3));
            body.put("list_long_optional", Arrays.asList(1, 2, 3));
        }
        {
            body.put("list_time_required", Arrays.asList(DateFormatUtils.ISO_8601_EXTENDED_TIME_FORMAT.format(new Date())));
            body.put("list_time_optional", Arrays.asList(DateFormatUtils.ISO_8601_EXTENDED_TIME_FORMAT.format(new Date())));
        }
        {
            body.put("date_enum_required", "2016-09-02");
            body.put("date_enum_optional", "2016-09-02");
        }
        {
            body.put("double_enum_required", 1.5);
            body.put("double_enum_optional", 1.5);
        }
        {
            body.put("list_string_required", Arrays.asList("people"));
            body.put("list_string_optional", Arrays.asList("people"));
        }
        {
            body.put("character_enum_required", 'c');
            body.put("character_enum_optional", 'c');
        }
        {
            body.put("list_double_required", Arrays.asList(1, 2.4, 5.6, 6));
            body.put("list_double_optional", Arrays.asList(1, 2.4, 5.6, 6));
        }
        {
            Map<String, Object> person = new HashMap<>();
            person.put("firstName", "Json");
            person.put("lastName", "lastName");
            body.put("list_person_required", Arrays.asList(person));
            body.put("list_person_optional", Arrays.asList(person));
        }
        {
            body.put("string_required", "ssss");
            body.put("string_optional", "ssss");
        }
        {
            body.put("list_datetime_enum_required", Arrays.asList("2016-09-02T01:00:00+07:00"));
            body.put("list_datetime_enum_optional", Arrays.asList("2016-09-02T01:00:00+07:00"));
        }
        {
            body.put("long_required", 1);
            body.put("long_optional", 1);
        }
        {
            body.put("boolean_required", true);
            body.put("boolean_optional", false);
        }
        {
            body.put("list_time_enum_required", Arrays.asList("01:00:00"));
            body.put("list_time_enum_optional", Arrays.asList("01:00:00"));
        }
        {
            {
                Map<String, Object> person = new HashMap<>();
                person.put("firstName", "Json");
                person.put("lastName", "lastName");
                body.put("person_required", person);
            }
            {
                Map<String, Object> person = new HashMap<>();
                person.put("firstName", "Json");
                person.put("lastName", "lastName");
                body.put("person_optional", person);
            }
        }
        {
            body.put("string_enum_required", "student");
            body.put("string_enum_optional", "student");
        }
        {
            body.put("time_required", DateFormatUtils.ISO_8601_EXTENDED_TIME_FORMAT.format(new Date()));
            body.put("time_optional", DateFormatUtils.ISO_8601_EXTENDED_TIME_FORMAT.format(new Date()));
        }
        {
            body.put("datetime_required", DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.format(new Date()));
            body.put("datetime_optional", DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.format(new Date()));
        }
        {
            body.put("list_boolean_enum_required", Arrays.asList(true, false));
            body.put("list_boolean_enum_optional", Arrays.asList(true, false));
        }
        {
            body.put("list_character_enum_required", Arrays.asList('a', 'b', 'c'));
            body.put("list_character_enum_optional", Arrays.asList('a', 'b', 'c'));
        }
        {
            body.put("date_required", DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(new Date()));
            body.put("date_optional", DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(new Date()));
        }
        requestBody.body(gson.toJson(body));
        System.out.println(gson.toJson(gson.fromJson(requestBody.asString().getBody(), Object.class)));
    }

}
