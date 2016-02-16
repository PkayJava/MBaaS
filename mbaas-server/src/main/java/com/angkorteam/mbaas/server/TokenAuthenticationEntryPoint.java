package com.angkorteam.mbaas.server;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class TokenAuthenticationEntryPoint implements AuthenticationEntryPoint,
        InitializingBean {
    // ~ Instance fields
    // ================================================================================================

    private String realmName;

    // ~ Methods
    // ========================================================================================================

    public void afterPropertiesSet() throws Exception {
        Assert.hasText(realmName, "realmName must be specified");
    }

    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.addHeader("WWW-Authenticate", "Token realm=\"" + realmName + "\"");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                authException.getMessage());
    }

    public String getRealmName() {
        return realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

}