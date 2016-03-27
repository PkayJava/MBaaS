package com.angkorteam.mbaas.server.function;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by socheat on 3/27/16.
 */
public class HttpFunction {

    public static String getHttpAddress(HttpServletRequest request) {
        ServletContext servletContext = request.getServletContext();
        StringBuffer address = new StringBuffer();
        if (request.isSecure() && request.getServerPort() == 443) {
            address.append("https://").append(request.getServerName()).append(servletContext.getContextPath());
        } else if (!request.isSecure() && request.getServerPort() == 80) {
            address.append("http://").append(request.getServerName()).append(servletContext.getContextPath());
        } else {
            if (request.isSecure()) {
                address.append("https://");
            } else {
                address.append("http://");
            }
            address.append(request.getServerName()).append(":").append(request.getServerPort()).append(servletContext.getContextPath());
        }
        return address.toString();
    }
}
