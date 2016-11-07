package com.angkorteam.mbaas.server.bean;

import com.angkorteam.framework.extension.share.provider.IDataSourceProvider;
import com.angkorteam.mbaas.server.Spring;

import javax.sql.DataSource;

/**
 * Created by socheat on 11/6/16.
 */
public class DataSourceProvider implements IDataSourceProvider {

    @Override
    public DataSource getDataSource() {
        return Spring.getBean(DataSource.class);
    }

}
