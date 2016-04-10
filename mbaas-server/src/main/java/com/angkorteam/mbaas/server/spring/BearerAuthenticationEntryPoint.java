package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.plain.response.UnknownResponse;
import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
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

    private Gson gson;

    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        UnknownResponse responseBody = new UnknownResponse();
        responseBody.setResult(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        responseBody.setHttpCode(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        this.gson.toJson(responseBody, response.getWriter());
    }

    public Gson getGson() {
        return gson;
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }
}