package com.angkorteam.mbaas.test;

import com.angkorteam.mbaas.request.*;
import com.angkorteam.mbaas.sdk.ClientSDK;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

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
            SecuritySignupRequest request = new SecuritySignupRequest();
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
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername("admin");
            request.setPassword("123123a");
//            System.out.println(gson.toJson(clientSDK.login(request)));
        }

        String session = "c3da78c2-1c15-437b-80bc-ea0126d92183";

        {
            CollectionCreateRequest request = new CollectionCreateRequest();
            request.setName("test11");
            CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
            attribute.setJavaType(String.class.getName());
            attribute.setNullable(true);
            attribute.setName("testingfield");
            request.getAttributes().add(attribute);
            clientSDK.createCollection(session, request);
        }

        {
            CollectionAttributeCreateRequest request = new CollectionAttributeCreateRequest();
            request.setName("hello");
            request.setCollection("test11");
            request.setJavaType(Integer.class.getTypeName());
            clientSDK.createCollectionAttribute(session, request);
        }

        {
            CollectionDeleteRequest request = new CollectionDeleteRequest();
            request.setName("test11");
//            clientSDK.deleteCollection(session, request);
        }
        {
            DocumentCreateRequest request = new DocumentCreateRequest();
            request.setCollection("test11");
            request.getDocument().put("testingfield", "test1");
            request.getDocument().put("hello1", "abc");
            clientSDK.createDocument(session, request);
        }
    }
}
