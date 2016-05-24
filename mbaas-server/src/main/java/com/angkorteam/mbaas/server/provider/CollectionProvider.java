package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.server.Jdbc;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableLike;
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
        this.from = this.collectionTable.join(this.userTable).on(collectionTable.field(Jdbc.Collection.OWNER_APPLICATION_USER_ID, String.class).eq(userTable.field(Jdbc.ApplicationUser.APPLICATION_USER_ID, String.class)));
    }

    public Field<String> getApplicationUser() {
        return this.userTable.field(Jdbc.ApplicationUser.LOGIN, String.class);
    }

    public Field<String> getApplicationUserId() {
        return this.userTable.field(Jdbc.ApplicationUser.APPLICATION_USER_ID, String.class);
    }

    public Field<Boolean> getSystem() {
        return this.collectionTable.field(Jdbc.ApplicationUser.SYSTEM, Boolean.class);
    }

    public Field<String> getName() {
        return this.collectionTable.field(Jdbc.Collection.NAME, String.class);
    }

    public Field<String> getCollectionId() {
        return this.collectionTable.field(Jdbc.Collection.COLLECTION_ID, String.class);
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
    }

    @Override
    protected List<Condition> where() {
        List<Condition> where = new ArrayList<>();
        where.add(this.collectionTable.field(Jdbc.Collection.SYSTEM, Boolean.class).eq(false));
        return where;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}
