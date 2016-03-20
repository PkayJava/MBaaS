package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.CollectionUserPrivacyTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.plain.enums.CollectionPermissionEnum;
import org.jooq.CaseValueStep;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.TableLike;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by socheat on 3/20/16.
 */
public class CollectionUserPrivacyProvider extends JooqProvider {

    private String collectionId;

    private CollectionUserPrivacyTable collectionUserPrivacyTable = Tables.COLLECTION_USER_PRIVACY.as("collectionUserPrivacyTable");
    private UserTable userTable = Tables.USER.as("userTable");

    private TableLike<?> from;

    public CollectionUserPrivacyProvider(String collectionId) {
        this.collectionId = collectionId;
        this.from = collectionUserPrivacyTable.join(userTable).on(collectionUserPrivacyTable.USER_ID.eq(userTable.USER_ID));
    }

    public Field<String> getUserId() {
        return this.collectionUserPrivacyTable.USER_ID;
    }

    public Field<String> getCollectionId() {
        return this.collectionUserPrivacyTable.COLLECTION_ID;
    }

    public Field<Boolean> getAttribute() {
        CaseValueStep<Integer> choose = DSL.choose(DSL.bitAnd(this.collectionUserPrivacyTable.PERMISSON, CollectionPermissionEnum.Attribute.getLiteral()));
        Field<Boolean> when = choose.when(CollectionPermissionEnum.Attribute.getLiteral(), true).otherwise(false);
        return when;
    }

    public Field<Boolean> getInsert() {
        CaseValueStep<Integer> choose = DSL.choose(DSL.bitAnd(this.collectionUserPrivacyTable.PERMISSON, CollectionPermissionEnum.Insert.getLiteral()));
        Field<Boolean> when = choose.when(CollectionPermissionEnum.Insert.getLiteral(), true).otherwise(false);
        return when;
    }

    public Field<Boolean> getDrop() {
        CaseValueStep<Integer> choose = DSL.choose(DSL.bitAnd(this.collectionUserPrivacyTable.PERMISSON, CollectionPermissionEnum.Drop.getLiteral()));
        Field<Boolean> when = choose.when(CollectionPermissionEnum.Drop.getLiteral(), true).otherwise(false);
        return when;
    }

    public Field<Boolean> getRead() {
        CaseValueStep<Integer> choose = DSL.choose(DSL.bitAnd(this.collectionUserPrivacyTable.PERMISSON, CollectionPermissionEnum.Read.getLiteral()));
        Field<Boolean> when = choose.when(CollectionPermissionEnum.Read.getLiteral(), true).otherwise(false);
        return when;
    }

    public Field<String> getLogin() {
        return this.userTable.LOGIN;
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
    }

    @Override
    protected List<Condition> where() {
        List<Condition> where = new ArrayList<>();
        where.add(this.collectionUserPrivacyTable.COLLECTION_ID.eq(collectionId));
        return where;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}