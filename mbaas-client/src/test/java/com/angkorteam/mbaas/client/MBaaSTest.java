package com.angkorteam.mbaas.client;

import com.angkorteam.mbaas.plain.request.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.script.ScriptException;
import java.util.Date;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class MBaaSTest {

    public static final String HOST_A = "http://pkayjava.ddns.net:7080/api";

    public static final String HOST_B = "http://172.16.1.42:7080/api";

    public static final String HOST_C = "http://192.168.1.117:7080/api";

    public static void main(String args[]) throws ScriptException {

        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").create();

        MBaaSClient client = new MBaaSClient("1234567890", HOST_C);

        {
            SecuritySignUpRequest request = new SecuritySignUpRequest();
            request.setUsername("admin11");
            request.setPassword("123123a");
            request.setAppCode("123461579");
            request.setToken("iOS Token");
//            System.out.println(gson.toJson(client.signUp(request)));
        }

        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername("admin");
            request.setPassword("admin");
            System.out.println(gson.toJson(client.login(request)));
        }


        String collectionName = "pkayjava12";

        {
            CollectionCreateRequest request = new CollectionCreateRequest();
            request.setCollectionName(collectionName);
            CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
            attribute.setJavaType(String.class.getName());
            attribute.setNullable(true);
            attribute.setName("testingfield");
            request.getAttributes().add(attribute);
//            System.out.println(gson.toJson(client.createCollection(request)));
        }

        {
            CollectionAttributeCreateRequest request = new CollectionAttributeCreateRequest();
            request.setCollectionName(collectionName);
            request.setAttributeName("bbbhellosssssssss");
            request.setJavaType(Boolean.class.getTypeName());
//            System.out.println(gson.toJson(client.createCollectionAttribute(request)));
        }

        {
            CollectionDeleteRequest request = new CollectionDeleteRequest();
            request.setCollectionName("test11");
//            client.deleteCollection(request);
        }
        {
            DocumentCreateRequest request = new DocumentCreateRequest();
            request.getDocument().put("testingfield", "gggggggggggggg");
            request.getDocument().put("bbbhellosssssssss", 1212d);
            System.out.println(gson.toJson(client.createDocument(collectionName, request)));
        }
    }
}
