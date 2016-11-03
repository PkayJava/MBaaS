package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.server.bean.AuthorizationStrategy;
import com.angkorteam.mbaas.server.bean.System;
import org.jooq.DSLContext;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by socheat on 11/3/16.
 */
public class AuthorizationStrategyFactoryBean implements FactoryBean<AuthorizationStrategy>, InitializingBean {

    private AuthorizationStrategy authorizationStrategy;

    private DSLContext context;

    private System system;

    @Override
    public AuthorizationStrategy getObject() throws Exception {
        return this.authorizationStrategy;
    }

    @Override
    public Class<?> getObjectType() {
        return AuthorizationStrategy.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.authorizationStrategy = new AuthorizationStrategy(this.context, this.system);
    }

    public void setContext(DSLContext context) {
        this.context = context;
    }

    public void setSystem(System system) {
        this.system = system;
    }
}
