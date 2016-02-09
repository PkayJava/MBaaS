package com.angkorteam.mbaas.factory;

import com.angkorteam.mbaas.ApplicationContext;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class StringEncryptorFactoryBean implements FactoryBean<StringEncryptor>, InitializingBean, ServletContextAware {

    private StringEncryptor encryptor;

    private ServletContext servletContext;

    public StringEncryptorFactoryBean() {
    }

    @Override
    public StringEncryptor getObject() throws Exception {
        return this.encryptor;
    }

    @Override
    public Class<?> getObjectType() {
        return StringEncryptor.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplicationContext applicationContext = ApplicationContext.get(this.servletContext);
        this.encryptor = applicationContext.getStringEncryptor();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
