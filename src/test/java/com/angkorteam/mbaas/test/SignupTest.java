package com.angkorteam.mbaas.test;

import com.angkorteam.baasbox.sdk.java.BaasBox;
import com.angkorteam.baasbox.sdk.java.Client;
import com.angkorteam.mbaas.request.LoginRequest;
import com.angkorteam.mbaas.request.Request;
import com.angkorteam.mbaas.request.SignupRequest;
import com.angkorteam.mbaas.response.Response;
import com.angkorteam.mbaas.sdk.ClientSDK;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class SignupTest {

    public static void main(String args[]) throws ScriptException {
        ClientSDK clientSDK = null;

        Gson gson;
        {
            GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ");
            gson = builder.create();
        }
        OkHttpClient http = new OkHttpClient();
        OkClient client = new OkClient(http);
        http.setReadTimeout(1, TimeUnit.MINUTES);
        http.setConnectTimeout(1, TimeUnit.MINUTES);
        http.setWriteTimeout(1, TimeUnit.MINUTES);
        {
            RestAdapter.Builder builder = new RestAdapter.Builder();
            builder.setConverter(new GsonConverter(gson));
            builder.setEndpoint("http://pkayjava.ddns.net:7080/api");
            builder.setClient(client);
            RestAdapter restAdapter = builder.build();
            clientSDK = restAdapter.create(ClientSDK.class);
        }

        {
            SignupRequest request = new SignupRequest();
            request.setUsername("admin");
            request.setPassword("123123a");
            request.setAppCode("123461579");
            request.setToken("iOS Token");
//            request.getVisibleByAnonymousUsers().put("test", 17);
//            request.getVisibleByTheUser().put("test2", 17);
//            request.getVisibleByRegisteredUsers().put("test3", 17);
//            request.getVisibleByFriends().put("test4", 17);
//            System.out.println(gson.toJson(clientSDK.signup(request)));
        }

        {
            LoginRequest request = new LoginRequest();
            request.setUsername("admin");
            request.setPassword("123123a");
//            System.out.println(gson.toJson(clientSDK.login(request)));
        }

        String session = "4eeab6d7-db15-481e-812f-840a77486a75";

        clientSDK.createCollection(session, "test11", new Request());

        clientSDK.deleteCollection(session, "test11", new Request());
    }
}
