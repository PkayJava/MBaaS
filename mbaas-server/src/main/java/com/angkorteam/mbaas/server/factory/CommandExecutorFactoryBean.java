package com.angkorteam.mbaas.server.factory;

import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by Khauv Socheat on 4/21/2016.
 */
public class CommandExecutorFactoryBean implements FactoryBean<Executor>, InitializingBean {
    private Executor executor;

    @Override
    public Executor getObject() throws Exception {
        return this.executor;
    }

    @Override
    public Class<?> getObjectType() {
        return Executor.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.executor = new DefaultExecutor();
        ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
        this.executor.setWatchdog(watchdog);
        this.executor.setWorkingDirectory(FileUtils.getTempDirectory());
    }
}
