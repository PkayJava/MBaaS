package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.QueryUserPrivacyTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
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
public class QueryUserPrivacyProvider extends JooqProvider {

    private String queryId;

    private QueryUserPrivacyTable queryUserPrivacyTable = Tables.QUERY_USER_PRIVACY.as("queryUserPrivacyTable");
    private UserTable userTable = Tables.USER.as("userTable");

    private TableLike<?> from;

    public QueryUserPrivacyProvider(String queryId) {
        this.queryId = queryId;
        this.from = queryUserPrivacyTable.join(userTable).on(queryUserPrivacyTable.USER_ID.eq(userTable.USER_ID));
    }

    public Field<String> getUserId() {
        return this.queryUserPrivacyTable.USER_ID;
    }

    public Field<String> getQueryId() {
        return this.queryUserPrivacyTable.QUERY_ID;
    }

    public Field<Boolean> getExecute() {
        CaseValueStep<Integer> choose = DSL.choose(DSL.bitAnd(this.queryUserPrivacyTable.PERMISSON, QueryPermissionEnum.Execute.getLiteral()));
        Field<Boolean> when = choose.when(QueryPermissionEnum.Execute.getLiteral(), true).otherwise(false);
        return when;
    }

    public Field<Boolean> getDelete() {
        CaseValueStep<Integer> choose = DSL.choose(DSL.bitAnd(this.queryUserPrivacyTable.PERMISSON, QueryPermissionEnum.Delete.getLiteral()));
        Field<Boolean> when = choose.when(QueryPermissionEnum.Delete.getLiteral(), true).otherwise(false);
        return when;
    }

    public Field<Boolean> getModify() {
        CaseValueStep<Integer> choose = DSL.choose(DSL.bitAnd(this.queryUserPrivacyTable.PERMISSON, QueryPermissionEnum.Modify.getLiteral()));
        Field<Boolean> when = choose.when(QueryPermissionEnum.Modify.getLiteral(), true).otherwise(false);
        return when;
    }

    public Field<Boolean> getRead() {
        CaseValueStep<Integer> choose = DSL.choose(DSL.bitAnd(this.queryUserPrivacyTable.PERMISSON, QueryPermissionEnum.Read.getLiteral()));
        Field<Boolean> when = choose.when(QueryPermissionEnum.Read.getLiteral(), true).otherwise(false);
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
        where.add(this.queryUserPrivacyTable.QUERY_ID.eq(queryId));
        return where;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}