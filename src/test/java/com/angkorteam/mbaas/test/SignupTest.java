package com.angkorteam.mbaas.test;

import com.angkorteam.baasbox.sdk.java.BaasBox;
import com.angkorteam.baasbox.sdk.java.Client;
import com.angkorteam.mbaas.request.LoginRequest;
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
            builder.setEndpoint("http://192.168.1.110:7080/api");
            builder.setClient(client);
            RestAdapter restAdapter = builder.build();
            clientSDK = restAdapter.create(ClientSDK.class);
        }

        {
            LoginRequest request = new LoginRequest();
            request.setUsername("test");
            request.setUsername("password");
            Response response = clientSDK.login(request);
            System.out.println(gson.toJson(response));
        }

        {
            SignupRequest request = new SignupRequest();
            request.setUsername("test211");
            request.setPassword("test211");
            request.setAppCode("12346579");
            request.setToken("iOS Token");
            request.getVisibleByAnonymousUsers().put("test", 17);
            Response response = clientSDK.signup(request);
            System.out.println(gson.toJson(response));
        }
    }
}
