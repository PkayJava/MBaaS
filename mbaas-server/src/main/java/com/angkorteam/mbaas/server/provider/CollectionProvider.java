package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by socheat on 3/1/16.
 */
public class CollectionProvider extends JooqProvider {

    private Field<Integer> document;

    private TableLike<?> from;

    private CollectionTable collectionTable;

    private UserTable userTable;

    private String ownerUserId;

    public CollectionProvider() {
        this(null);
    }

    public CollectionProvider(String ownerUserId) {
        this.ownerUserId = ownerUserId;
        this.collectionTable = Tables.COLLECTION.as("collectionTable");
        this.userTable = Tables.USER.as("userTable");
        {
            DSLContext context = getDSLContext();
            List<String> names = context.select(collectionTable.NAME).from(collectionTable).fetchInto(String.class);
            CaseValueStep<String> choose = DSL.choose(collectionTable.NAME);
            String first = names.remove(0);
            CaseWhenStep when = choose.when(first, context.selectCount().from(DSL.table("`" + first + "`")).asField());
            for (String name : names) {
                when = when.when(name, context.selectCount().from(DSL.table("`" + name + "`")).asField());
            }
            document = when;
        }
        this.from = collectionTable.join(userTable).on(collectionTable.OWNER_USER_ID.eq(userTable.USER_ID));
    }

    public Field<String> getOwnerUser() {
        return this.userTable.LOGIN;
    }

    public Field<String> getOwnerUserId() {
        return this.userTable.USER_ID;
    }

    public Field<Integer> getDocument() {
        return this.document;
    }

    public Field<Boolean> getSystem() {
        return this.collectionTable.SYSTEM;
    }

    public Field<String> getName() {
        return this.collectionTable.NAME;
    }

    public Field<String> getCollectionId() {
        return this.collectionTable.COLLECTION_ID;
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
    }

    @Override
    protected List<Condition> where() {
        List<Condition> where = new ArrayList<>();
        where.add(this.collectionTable.SYSTEM.eq(false));
        if (ownerUserId != null) {
            where.add(this.userTable.USER_ID.eq(ownerUserId));
        }
        return where;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}
