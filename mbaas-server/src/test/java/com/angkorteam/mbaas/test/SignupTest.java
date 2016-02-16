package com.angkorteam.mbaas.test;

import javax.script.ScriptException;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class SignupTest {

    public static final String HOST_A = "http://pkayjava.ddns.net:7080/api";

    public static final String HOST_B = "http://172.16.1.42:7080/api";

    public static final String HOST_C = "http://192.168.1.117:7080/api";

    public static void main(String args[]) throws ScriptException {
//        ClientSDK clientSDK = null;
//
//        Gson gson;
//        {
//            GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ");
//            gson = builder.create();
//        }
//        OkHttpClient http = new OkHttpClient();
//        OkClient client = new OkClient(http);
//        http.setReadTimeout(1, TimeUnit.MINUTES);
//        http.setConnectTimeout(1, TimeUnit.MINUTES);
//        http.setWriteTimeout(1, TimeUnit.MINUTES);
//        {
//            RestAdapter.Builder builder = new RestAdapter.Builder();
//            builder.setConverter(new GsonConverter(gson));
//            builder.setEndpoint(HOST_C);
//            builder.setClient(client);
//            RestAdapter restAdapter = builder.build();
//            clientSDK = restAdapter.create(ClientSDK.class);
//        }
//
//        {
//            SecuritySignUpRequest request = new SecuritySignUpRequest();
//            request.setUsername("admin1");
//            request.setPassword("123123a");
//            request.setAppCode("123461579");
//            request.setToken("iOS Token");
////            request.getVisibleByAnonymousUsers().put("test", 17);
////            request.getVisibleByTheUser().put("test2", 17);
////            request.getVisibleByRegisteredUsers().put("test3", 17);
////            request.getVisibleByFriends().put("test4", 17);
////            System.out.println(gson.toJson(clientSDK.signUp(request)));
//        }
//
//        {
//            SecurityLoginRequest request = new SecurityLoginRequest();
//            request.setUsername("admin1");
//            request.setPassword("123123a");
////            System.out.println(gson.toJson(clientSDK.login(request)));
//        }
//
//        String session = "dd599d03-807f-4098-8816-524da59cb5cd";
//
//        String collectionName = "pkayjava1";
//
//        {
//            CollectionCreateRequest request = new CollectionCreateRequest();
//            request.setName(collectionName);
//            CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
//            attribute.setJavaType(String.class.getName());
//            attribute.setNullable(true);
//            attribute.setName("testingfield");
//            request.getAttributes().add(attribute);
//            System.out.println(gson.toJson(clientSDK.createCollection(session, request)));
//        }
//
//        {
//            CollectionAttributeCreateRequest request = new CollectionAttributeCreateRequest();
//            request.setCollection(collectionName);
//            request.setName("hello");
//            request.setJavaType(Integer.class.getTypeName());
//            System.out.println(gson.toJson(clientSDK.createCollectionAttribute(session, request)));
//        }
//
//        {
//            CollectionDeleteRequest request = new CollectionDeleteRequest();
//            request.setName("test11");
////            clientSDK.deleteCollection(session, request);
//        }
//        {
//            DocumentCreateRequest request = new DocumentCreateRequest();
//            request.getDocument().put("testingfield", "test1");
//            request.getDocument().put("hello", "abc");
//            System.out.println(gson.toJson(clientSDK.createDocument(session, collectionName, request)));
//        }
    }
}
