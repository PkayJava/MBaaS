package com.angkorteam.mbaas.service;

import com.angkorteam.mbaas.response.Response;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Khauv Socheat on 2/7/2016.
 */
public class RequestHeader {

    public static void serve(Response responseBody, HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String name = headers.nextElement();
            Enumeration<String> values = request.getHeaders(name);
            List<String> h = new LinkedList<>();
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                h.add(value);
            }
            responseBody.getRequestHeader().put(name, h);
        }
    }
}
