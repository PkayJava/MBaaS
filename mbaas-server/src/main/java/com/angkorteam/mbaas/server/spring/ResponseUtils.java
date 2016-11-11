package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.plain.response.UnknownResponse;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * Created by Khauv Socheat on 4/17/2016.
 */
public class ResponseUtils {

    public static UnknownResponse unknownResponse(HttpServletRequest request, HttpStatus httpStatus) {
        UnknownResponse responseBody = new UnknownResponse();
        Map<String, List<String>> requestHeader = responseBody.getRequestHeader();
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String header = headers.nextElement();
            Enumeration<String> values = request.getHeaders(header);
            List<String> value = new ArrayList<>();
            while (values.hasMoreElements()) {
                value.add(values.nextElement());
            }
            requestHeader.put(header, value);
        }
        responseBody.setMethod(request.getMethod());
        responseBody.setResultCode(httpStatus.value());
        responseBody.setResultMessage(httpStatus.getReasonPhrase());
        return responseBody;
    }
}
