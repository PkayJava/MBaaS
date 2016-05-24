package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by socheat on 3/1/16.
 */
public class CollectionProvider extends JooqProvider {

    private TableLike<?> from;

    private final String applicationCode;

    private Table<?> collectionTable;
    private Table<?> userTable;

    public CollectionProvider(String applicationCode) {
        this.applicationCode = applicationCode;
        this.collectionTable = DSL.table(Jdbc.COLLECTION).as("collectionTable");
        this.userTable = DSL.table(Jdbc.APPLICATION_USER).as("userTable");
        this.from = this.collectionTable.join(this.userTable).on(DSL.field(this.collectionTable.getName() + "." + Jdbc.Collection.OWNER_APPLICATION_USER_ID, String.class).eq(DSL.field(this.userTable.getName() + "." + Jdbc.ApplicationUser.APPLICATION_USER_ID, String.class)));
    }

    public Field<String> getApplicationUser() {
        return DSL.field(this.userTable.getName() + "." + Jdbc.ApplicationUser.LOGIN, String.class);
    }

    public Field<String> getApplicationUserId() {
        return DSL.field(this.userTable.getName() + "." + Jdbc.ApplicationUser.APPLICATION_USER_ID, String.class);
    }

    public Field<Boolean> getSystem() {
        return DSL.field(this.collectionTable.getName() + "." + Jdbc.ApplicationUser.SYSTEM, Boolean.class);
    }

    public Field<String> getName() {
        return DSL.field(this.collectionTable.getName() + "." + Jdbc.Collection.NAME, String.class);
    }

    public Field<String> getCollectionId() {
        return DSL.field(this.collectionTable.getName() + "." + Jdbc.Collection.COLLECTION_ID, String.class);
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
    }

    @Override
    protected List<Condition> where() {
        List<Condition> where = new ArrayList<>();
        where.add(DSL.field(this.collectionTable.getName() + "." + Jdbc.Collection.SYSTEM, Boolean.class).eq(false));
        return where;
    }

    @Override
    protected DSLContext getDSLContext() {
        Application application = ApplicationUtils.getApplication();
        return application.getDSLContext(this.applicationCode);
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}
