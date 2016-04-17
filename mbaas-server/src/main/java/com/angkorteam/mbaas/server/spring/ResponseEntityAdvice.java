package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.plain.response.Response;
import com.angkorteam.mbaas.plain.response.UnknownResponse;
import com.google.gson.Gson;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public ResponseEntityAdvice() {
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Response beforeBodyWrite(Response body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();

        Response responseBody = body;
        if (body == null) {
            responseBody = new UnknownResponse();
            responseBody.setHttpCode(HttpStatus.OK.value());
            responseBody.setResult(HttpStatus.OK.getReasonPhrase());
        } else {
            if (responseBody.getHttpCode() == null) {
                responseBody.setHttpCode(HttpStatus.OK.value());
                responseBody.setResult(HttpStatus.OK.getReasonPhrase());
            }
            if (responseBody.getResult() == null) {
                try {
                    responseBody.setResult(HttpStatus.valueOf(responseBody.getHttpCode()).getReasonPhrase());
                } catch (IllegalArgumentException e) {
                    responseBody.setResult("Unknown");
                }
            }
        }
        HttpHeaders httpHeaders = request.getHeaders();
        Map<String, List<String>> requestHeader = responseBody.getRequestHeader();
        for (Map.Entry<String, List<String>> entry : httpHeaders.entrySet()) {
            requestHeader.put(entry.getKey(), entry.getValue());
        }

        responseBody.setVersion(configuration.getString(Constants.APP_VERSION));
        responseBody.setMethod(request.getMethod().name());

        return responseBody;
    }
}
