//package com.angkorteam.mbaas.server.factory;
//
//import com.angkorteam.mbaas.server.spring.ApplicationContext;
//import org.springframework.beans.factory.FactoryBean;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.scheduling.TaskScheduler;
//import org.springframework.web.context.ServletContextAware;
//
//import javax.servlet.ServletContext;
//
///**
// * Created by socheat on 4/25/16.
// */
//public class TaskSchedulerFactoryBean implements FactoryBean<TaskScheduler>, InitializingBean, ServletContextAware {
//
//    private ServletContext servletContext;
//
//    private TaskScheduler scheduler;
//
//    @Override
//    public TaskScheduler getObject() throws Exception {
//        return this.scheduler;
//    }
//
//    @Override
//    public Class<?> getObjectType() {
//        return TaskScheduler.class;
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
//        this.scheduler = applicationContext.getScheduler();
//    }
//
//    @Override
//    public void setServletContext(ServletContext servletContext) {
//        this.servletContext = servletContext;
//    }
//}
