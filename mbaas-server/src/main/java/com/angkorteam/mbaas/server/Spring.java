package com.angkorteam.mbaas.server;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * Created by socheat on 10/23/16.
 */
public class Spring {

    private static ServletContext servletContext = null;

    public static <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        if (servletContext == null) {
            servletContext = Application.get().getServletContext();
        }
        ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        return applicationContext.getBean(name, requiredType);
    }


    public static <T> T getBean(Class<T> requiredType) throws BeansException {
        if (servletContext == null) {
            servletContext = Application.get().getServletContext();
        }
        ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        return applicationContext.getBean(requiredType);
    }

}
