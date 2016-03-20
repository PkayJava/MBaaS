//package com.angkorteam.mbaas.client;
//
//import com.angkorteam.mbaas.plain.enums.CollectionPermissionEnum;
//import com.angkorteam.mbaas.plain.request.collection.CollectionCreateRequest;
//import com.angkorteam.mbaas.plain.request.collection.CollectionDeleteRequest;
//import com.angkorteam.mbaas.plain.request.collection.CollectionPermissionUsernameRequest;
//import com.angkorteam.mbaas.plain.request.document.*;
//import com.angkorteam.mbaas.plain.request.security.SecurityLoginRequest;
//import com.angkorteam.mbaas.plain.request.security.SecuritySignUpRequest;
//import com.angkorteam.mbaas.plain.response.collection.CollectionCreateResponse;
//import com.angkorteam.mbaas.plain.response.collection.CollectionDeleteResponse;
//import com.angkorteam.mbaas.plain.response.collection.CollectionPermissionUsernameResponse;
//import com.angkorteam.mbaas.plain.response.document.*;
//import com.angkorteam.mbaas.plain.response.security.SecurityLoginResponse;
//import com.angkorteam.mbaas.plain.response.security.SecuritySignUpResponse;
//import org.apache.commons.lang3.RandomStringUtils;
//import org.junit.Assert;
//import org.junit.Test;
//
//import java.util.Date;
//
///**
// * Created by Khauv Socheat on 2/4/2016.
// */
//public class DocumentControllerTest extends BaseTest {
//
//    @Test
//    public void documentCreateTestA() {
//        String login = "admin";
//        String password = "admin";
//
//        {
//            SecurityLoginRequest request = new SecurityLoginRequest();
//            request.setUsername(login);
//            request.setPassword(password);
//            SecurityLoginResponse response = client.securityLogin(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        String collectionName = "tmp_user" + RandomStringUtils.randomAlphabetic(5).toLowerCase();
//
//        {
//            CollectionCreateRequest request = new CollectionCreateRequest();
//            request.setCollectionName(collectionName);
//            {
//                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
//                attribute.setName("first_name");
//                attribute.setNullable(false);
//                attribute.setJavaType(String.class.getName());
//                request.getAttributes().add(attribute);
//            }
//            {
//                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
//                attribute.setName("join_date");
//                attribute.setNullable(true);
//                attribute.setJavaType(Date.class.getName());
//                request.getAttributes().add(attribute);
//            }
//            CollectionCreateResponse response = client.collectionCreate(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        {
//            DocumentCreateRequest request = new DocumentCreateRequest();
//            request.getDocument().put("first_name", "Socheat KHAUV");
//            request.getDocument().put("join_date", new Date());
//            DocumentCreateResponse response = client.documentCreate(collectionName, request);
//            System.out.println(gson.toJson(response));
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        {
//            CollectionDeleteRequest request = new CollectionDeleteRequest();
//            request.setCollectionName(collectionName);
//            CollectionDeleteResponse response = client.collectionDelete(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//    }
//
//    @Test
//    public void documentCreateTestB() {
//        String login = "admin";
//        String password = "admin";
//
//        String userALogin = "userA" + RandomStringUtils.randomAlphabetic(5).toLowerCase();
//        String userAPassword = "userA" + RandomStringUtils.randomAlphabetic(5).toLowerCase();
//
//        {
//            SecuritySignUpRequest request = new SecuritySignUpRequest();
//            request.setUsername(userALogin);
//            request.setPassword(userAPassword);
//            SecuritySignUpResponse response = client.securitySignUp(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        {
//            SecurityLoginRequest request = new SecurityLoginRequest();
//            request.setUsername(login);
//            request.setPassword(password);
//            SecurityLoginResponse response = client.securityLogin(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        String collectionName = "tmp_user" + RandomStringUtils.randomAlphabetic(5).toLowerCase();
//
//        {
//            CollectionCreateRequest request = new CollectionCreateRequest();
//            request.setCollectionName(collectionName);
//            {
//                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
//                attribute.setName("first_name");
//                attribute.setNullable(false);
//                attribute.setJavaType(String.class.getName());
//                request.getAttributes().add(attribute);
//            }
//            {
//                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
//                attribute.setName("join_date");
//                attribute.setNullable(true);
//                attribute.setJavaType(Date.class.getName());
//                request.getAttributes().add(attribute);
//            }
//            CollectionCreateResponse response = client.collectionCreate(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        {
//            CollectionPermissionUsernameRequest request = new CollectionPermissionUsernameRequest();
//            request.setCollectionName(collectionName);
//            request.setUsername(userALogin);
//            request.getActions().add(CollectionPermissionEnum.Create.getLiteral());
//            request.getActions().add(CollectionPermissionEnum.Delete.getLiteral());
//            CollectionPermissionUsernameResponse response = client.collectionPermissionGrantUsername(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        {
//            SecurityLoginRequest request = new SecurityLoginRequest();
//            request.setUsername(userALogin);
//            request.setPassword(userAPassword);
//            SecurityLoginResponse response = client.securityLogin(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        {
//            DocumentCreateRequest request = new DocumentCreateRequest();
//            request.getDocument().put("first_name", "Socheat KHAUV");
//            request.getDocument().put("join_date", new Date());
//            DocumentCreateResponse response = client.documentCreate(collectionName, request);
//            System.out.println(gson.toJson(response));
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        {
//            CollectionDeleteRequest request = new CollectionDeleteRequest();
//            request.setCollectionName(collectionName);
//            CollectionDeleteResponse response = client.collectionDelete(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//    }
//
//    @Test
//    public void documentCountTestA() {
//        String login = "admin";
//        String password = "admin";
//
//        {
//            SecurityLoginRequest request = new SecurityLoginRequest();
//            request.setUsername(login);
//            request.setPassword(password);
//            SecurityLoginResponse response = client.securityLogin(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        String collectionName = "tmp_user" + RandomStringUtils.randomAlphabetic(5).toLowerCase();
//
//        {
//            CollectionCreateRequest request = new CollectionCreateRequest();
//            request.setCollectionName(collectionName);
//            {
//                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
//                attribute.setName("first_name");
//                attribute.setNullable(false);
//                attribute.setJavaType(String.class.getName());
//                request.getAttributes().add(attribute);
//            }
//            {
//                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
//                attribute.setName("join_date");
//                attribute.setNullable(true);
//                attribute.setJavaType(Date.class.getName());
//                request.getAttributes().add(attribute);
//            }
//            CollectionCreateResponse response = client.collectionCreate(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        {
//            DocumentCreateRequest request = new DocumentCreateRequest();
//            request.getDocument().put("first_name", "Socheat KHAUV");
//            request.getDocument().put("join_date", new Date());
//            DocumentCreateResponse response = client.documentCreate(collectionName, request);
//            System.out.println(gson.toJson(response));
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        {
//            DocumentCountRequest request = new DocumentCountRequest();
//            DocumentCountResponse response = client.documentCount(collectionName, request);
//            System.out.println(gson.toJson(response));
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        {
//            CollectionDeleteRequest request = new CollectionDeleteRequest();
//            request.setCollectionName(collectionName);
//            CollectionDeleteResponse response = client.collectionDelete(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//    }
//
//    @Test
//    public void documentModifyTestA() {
//        String login = "admin";
//        String password = "admin";
//
//        {
//            SecurityLoginRequest request = new SecurityLoginRequest();
//            request.setUsername(login);
//            request.setPassword(password);
//            SecurityLoginResponse response = client.securityLogin(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        String collectionName = "tmp_user" + RandomStringUtils.randomAlphabetic(5).toLowerCase();
//
//        {
//            CollectionCreateRequest request = new CollectionCreateRequest();
//            request.setCollectionName(collectionName);
//            {
//                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
//                attribute.setName("first_name");
//                attribute.setNullable(false);
//                attribute.setJavaType(String.class.getName());
//                request.getAttributes().add(attribute);
//            }
//            {
//                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
//                attribute.setName("join_date");
//                attribute.setNullable(true);
//                attribute.setJavaType(Date.class.getName());
//                request.getAttributes().add(attribute);
//            }
//            CollectionCreateResponse response = client.collectionCreate(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        String documentId = null;
//
//        {
//            DocumentCreateRequest request = new DocumentCreateRequest();
//            request.getDocument().put("first_name", "Socheat KHAUV");
//            request.getDocument().put("join_date", new Date());
//            DocumentCreateResponse response = client.documentCreate(collectionName, request);
//            System.out.println(gson.toJson(response));
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//            documentId = response.getData().getDocumentId();
//        }
//
//        {
//            DocumentModifyRequest request = new DocumentModifyRequest();
//            request.getDocument().put("first_name", "PkayJava");
//            DocumentModifyResponse response = client.documentModify(collectionName, documentId, request);
//            System.out.println(gson.toJson(response));
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        {
//            CollectionDeleteRequest request = new CollectionDeleteRequest();
//            request.setCollectionName(collectionName);
//            CollectionDeleteResponse response = client.collectionDelete(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//    }
//
//    @Test
//    public void documentDeleteTestA() {
//        String login = "admin";
//        String password = "admin";
//
//        {
//            SecurityLoginRequest request = new SecurityLoginRequest();
//            request.setUsername(login);
//            request.setPassword(password);
//            SecurityLoginResponse response = client.securityLogin(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        String collectionName = "tmp_user" + RandomStringUtils.randomAlphabetic(5).toLowerCase();
//
//        {
//            CollectionCreateRequest request = new CollectionCreateRequest();
//            request.setCollectionName(collectionName);
//            {
//                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
//                attribute.setName("first_name");
//                attribute.setNullable(false);
//                attribute.setJavaType(String.class.getName());
//                request.getAttributes().add(attribute);
//            }
//            {
//                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
//                attribute.setName("join_date");
//                attribute.setNullable(true);
//                attribute.setJavaType(Date.class.getName());
//                request.getAttributes().add(attribute);
//            }
//            CollectionCreateResponse response = client.collectionCreate(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        String documentId = null;
//
//        {
//            DocumentCreateRequest request = new DocumentCreateRequest();
//            request.getDocument().put("first_name", "Socheat KHAUV");
//            request.getDocument().put("join_date", new Date());
//            DocumentCreateResponse response = client.documentCreate(collectionName, request);
//            System.out.println(gson.toJson(response));
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//            documentId = response.getData().getDocumentId();
//        }
//
//        {
//            DocumentDeleteRequest request = new DocumentDeleteRequest();
//            DocumentDeleteResponse response = client.documentDelete(collectionName, documentId, request);
//            System.out.println(gson.toJson(response));
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        {
//            CollectionDeleteRequest request = new CollectionDeleteRequest();
//            request.setCollectionName(collectionName);
//            CollectionDeleteResponse response = client.collectionDelete(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//    }
//
//    @Test
//    public void documentRetrieveTestA() {
//        String login = "admin";
//        String password = "admin";
//
//        {
//            SecurityLoginRequest request = new SecurityLoginRequest();
//            request.setUsername(login);
//            request.setPassword(password);
//            SecurityLoginResponse response = client.securityLogin(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        String collectionName = "tmp_user" + RandomStringUtils.randomAlphabetic(5).toLowerCase();
//
//        {
//            CollectionCreateRequest request = new CollectionCreateRequest();
//            request.setCollectionName(collectionName);
//            {
//                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
//                attribute.setName("first_name");
//                attribute.setNullable(false);
//                attribute.setJavaType(String.class.getName());
//                request.getAttributes().add(attribute);
//            }
//            {
//                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
//                attribute.setName("join_date");
//                attribute.setNullable(true);
//                attribute.setJavaType(Date.class.getName());
//                request.getAttributes().add(attribute);
//            }
//            CollectionCreateResponse response = client.collectionCreate(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        String documentId = null;
//
//        {
//            DocumentCreateRequest request = new DocumentCreateRequest();
//            request.getDocument().put("first_name", "Socheat KHAUV");
//            request.getDocument().put("join_date", new Date());
//            DocumentCreateResponse response = client.documentCreate(collectionName, request);
//            System.out.println(gson.toJson(response));
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//            documentId = response.getData().getDocumentId();
//        }
//
//        {
//            DocumentRetrieveRequest request = new DocumentRetrieveRequest();
//            DocumentRetrieveResponse response = client.documentRetrieve(collectionName, documentId, request);
//            System.out.println(gson.toJson(response));
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        {
//            DocumentDeleteRequest request = new DocumentDeleteRequest();
//            DocumentDeleteResponse response = client.documentDelete(collectionName, documentId, request);
//            System.out.println(gson.toJson(response));
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        {
//            CollectionDeleteRequest request = new CollectionDeleteRequest();
//            request.setCollectionName(collectionName);
//            CollectionDeleteResponse response = client.collectionDelete(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//    }
//
//    @Test
//    public void documentQueryTestA() {
//        String login = "admin";
//        String password = "admin";
//
//        {
//            SecurityLoginRequest request = new SecurityLoginRequest();
//            request.setUsername(login);
//            request.setPassword(password);
//            SecurityLoginResponse response = client.securityLogin(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        String collectionName = "tmp_user" + RandomStringUtils.randomAlphabetic(5).toLowerCase();
//
//        {
//            CollectionCreateRequest request = new CollectionCreateRequest();
//            request.setCollectionName(collectionName);
//            {
//                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
//                attribute.setName("first_name");
//                attribute.setNullable(false);
//                attribute.setJavaType(String.class.getName());
//                request.getAttributes().add(attribute);
//            }
//            {
//                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
//                attribute.setName("join_date");
//                attribute.setNullable(true);
//                attribute.setJavaType(Date.class.getName());
//                request.getAttributes().add(attribute);
//            }
//            CollectionCreateResponse response = client.collectionCreate(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        String documentId = null;
//
//        {
//            DocumentCreateRequest request = new DocumentCreateRequest();
//            request.getDocument().put("first_name", "Socheat KHAUV");
//            request.getDocument().put("join_date", new Date());
//            DocumentCreateResponse response = client.documentCreate(collectionName, request);
//            System.out.println(gson.toJson(response));
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//            documentId = response.getData().getDocumentId();
//        }
//
//        {
//            DocumentCreateRequest request = new DocumentCreateRequest();
//            request.getDocument().put("first_name", "PkayJava");
//            request.getDocument().put("join_date", new Date());
//            DocumentCreateResponse response = client.documentCreate(collectionName, request);
//            System.out.println(gson.toJson(response));
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//            documentId = response.getData().getDocumentId();
//        }
//
//        {
//            DocumentQueryRequest request = new DocumentQueryRequest();
//            request.getQuery().getFields().add("first_name");
//            request.getQuery().getWhere().add("first_name = :first_name");
//            request.getQuery().getParams().put("first_name", "PkayJava");
//            request.getQuery().getFields().add("join_date");
//            DocumentQueryResponse response = client.documentQuery(collectionName, request);
//            System.out.println(gson.toJson(response));
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        {
//            DocumentDeleteRequest request = new DocumentDeleteRequest();
//            DocumentDeleteResponse response = client.documentDelete(collectionName, documentId, request);
//            System.out.println(gson.toJson(response));
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//
//        {
//            CollectionDeleteRequest request = new CollectionDeleteRequest();
//            request.setCollectionName(collectionName);
//            CollectionDeleteResponse response = client.collectionDelete(request);
//            Assert.assertEquals(response.getHttpCode().intValue(), 200);
//        }
//    }
//
//}
