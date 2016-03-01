package com.angkorteam.mbaas.client;

import com.angkorteam.mbaas.plain.request.monitor.MonitorCpuRequest;
import com.angkorteam.mbaas.plain.request.security.SecurityLoginRequest;
import com.angkorteam.mbaas.plain.request.security.SecurityLogoutRequest;
import com.angkorteam.mbaas.plain.request.security.SecurityLogoutSessionRequest;
import com.angkorteam.mbaas.plain.request.security.SecuritySignUpRequest;
import com.angkorteam.mbaas.plain.response.monitor.MonitorCpuResponse;
import com.angkorteam.mbaas.plain.response.security.SecurityLoginResponse;
import com.angkorteam.mbaas.plain.response.security.SecurityLogoutResponse;
import com.angkorteam.mbaas.plain.response.security.SecurityLogoutSessionResponse;
import com.angkorteam.mbaas.plain.response.security.SecuritySignUpResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class SecurityControllerTest extends BaseTest {


    @Test
    public void securitySignUpTest() {

        String login = "temp_" + RandomStringUtils.randomAlphabetic(5).toLowerCase();
        String password = "temp_" + RandomStringUtils.randomAlphabetic(5).toLowerCase();
        SecuritySignUpRequest request = new SecuritySignUpRequest();
        request.setUsername(login);
        request.setPassword(password);

        SecuritySignUpResponse response = client.securitySignUp(request);
        Assert.assertEquals(response.getHttpCode().intValue(), 200);

    }

    @Test
    public void securityLoginTest() {
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
    public void securityLogoutTest() {
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
    public void securityLogoutSessionTest() {
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

}
