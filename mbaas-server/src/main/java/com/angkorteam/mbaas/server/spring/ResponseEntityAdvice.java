package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.plain.response.Response;
import com.angkorteam.mbaas.plain.response.UnknownResponse;
import com.angkorteam.mbaas.server.bean.Configuration;
import com.angkorteam.mbaas.server.bean.System;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 2/16/16.
 */
@ControllerAdvice
public class ResponseEntityAdvice implements ResponseBodyAdvice<Response> {

    @Autowired
    private System system;

    public ResponseEntityAdvice() {
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Response beforeBodyWrite(Response body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        Configuration configuration = system.getConfiguration();

        Response responseBody = body;
        if (body == null) {
            responseBody = new UnknownResponse();
            responseBody.setResultCode(HttpStatus.OK.value());
            responseBody.setResultMessage(HttpStatus.OK.getReasonPhrase());
        } else {
            if (responseBody.getResultCode() == null) {
                responseBody.setResultCode(HttpStatus.OK.value());
                responseBody.setResultMessage(HttpStatus.OK.getReasonPhrase());
            }
            if (responseBody.getResultMessage() == null) {
                try {
                    responseBody.setResultMessage(HttpStatus.valueOf(responseBody.getResultCode()).getReasonPhrase());
                } catch (IllegalArgumentException e) {
                    responseBody.setResultMessage("Unknown");
                }
            }
        }
        HttpHeaders httpHeaders = request.getHeaders();
        Map<String, List<String>> requestHeader = responseBody.getRequestHeader();
        for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
            requestHeader.put(entry.getKey(), entry.getValue());
        }

        responseBody.setMethod(request.getMethod().name());

        return responseBody;
    }
}
