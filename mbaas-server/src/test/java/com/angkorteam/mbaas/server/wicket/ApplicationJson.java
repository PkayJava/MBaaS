package com.angkorteam.mbaas.server.wicket;

import com.google.gson.Gson;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import com.mashape.unirest.request.body.MultipartBody;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.http.MediaType;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 9/3/16.
 */
public class ApplicationJson {
    public static void main(String[] args) throws UnirestException {
        Gson gson = new Gson();
        HttpRequestWithBody requestBody = Unirest.post("http://demo.ddns.net:9080/api/javascript/hello");
        requestBody.header("content-type", MediaType.APPLICATION_JSON_VALUE);
        requestBody.body(gson.toJson(""));

        System.out.println(requestBody.asString());
    }

}
