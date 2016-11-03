//package com.angkorteam.mbaas.server.factory;
//
//import org.hyperic.sigar.Sigar;
//import org.springframework.beans.factory.DisposableBean;
//import org.springframework.beans.factory.FactoryBean;
//import org.springframework.beans.factory.InitializingBean;
//
///**
// * Created by socheat on 5/2/16.
// */
//public class SigarFactoryBean implements FactoryBean<Sigar>, InitializingBean, DisposableBean {
//
//    private Sigar sigar;
//
//    @Override
//    public Sigar getObject() throws Exception {
//        return this.sigar;
//    }
//
//    @Override
//    public Class<?> getObjectType() {
//        return Sigar.class;
//    }
//
//    @Override
//    public boolean isSingleton() {
//        return true;
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        this.sigar = new Sigar();
//    }
//
//    @Override
//    public void destroy() throws Exception {
//        this.sigar.close();
//    }
//}
