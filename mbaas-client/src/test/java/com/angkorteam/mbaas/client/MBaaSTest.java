package com.angkorteam.mbaas.client;

import com.angkorteam.mbaas.plain.enums.PermissionEnum;
import com.angkorteam.mbaas.plain.request.*;
import com.angkorteam.mbaas.plain.response.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Assert;
import org.junit.Test;

import javax.script.ScriptException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class MBaaSTest {

    public final String HOST_A = "http://pkayjava.ddns.net:7080/api";

    public final String HOST_B = "http://172.16.1.42:7080/api";

    public final String HOST_C = "http://192.168.1.117:7080/api";

    private Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZZ").create();

    private MBaaSClient client = new MBaaSClient("1234567890", HOST_B);

    @Test
    public void securitySignUpTest() throws ScriptException {

        String login = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        SecuritySignUpRequest request = new SecuritySignUpRequest();
        request.setUsername(login);
        request.setPassword(password);

        SecuritySignUpResponse response = client.securitySignUp(request);
        Assert.assertEquals(response.getHttpCode().intValue(), 200);

    }

    @Test
    public void securityLoginTest() throws ScriptException {
        String login = "admin";
        String password = "admin";

        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.securityLogin(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        MonitorCpuRequest request = new MonitorCpuRequest();
        MonitorCpuResponse response = client.cpu(request);
        Assert.assertEquals(response.getHttpCode().intValue(), 200);
    }

    @Test
    public void securityLogoutTest() throws ScriptException {
        String login = "admin";
        String password = "admin";
        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.securityLogin(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        SecurityLogoutRequest request = new SecurityLogoutRequest();
        SecurityLogoutResponse response = client.securityLogout(request);
        Assert.assertEquals(response.getHttpCode().intValue(), 200);
    }

    @Test
    public void securityLogoutSessionTest() throws ScriptException {
        String login = "admin";
        String password = "admin";
        String session = null;
        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.securityLogin(request);
            session = response.getData().getSession();
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        SecurityLogoutSessionRequest request = new SecurityLogoutSessionRequest();
        SecurityLogoutSessionResponse response = client.securityLogoutSession(session, request);
        Assert.assertEquals(response.getHttpCode().intValue(), 200);
    }

    @Test
    public void monitorCpuTest() throws ScriptException {

        String login = "admin";
        String password = "admin";

        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.securityLogin(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        MonitorCpuRequest request = new MonitorCpuRequest();
        MonitorCpuResponse response = client.cpu(request);
        Assert.assertEquals(response.getHttpCode().intValue(), 200);
        System.out.println(gson.toJson(response));
    }

    @Test
    public void monitorMemTest() throws ScriptException {

        String login = "admin";
        String password = "admin";

        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.securityLogin(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        MonitorMemRequest request = new MonitorMemRequest();
        MonitorMemResponse response = client.mem(request);
        Assert.assertEquals(response.getHttpCode().intValue(), 200);
        System.out.println(gson.toJson(response));
    }

    @Test
    public void collectionCreateTest() throws ScriptException {

        String login = "admin";
        String password = "admin";

        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.securityLogin(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        String collectionName = "tmp_user" + UUID.randomUUID().toString();

        {
            CollectionCreateRequest request = new CollectionCreateRequest();
            request.setCollectionName(collectionName);
            {
                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
                attribute.setJavaType(String.class.getName());
                attribute.setName("first_name");
                attribute.setNullable(false);
                request.getAttributes().add(attribute);
            }
            {
                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
                attribute.setJavaType(String.class.getName());
                attribute.setName("last_name");
                attribute.setNullable(true);
                request.getAttributes().add(attribute);
            }
            {
                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
                attribute.setJavaType(Byte.class.getName());
                attribute.setName("amount1");
                attribute.setNullable(true);
                request.getAttributes().add(attribute);
            }
            {
                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
                attribute.setJavaType(Short.class.getName());
                attribute.setName("amount2");
                attribute.setNullable(true);
                request.getAttributes().add(attribute);
            }
            {
                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
                attribute.setJavaType(Integer.class.getName());
                attribute.setName("amount3");
                attribute.setNullable(true);
                request.getAttributes().add(attribute);
            }
            {
                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
                attribute.setJavaType(Long.class.getName());
                attribute.setName("amount4");
                attribute.setNullable(true);
                request.getAttributes().add(attribute);
            }
            {
                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
                attribute.setJavaType(Boolean.class.getName());
                attribute.setName("status");
                attribute.setNullable(true);
                request.getAttributes().add(attribute);
            }
            {
                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
                attribute.setJavaType(Float.class.getName());
                attribute.setName("discount1");
                attribute.setNullable(true);
                request.getAttributes().add(attribute);
            }
            {
                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
                attribute.setJavaType(Double.class.getName());
                attribute.setName("discount2");
                attribute.setNullable(true);
                request.getAttributes().add(attribute);
            }
            {
                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
                attribute.setJavaType(Date.class.getName());
                attribute.setName("age");
                attribute.setNullable(true);
                request.getAttributes().add(attribute);
            }
            {
                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
                attribute.setJavaType(Character.class.getName());
                attribute.setName("gender");
                attribute.setNullable(true);
                request.getAttributes().add(attribute);
            }
            CollectionCreateResponse response = client.collectionCreate(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        {
            CollectionDeleteRequest request = new CollectionDeleteRequest();
            request.setCollectionName(collectionName);
            CollectionDeleteResponse response = client.collectionDelete(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

    }

    @Test
    public void collectionDeleteTest() throws ScriptException {

        String login = "admin";
        String password = "admin";

        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.securityLogin(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        String collectionName = "tmp_user" + UUID.randomUUID().toString();

        {
            CollectionCreateRequest request = new CollectionCreateRequest();
            request.setCollectionName(collectionName);
            {
                CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
                attribute.setJavaType(String.class.getName());
                attribute.setName("first_name");
                attribute.setNullable(false);
                request.getAttributes().add(attribute);
            }
            CollectionCreateResponse response = client.collectionCreate(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        {
            CollectionDeleteRequest request = new CollectionDeleteRequest();
            request.setCollectionName(collectionName);
            CollectionDeleteResponse response = client.collectionDelete(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }
    }

    @Test
    public void collectionAttributeCreateTest() throws ScriptException {

        String login = "admin";
        String password = "admin";

        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.securityLogin(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        String collectionName = "tmp_user" + UUID.randomUUID().toString();

        {
            CollectionCreateRequest request = new CollectionCreateRequest();
            request.setCollectionName(collectionName);
            CollectionCreateResponse response = client.collectionCreate(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        {
            List<String> javaTypes = Arrays.asList(
                    Byte.class.getName(),
                    Short.class.getName(),
                    Integer.class.getName(),
                    Long.class.getName(),
                    Float.class.getName(),
                    Double.class.getName(),
                    Date.class.getName(),
                    Boolean.class.getName(),
                    String.class.getName(),
                    Character.class.getName()
            );
            int i = 0;
            for (String javaType : javaTypes) {
                i++;
                CollectionAttributeCreateRequest request = new CollectionAttributeCreateRequest();
                request.setCollectionName(collectionName);
                request.setJavaType(javaType);
                request.setAttributeName("first_name" + i);
                request.setNullable(false);
                CollectionAttributeCreateResponse response = client.collectionAttributeCreate(request);
                Assert.assertEquals(response.getHttpCode().intValue(), 200);
            }
        }

        {
            CollectionDeleteRequest request = new CollectionDeleteRequest();
            request.setCollectionName(collectionName);
            CollectionDeleteResponse response = client.collectionDelete(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }
    }

    @Test
    public void collectionAttributeDeleteTest() throws ScriptException {

        String login = "admin";
        String password = "admin";

        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.securityLogin(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        String collectionName = "tmp_user" + UUID.randomUUID().toString();

        {
            CollectionCreateRequest request = new CollectionCreateRequest();
            request.setCollectionName(collectionName);
            CollectionCreateResponse response = client.collectionCreate(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        String attributeName = "first_name";
        {

            CollectionAttributeCreateRequest request = new CollectionAttributeCreateRequest();
            request.setCollectionName(collectionName);
            request.setJavaType(String.class.getName());
            request.setAttributeName(attributeName);
            request.setNullable(false);
            CollectionAttributeCreateResponse response = client.collectionAttributeCreate(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        {

            CollectionAttributeDeleteRequest request = new CollectionAttributeDeleteRequest();
            request.setCollectionName(collectionName);
            request.setAttributeName(attributeName);
            CollectionAttributeDeleteResponse response = client.collectionAttributeDelete(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        {
            CollectionDeleteRequest request = new CollectionDeleteRequest();
            request.setCollectionName(collectionName);
            CollectionDeleteResponse response = client.collectionDelete(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }
    }

    @Test
    public void collectionPermissionGrantUsernameTest() {
        String login = "admin";
        String password = "admin";

        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.securityLogin(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        String collectionName = "tmp_user" + UUID.randomUUID().toString();

        {
            CollectionCreateRequest request = new CollectionCreateRequest();
            request.setCollectionName(collectionName);
            CollectionCreateResponse response = client.collectionCreate(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        String userALogin = "us" + UUID.randomUUID().toString();
        String userAPassword = "us" + UUID.randomUUID().toString();
        {
            SecuritySignUpRequest request = new SecuritySignUpRequest();
            request.setUsername(userALogin);
            request.setPassword(userAPassword);
            SecuritySignUpResponse response = client.securitySignUp(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        {
            CollectionPermissionUsernameRequest request = new CollectionPermissionUsernameRequest();
            request.setCollectionName(collectionName);
            request.setUsername(userALogin);
            request.getActions().add(PermissionEnum.Create.getLiteral());
            request.getActions().add(PermissionEnum.Delete.getLiteral());
            request.getActions().add(PermissionEnum.Modify.getLiteral());
            request.getActions().add(PermissionEnum.Read.getLiteral());
            CollectionPermissionUsernameResponse response = client.collectionPermissionGrantUsername(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        {
            CollectionDeleteRequest request = new CollectionDeleteRequest();
            request.setCollectionName(collectionName);
            CollectionDeleteResponse response = client.collectionDelete(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }
    }

    @Test
    public void collectionPermissionGrantRoleNameTest() {
        String login = "admin";
        String password = "admin";
        String rolename = "backoffice";

        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.securityLogin(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        String collectionName = "tmp_user" + UUID.randomUUID().toString();

        {
            CollectionCreateRequest request = new CollectionCreateRequest();
            request.setCollectionName(collectionName);
            CollectionCreateResponse response = client.collectionCreate(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        {
            CollectionPermissionRoleNameRequest request = new CollectionPermissionRoleNameRequest();
            request.setCollectionName(collectionName);
            request.setRoleName(rolename);
            request.getActions().add(PermissionEnum.Create.getLiteral());
            request.getActions().add(PermissionEnum.Delete.getLiteral());
            request.getActions().add(PermissionEnum.Modify.getLiteral());
            request.getActions().add(PermissionEnum.Read.getLiteral());
            CollectionPermissionRoleNameResponse response = client.collectionPermissionGrantRoleName(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        {
            CollectionDeleteRequest request = new CollectionDeleteRequest();
            request.setCollectionName(collectionName);
            CollectionDeleteResponse response = client.collectionDelete(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }
    }

}
