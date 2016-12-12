package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.server.bean.GroovyClassLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;

/**
 * Created by socheat on 12/12/16.
 */
public class ServletRequestListener implements javax.servlet.ServletRequestListener {

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {

    }

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        ServletContext servletContext = sre.getServletContext();
        ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
        GroovyClassLoader classLoader = applicationContext.getBean(GroovyClassLoader.class);
        if (Thread.currentThread().getContextClassLoader() instanceof GroovyClassLoader) {
        } else {
            Thread.currentThread().setContextClassLoader(classLoader);
        }
    }
}
