package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.UserPrivacyTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
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
    private AttributeTable masterAttributeTable = Tables.ATTRIBUTE.as("masterAttributeTable");
    private UserPrivacyTable userPrivacyTable = Tables.USER_PRIVACY.as("userPrivacyTable");

    private TableLike<?> from;

    private String collectionId;
    private String userId;

    public UserAttributeProvider(String userId) {
        DSLContext context = getDSLContext();
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        this.userId = userId;
        this.collectionId = context.select(collectionTable.COLLECTION_ID).from(collectionTable).where(collectionTable.NAME.eq(Tables.USER.getName())).fetchOneInto(String.class);
        this.from = attributeTable.leftJoin(masterAttributeTable).on(attributeTable.VIRTUAL_ATTRIBUTE_ID.eq(masterAttributeTable.ATTRIBUTE_ID));
    }

    public Field<String> getName() {
        return this.attributeTable.NAME;
    }

    public Field<String> getAttributeId() {
        return this.attributeTable.ATTRIBUTE_ID;
    }

    public Field<String> getJavaType() {
        return this.attributeTable.JAVA_TYPE;
    }

    public Field<String> getSqlType() {
        return this.attributeTable.SQL_TYPE;
    }

    public Field<Boolean> getVirtual() {
        return this.attributeTable.VIRTUAL;
    }

    public Field<String> getVirtualAttribute() {
        return this.masterAttributeTable.NAME.as("masterAttributeName");
    }

    public Field<Boolean> getSystem() {
        return this.attributeTable.SYSTEM;
    }

    public Field<Boolean> getExposed() {
        return this.attributeTable.EXPOSED;
    }

    public Field<Boolean> getNullable() {
        return this.attributeTable.NULLABLE;
    }

    public Field<Boolean> getAutoIncrement() {
        return this.attributeTable.AUTO_INCREMENT;
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
