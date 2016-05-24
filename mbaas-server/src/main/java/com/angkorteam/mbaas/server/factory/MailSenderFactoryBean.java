package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.server.spring.ApplicationContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.mail.MailSender;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * Created by socheat on 4/4/16.
 */
public class MailSenderFactoryBean implements FactoryBean<MailSender>, InitializingBean, ServletContextAware {

    private MailSender mailSender;

    private ServletContext servletContext;

    @Override
    public MailSender getObject() throws Exception {
        return this.mailSender;
    }

    @Override
    public Class<?> getObjectType() {
        return MailSender.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplicationContext applicationContext = ApplicationContext.get(this.servletContext);
        this.mailSender = applicationContext.getMailSender();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
