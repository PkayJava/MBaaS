package com.angkorteam.mbaas.client;

import com.angkorteam.mbaas.plain.enums.PermissionEnum;
import com.angkorteam.mbaas.plain.request.collection.*;
import com.angkorteam.mbaas.plain.request.security.SecurityLoginRequest;
import com.angkorteam.mbaas.plain.request.security.SecuritySignUpRequest;
import com.angkorteam.mbaas.plain.response.collection.*;
import com.angkorteam.mbaas.plain.response.security.SecurityLoginResponse;
import com.angkorteam.mbaas.plain.response.security.SecuritySignUpResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class CollectionControllerTest extends BaseTest {

    @Test
    public void collectionCreateTest() {

        String login = "admin";
        String password = "admin";

        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.securityLogin(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        String collectionName = "tmp_user" + RandomStringUtils.randomAlphabetic(5).toLowerCase();

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
    public void collectionDeleteTest() {

        String login = "admin";
        String password = "admin";

        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.securityLogin(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        String collectionName = "tmp_user" + RandomStringUtils.randomAlphabetic(5).toLowerCase();

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
    public void collectionAttributeCreateTest() {

        String login = "admin";
        String password = "admin";

        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.securityLogin(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        String collectionName = "tmp_user" + RandomStringUtils.randomAlphabetic(5).toLowerCase();

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
    public void collectionAttributeDeleteTest() {

        String login = "admin";
        String password = "admin";

        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.securityLogin(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        String collectionName = "tmp_user" + RandomStringUtils.randomAlphabetic(5).toLowerCase();

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

        String collectionName = "tmp_user" + RandomStringUtils.randomAlphabetic(5).toLowerCase();

        {
            CollectionCreateRequest request = new CollectionCreateRequest();
            request.setCollectionName(collectionName);
            CollectionCreateResponse response = client.collectionCreate(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        String userALogin = "us" + RandomStringUtils.randomAlphabetic(5).toLowerCase();
        String userAPassword = "us" + RandomStringUtils.randomAlphabetic(5).toLowerCase();
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
            Assert.assertEquals(response.getData().getPermission().intValue(), PermissionEnum.Create.getLiteral() | PermissionEnum.Delete.getLiteral() | PermissionEnum.Modify.getLiteral() | PermissionEnum.Read.getLiteral());
        }

        {
            CollectionDeleteRequest request = new CollectionDeleteRequest();
            request.setCollectionName(collectionName);
            CollectionDeleteResponse response = client.collectionDelete(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }
    }

    @Test
    public void collectionPermissionRevokeUsernameTest() {
        String login = "admin";
        String password = "admin";

        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.securityLogin(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        String collectionName = "tmp_user" + RandomStringUtils.randomAlphabetic(5).toLowerCase();

        {
            CollectionCreateRequest request = new CollectionCreateRequest();
            request.setCollectionName(collectionName);
            CollectionCreateResponse response = client.collectionCreate(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        String userALogin = "us" + RandomStringUtils.randomAlphabetic(5).toLowerCase();
        String userAPassword = "us" + RandomStringUtils.randomAlphabetic(5).toLowerCase();
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
            Assert.assertEquals(response.getData().getPermission().intValue(), PermissionEnum.Create.getLiteral() | PermissionEnum.Delete.getLiteral() | PermissionEnum.Modify.getLiteral() | PermissionEnum.Read.getLiteral());
        }

        {
            CollectionPermissionUsernameRequest request = new CollectionPermissionUsernameRequest();
            request.setCollectionName(collectionName);
            request.setUsername(userALogin);
            request.getActions().add(PermissionEnum.Create.getLiteral());
            CollectionPermissionUsernameResponse response = client.collectionPermissionRevokeUsername(request);
            Assert.assertEquals(response.getData().getPermission().intValue(), PermissionEnum.Delete.getLiteral() | PermissionEnum.Modify.getLiteral() | PermissionEnum.Read.getLiteral());
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

        String collectionName = "tmp_user" + RandomStringUtils.randomAlphabetic(5).toLowerCase();

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
            Assert.assertEquals(response.getData().getPermission().intValue(), PermissionEnum.Create.getLiteral() | PermissionEnum.Delete.getLiteral() | PermissionEnum.Modify.getLiteral() | PermissionEnum.Read.getLiteral());
        }

        {
            CollectionDeleteRequest request = new CollectionDeleteRequest();
            request.setCollectionName(collectionName);
            CollectionDeleteResponse response = client.collectionDelete(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }
    }

    @Test
    public void collectionPermissionRevokeRoleNameTest() {
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

        String collectionName = "tmp_user" + RandomStringUtils.randomAlphabetic(5).toLowerCase();

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
            Assert.assertEquals(response.getData().getPermission().intValue(), PermissionEnum.Create.getLiteral() | PermissionEnum.Delete.getLiteral() | PermissionEnum.Modify.getLiteral() | PermissionEnum.Read.getLiteral());
        }

        {
            CollectionPermissionRoleNameRequest request = new CollectionPermissionRoleNameRequest();
            request.setCollectionName(collectionName);
            request.setRoleName(rolename);
            request.getActions().add(PermissionEnum.Modify.getLiteral());
            request.getActions().add(PermissionEnum.Read.getLiteral());
            CollectionPermissionRoleNameResponse response = client.collectionPermissionRevokeRoleName(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
            Assert.assertEquals(response.getData().getPermission().intValue(), PermissionEnum.Create.getLiteral() | PermissionEnum.Delete.getLiteral());
        }

        {
            CollectionDeleteRequest request = new CollectionDeleteRequest();
            request.setCollectionName(collectionName);
            CollectionDeleteResponse response = client.collectionDelete(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }
    }

}
