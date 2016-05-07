package com.angkorteam.mbaas.server.xmpp;

import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by socheat on 5/6/16.
 */
public class OpenStorageProviderRegistry extends org.apache.vysper.storage.OpenStorageProviderRegistry {

    public OpenStorageProviderRegistry(final DSLContext context, final JdbcTemplate jdbcTemplate) {
        add(new AccountManagement(context, jdbcTemplate));
        add(new OfflineStorageProvider());
        add(new PrivateDataPersistenceManager());
        add(new RosterManager(context, jdbcTemplate));
        add(new UserAuthorization(context, jdbcTemplate));
        add(new VcardTempPersistenceManager());
    }
}
