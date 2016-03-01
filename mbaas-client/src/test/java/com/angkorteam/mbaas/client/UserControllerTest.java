package com.angkorteam.mbaas.client;

import com.angkorteam.mbaas.plain.request.monitor.MonitorCpuRequest;
import com.angkorteam.mbaas.plain.request.security.SecurityLoginRequest;
import com.angkorteam.mbaas.plain.request.security.SecurityLogoutRequest;
import com.angkorteam.mbaas.plain.request.security.SecurityLogoutSessionRequest;
import com.angkorteam.mbaas.plain.request.security.SecuritySignUpRequest;
import com.angkorteam.mbaas.plain.request.user.UserPasswordResetRequest;
import com.angkorteam.mbaas.plain.response.monitor.MonitorCpuResponse;
import com.angkorteam.mbaas.plain.response.security.SecurityLoginResponse;
import com.angkorteam.mbaas.plain.response.security.SecurityLogoutResponse;
import com.angkorteam.mbaas.plain.response.security.SecurityLogoutSessionResponse;
import com.angkorteam.mbaas.plain.response.security.SecuritySignUpResponse;
import com.angkorteam.mbaas.plain.response.user.UserPasswordResetResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class UserControllerTest extends BaseTest {

    @Test
    public void userPasswordReset() {
        String username = "socheat.khauv" + RandomStringUtils.randomAlphabetic(5).toLowerCase();
        {
            String login = username;
            String password = "temp_" + RandomStringUtils.randomAlphabetic(5).toLowerCase();
            SecuritySignUpRequest request = new SecuritySignUpRequest();
            request.setUsername(login);
            request.setPassword(password);

            SecuritySignUpResponse response = client.securitySignUp(request);
            Assert.assertEquals(response.getHttpCode().intValue(), 200);
        }

        {
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
        }

        UserPasswordResetRequest request = new UserPasswordResetRequest();
        UserPasswordResetResponse response = client.userPasswordReset(username, request);
        Assert.assertEquals(response.getHttpCode().intValue(), 200);
    }

}
