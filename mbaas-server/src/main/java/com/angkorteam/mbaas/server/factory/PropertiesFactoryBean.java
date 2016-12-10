package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.server.bean.Configuration;
import org.elasticsearch.common.Strings;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by socheat on 12/10/16.
 */
public class PropertiesFactoryBean implements FactoryBean<Properties>, InitializingBean, ServletContextAware {

    private Properties properties;

    private ServletContext servletContext;

    @Override
    public Properties getObject() throws Exception {
        return this.properties;
    }

    @Override
    public Class<?> getObjectType() {
        return Properties.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String configurationFile = this.servletContext.getInitParameter("configuration");
        File file;
        if (!Strings.isNullOrEmpty(configurationFile)) {
            file = new File(configurationFile);
        } else {
            File home = new File(java.lang.System.getProperty("user.home"));
            file = new File(home, ".xml/" + Configuration.KEY);
        }

        this.properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(file)) {
            this.properties.loadFromXML(inputStream);
        }
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

}
