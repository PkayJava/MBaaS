package com.angkorteam.mbaas.server.spring;

import com.angkorteam.mbaas.server.bean.GroovyClassLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by socheat on 12/12/16.
 */
public class ServletRequestListener implements javax.servlet.ServletRequestListener {

    private Connection connection;

    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        if (this.connection != null) {
            try {
                this.connection.commit();
            } catch (SQLException e) {
                try {
                    this.connection.rollback();
                } catch (SQLException e1) {
                }
            }
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            this.connection = null;
        }
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
        DataSource dataSource = applicationContext.getBean(DataSource.class);
        try {
            this.connection = dataSource.getConnection();
            this.connection.setAutoCommit(false);
        } catch (SQLException e) {
        }
    }
}
