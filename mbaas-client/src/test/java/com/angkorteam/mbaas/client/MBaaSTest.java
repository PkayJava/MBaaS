package com.angkorteam.mbaas.client;

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
    public void signUpTest() throws ScriptException {

        String login = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        SecuritySignUpRequest request = new SecuritySignUpRequest();
        request.setUsername(login);
        request.setPassword(password);

        SecuritySignUpResponse response = client.signUp(request);
        Assert.assertEquals(response.getHttpCode().intValue(), 200);

    }

    @Test
    public void loginTest() throws ScriptException {
        String login = "admin";
        String password = "admin";

        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.login(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        MonitorCpuRequest request = new MonitorCpuRequest();
        MonitorCpuResponse response = client.cpu(request);
        Assert.assertEquals(response.getHttpCode().intValue(), 200);
    }

    @Test
    public void logoutTest() throws ScriptException {
        String login = "admin";
        String password = "admin";
        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.login(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        SecurityLogoutRequest request = new SecurityLogoutRequest();
        SecurityLogoutResponse response = client.logout(request);
        Assert.assertEquals(response.getHttpCode().intValue(), 200);
    }

    @Test
    public void logoutSessionTest() throws ScriptException {
        String login = "admin";
        String password = "admin";
        String session = null;
        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.login(request);
            session = response.getData().getSession();
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        SecurityLogoutSessionRequest request = new SecurityLogoutSessionRequest();
        SecurityLogoutSessionResponse response = client.logoutSession(session, request);
        Assert.assertEquals(response.getHttpCode().intValue(), 200);
    }

    @Test
    public void cpuTest() throws ScriptException {

        String login = "admin";
        String password = "admin";

        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.login(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        MonitorCpuRequest request = new MonitorCpuRequest();
        MonitorCpuResponse response = client.cpu(request);
        Assert.assertEquals(response.getHttpCode().intValue(), 200);
        System.out.println(gson.toJson(response));
    }

    @Test
    public void memTest() throws ScriptException {

        String login = "admin";
        String password = "admin";

        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.login(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        MonitorMemRequest request = new MonitorMemRequest();
        MonitorMemResponse response = client.mem(request);
        Assert.assertEquals(response.getHttpCode().intValue(), 200);
        System.out.println(gson.toJson(response));
    }

    @Test
    public void createCollectionTest() throws ScriptException {

        String login = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        {
            SecuritySignUpRequest request = new SecuritySignUpRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecuritySignUpResponse response = client.signUp(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        {
            SecurityLoginRequest request = new SecurityLoginRequest();
            request.setUsername(login);
            request.setPassword(password);
            SecurityLoginResponse response = client.login(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        String collectionName = "hello" + UUID.randomUUID().toString();

        {
            CollectionCreateRequest request = new CollectionCreateRequest();
            request.setCollectionName(collectionName);
            CollectionCreateResponse response = client.createCollection(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        {
            CollectionDeleteRequest request = new CollectionDeleteRequest();
            request.setCollectionName(collectionName);
            CollectionDeleteResponse response = client.deleteCollection(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

    }

}
