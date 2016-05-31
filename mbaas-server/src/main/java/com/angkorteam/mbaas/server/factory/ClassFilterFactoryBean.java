package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.server.spring.ApplicationContext;
import jdk.nashorn.api.scripting.ClassFilter;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * Created by socheat on 5/31/16.
 */
public class ClassFilterFactoryBean implements FactoryBean<ClassFilter>, InitializingBean, ServletContextAware {

    private ClassFilter classFilter;

    private ServletContext servletContext;

    @Override
    public ClassFilter getObject() throws Exception {
        return this.classFilter;
    }

    @Override
    public Class<?> getObjectType() {
        return ClassFilter.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplicationContext applicationContext = ApplicationContext.get(this.servletContext);
        this.classFilter = applicationContext.getClassFilter();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
