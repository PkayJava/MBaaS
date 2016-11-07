package com.angkorteam.mbaas.server.factory;

import com.angkorteam.framework.extension.share.provider.IDataSourceProvider;
import com.angkorteam.mbaas.server.bean.DataSourceProvider;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by socheat on 11/6/16.
 */
public class DataSourceProviderFactoryBean implements FactoryBean<IDataSourceProvider>, InitializingBean {

    private IDataSourceProvider provider;

    @Override
    public IDataSourceProvider getObject() throws Exception {
        return this.provider;
    }

    @Override
    public Class<?> getObjectType() {
        return IDataSourceProvider.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.provider = new DataSourceProvider();
    }

}
