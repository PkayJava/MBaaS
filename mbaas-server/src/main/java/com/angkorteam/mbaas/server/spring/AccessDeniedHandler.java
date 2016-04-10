package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.plain.response.UnknownResponse;
import com.google.gson.Gson;
import org.springframework.security.access.AccessDeniedException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by socheat on 4/10/16.
 */
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {

    private Gson gson;

    public AccessDeniedHandler() {
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        UnknownResponse responseBody = new UnknownResponse();
        responseBody.setResult(org.springframework.http.HttpStatus.FORBIDDEN.getReasonPhrase());
        responseBody.setHttpCode(org.springframework.http.HttpStatus.FORBIDDEN.value());
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
