package com.angkorteam.mbaas.factory;

import com.angkorteam.mbaas.ApplicationContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

/**
 * Created by Khauv Socheat on 2/4/2016.
 */
public class BasicDataSourceFactory implements FactoryBean<DataSource>, InitializingBean, ServletContextAware {

    private DataSource dataSource;

    private ServletContext servletContext;

    @Override
    public DataSource getObject() throws Exception {
        return this.dataSource;
    }

    @Override
    public Class<?> getObjectType() {
        return DataSource.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ApplicationContext applicationContext = ApplicationContext.get(this.servletContext);
        this.dataSource = applicationContext.getDataSource();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
