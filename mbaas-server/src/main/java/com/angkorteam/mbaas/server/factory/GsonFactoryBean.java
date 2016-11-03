//package com.angkorteam.mbaas.server.factory;
//
//import com.angkorteam.mbaas.server.spring.ApplicationContext;
//import com.google.gson.Gson;
//import org.springframework.beans.factory.FactoryBean;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.web.context.ServletContextAware;
//
//import javax.servlet.ServletContext;
//
///**
// * Created by socheat on 5/14/16.
// */
//public class GsonFactoryBean implements FactoryBean<Gson>, InitializingBean, ServletContextAware {
//
//    private Gson gson;
//
//    private ServletContext servletContext;
//
//    @Override
//    public Gson getObject() throws Exception {
//        return this.gson;
//    }
//
//    @Override
//    public Class<?> getObjectType() {
//        return Gson.class;
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
//        this.gson = applicationContext.getGson();
//    }
//
//    @Override
//    public void setServletContext(ServletContext servletContext) {
//        this.servletContext = servletContext;
//    }
//}
