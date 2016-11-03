//package com.angkorteam.mbaas.server.factory;
//
//import com.angkorteam.mbaas.server.spring.ApplicationContext;
//import org.springframework.beans.factory.FactoryBean;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.web.context.ServletContextAware;
//
//import javax.servlet.ServletContext;
//
///**
// * Created by Khauv Socheat on 2/4/2016.
// */
//public class JdbcTemplateFactoryBean implements FactoryBean<JdbcTemplate>, InitializingBean, ServletContextAware {
//
//    private JdbcTemplate jdbcTemplate;
//
//    private ServletContext servletContext;
//
//    @Override
//    public JdbcTemplate getObject() throws Exception {
//        return this.jdbcTemplate;
//    }
//
//    @Override
//    public Class<?> getObjectType() {
//        return JdbcTemplate.class;
//    }
//
//    @Override
//    public boolean isSingleton() {
//        return true;
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        ApplicationContext applicationContext = ApplicationContext.get(this.servletContext);
//        this.jdbcTemplate = applicationContext.getJdbcTemplate();
//    }
//
//    @Override
//    public void setServletContext(ServletContext servletContext) {
//        this.servletContext = servletContext;
//    }
//}
