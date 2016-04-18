package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.UserPrivacyTable;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by socheat on 3/7/16.
 */
public class UserAttributeProvider extends JooqProvider {

    private AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
    private UserPrivacyTable userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

    private TableLike<?> from;

    private String collectionId;
    private String userId;

    public UserAttributeProvider(String userId) {
        DSLContext context = getDSLContext();
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        this.userId = userId;
        this.collectionId = context.select(collectionTable.COLLECTION_ID).from(collectionTable).where(collectionTable.NAME.eq(Tables.USER.getName())).fetchOneInto(String.class);
        this.from = attributeTable;
    }

    public Field<String> getName() {
        return this.attributeTable.NAME;
    }

    public Field<String> getAttributeId() {
        return this.attributeTable.ATTRIBUTE_ID;
    }

    public Field<String> getAttributeType() {
        return this.attributeTable.ATTRIBUTE_TYPE;
    }

    public Field<Integer> getExtra() {
        return this.attributeTable.EXTRA;
    }


    public Field<String> getUserPrivacyId() {
        DSLContext context = getDSLContext();
        Field<String> scope = context.select(this.userPrivacyTable.USER_PRIVACY_ID).from(this.userPrivacyTable).where(this.userPrivacyTable.USER_ID.eq(userId)).and(this.userPrivacyTable.ATTRIBUTE_ID.eq(attributeTable.ATTRIBUTE_ID)).asField();
        return scope;
    }

    public Field<String> getScope() {
        DSLContext context = getDSLContext();
        Field<String> scope = context.select(this.userPrivacyTable.SCOPE).from(this.userPrivacyTable).where(this.userPrivacyTable.USER_ID.eq(userId)).and(this.userPrivacyTable.ATTRIBUTE_ID.eq(attributeTable.ATTRIBUTE_ID)).asField();
        return scope;
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
    }

    @Override
    protected List<Condition> where() {
        List<Condition> where = new ArrayList<>();
        where.add(attributeTable.COLLECTION_ID.eq(this.collectionId));
        return where;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}
