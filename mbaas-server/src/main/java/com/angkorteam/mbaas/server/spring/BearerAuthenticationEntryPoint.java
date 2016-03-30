package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.server.function.HttpFunction;
import com.angkorteam.mbaas.server.page.oauth2.AuthorizePage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class BearerAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        StringBuffer address = new StringBuffer();
        address.append(HttpFunction.getHttpAddress(request)).append("/web" + AuthorizePage.class.getAnnotation(Mount.class).value());
        response.sendRedirect(address.toString());
    }

}