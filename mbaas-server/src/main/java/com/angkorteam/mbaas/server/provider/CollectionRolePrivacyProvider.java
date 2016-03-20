package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.CollectionRolePrivacyTable;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
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
public class CollectionRolePrivacyProvider extends JooqProvider {

    private String collectionId;

    private CollectionRolePrivacyTable collectionRolePrivacyTable = Tables.COLLECTION_ROLE_PRIVACY.as("collectionRolePrivacyTable");
    private RoleTable roleTable = Tables.ROLE.as("roleTable");

    private TableLike<?> from;

    public CollectionRolePrivacyProvider(String collectionId) {
        this.collectionId = collectionId;
        this.from = collectionRolePrivacyTable.leftJoin(roleTable).on(collectionRolePrivacyTable.ROLE_ID.eq(roleTable.ROLE_ID));
    }

    public Field<String> getRoleId() {
        return this.collectionRolePrivacyTable.ROLE_ID;
    }

    public Field<String> getCollectionId() {
        return this.collectionRolePrivacyTable.COLLECTION_ID;
    }

    public Field<Boolean> getAttribute() {
        CaseValueStep<Integer> choose = DSL.choose(DSL.bitAnd(this.collectionRolePrivacyTable.PERMISSON, CollectionPermissionEnum.Attribute.getLiteral()));
        Field<Boolean> when = choose.when(CollectionPermissionEnum.Attribute.getLiteral(), true).otherwise(false);
        return when;
    }

    public Field<Boolean> getInsert() {
        CaseValueStep<Integer> choose = DSL.choose(DSL.bitAnd(this.collectionRolePrivacyTable.PERMISSON, CollectionPermissionEnum.Insert.getLiteral()));
        Field<Boolean> when = choose.when(CollectionPermissionEnum.Insert.getLiteral(), true).otherwise(false);
        return when;
    }

    public Field<Boolean> getDrop() {
        CaseValueStep<Integer> choose = DSL.choose(DSL.bitAnd(this.collectionRolePrivacyTable.PERMISSON, CollectionPermissionEnum.Drop.getLiteral()));
        Field<Boolean> when = choose.when(CollectionPermissionEnum.Drop.getLiteral(), true).otherwise(false);
        return when;
    }

    public Field<Boolean> getRead() {
        CaseValueStep<Integer> choose = DSL.choose(DSL.bitAnd(this.collectionRolePrivacyTable.PERMISSON, CollectionPermissionEnum.Read.getLiteral()));
        Field<Boolean> when = choose.when(CollectionPermissionEnum.Read.getLiteral(), true).otherwise(false);
        return when;
    }

    public Field<String> getRoleName() {
        return this.roleTable.NAME;
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
    }

    @Override
    protected List<Condition> where() {
        List<Condition> where = new ArrayList<>();
        where.add(this.collectionRolePrivacyTable.COLLECTION_ID.eq(collectionId));
        return where;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}
