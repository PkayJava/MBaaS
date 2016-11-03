//package com.angkorteam.mbaas.server.factory;
//
//import com.angkorteam.mbaas.server.bean.JavaFilter;
//import jdk.nashorn.api.scripting.ClassFilter;
//import org.jooq.DSLContext;
//import org.springframework.beans.factory.FactoryBean;
//import org.springframework.beans.factory.InitializingBean;
//
///**
// * Created by socheat on 5/31/16.
// */
//public class ClassFilterFactoryBean implements FactoryBean<ClassFilter>, InitializingBean {
//
//    private JavaFilter javaFilter;
//
//    private DSLContext context;
//
//    @Override
//    public JavaFilter getObject() throws Exception {
//        return this.javaFilter;
//    }
//
//    @Override
//    public Class<?> getObjectType() {
//        return ClassFilter.class;
//    }
//
//    @Override
//    public boolean isSingleton() {
//        return true;
//    }
//
//    public void setContext(DSLContext context) {
//        this.context = context;
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        this.javaFilter = new JavaFilter(context);
//    }
//}
