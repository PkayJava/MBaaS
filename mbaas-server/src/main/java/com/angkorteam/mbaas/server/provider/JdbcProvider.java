package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.AbstractJdbcProvider;
import com.angkorteam.mbaas.server.Spring;

import javax.sql.DataSource;

/**
 * Created by socheat on 12/8/16.
 */
public class JdbcProvider extends AbstractJdbcProvider {

    public JdbcProvider(String from) {
        super(from);
    }

    @Override
    protected DataSource getDataSource() {
        return Spring.getBean(DataSource.class);
    }

}
