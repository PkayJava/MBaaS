package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.QueryRolePrivacyTable;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.plain.enums.QueryPermissionEnum;
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
public class QueryRolePrivacyProvider extends JooqProvider {

    private String queryId;

    private QueryRolePrivacyTable queryRolePrivacyTable = Tables.QUERY_ROLE_PRIVACY.as("queryRolePrivacyTable");
    private RoleTable roleTable = Tables.ROLE.as("roleTable");

    private TableLike<?> from;

    public QueryRolePrivacyProvider(String queryId) {
        this.queryId = queryId;
        this.from = queryRolePrivacyTable.join(roleTable).on(queryRolePrivacyTable.ROLE_ID.eq(roleTable.ROLE_ID));
    }

    public Field<String> getRoleId() {
        return this.queryRolePrivacyTable.ROLE_ID;
    }

    public Field<String> getQueryId() {
        return this.queryRolePrivacyTable.QUERY_ID;
    }

    public Field<Boolean> getDelete() {
        CaseValueStep<Integer> choose = DSL.choose(DSL.bitAnd(this.queryRolePrivacyTable.PERMISSON, QueryPermissionEnum.Delete.getLiteral()));
        Field<Boolean> when = choose.when(QueryPermissionEnum.Delete.getLiteral(), true).otherwise(false);
        return when;
    }

    public Field<Boolean> getModify() {
        CaseValueStep<Integer> choose = DSL.choose(DSL.bitAnd(this.queryRolePrivacyTable.PERMISSON, QueryPermissionEnum.Modify.getLiteral()));
        Field<Boolean> when = choose.when(QueryPermissionEnum.Modify.getLiteral(), true).otherwise(false);
        return when;
    }

    public Field<Boolean> getExecute() {
        CaseValueStep<Integer> choose = DSL.choose(DSL.bitAnd(this.queryRolePrivacyTable.PERMISSON, QueryPermissionEnum.Execute.getLiteral()));
        Field<Boolean> when = choose.when(QueryPermissionEnum.Execute.getLiteral(), true).otherwise(false);
        return when;
    }

    public Field<Boolean> getRead() {
        CaseValueStep<Integer> choose = DSL.choose(DSL.bitAnd(this.queryRolePrivacyTable.PERMISSON, QueryPermissionEnum.Read.getLiteral()));
        Field<Boolean> when = choose.when(QueryPermissionEnum.Read.getLiteral(), true).otherwise(false);
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
        where.add(this.queryRolePrivacyTable.QUERY_ID.eq(queryId));
        return where;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}
