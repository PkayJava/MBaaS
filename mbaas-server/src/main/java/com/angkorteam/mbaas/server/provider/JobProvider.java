package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.JobTable;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 3/25/16.
 */
public class JobProvider extends JooqProvider {

    private UserTable userTable = Tables.USER.as("userTable");
    private JobTable jobTable = Tables.JOB.as("jobTable");

    private TableLike<?> from;

    public JobProvider() {
        this.from = this.jobTable.join(this.userTable).on(this.jobTable.OWNER_USER_ID.eq(this.userTable.USER_ID));
    }

    public Field<String> getJobId() {
        return this.jobTable.JOB_ID;
    }

    public Field<String> getOwnerUser() {
        return this.userTable.LOGIN;
    }

    public Field<String> getSecurity() {
        return this.jobTable.SECURITY;
    }

    public Field<String> getName() {
        return this.jobTable.NAME;
    }

    public Field<String> getCron() {
        return this.jobTable.CRON;
    }

    public Field<String> getErrorMessage() {
        return this.jobTable.ERROR_MESSAGE;
    }

    public Field<String> getErrorClass() {
        return this.jobTable.ERROR_CLASS;
    }

    public Field<Date> getDateLastExecuted() {
        return this.jobTable.DATE_LAST_EXECUTED;
    }

    public Field<Date> getDateCreated() {
        return this.jobTable.DATE_CREATED;
    }

    @Override

    protected TableLike<?> from() {
        return from;
    }

    @Override
    protected List<Condition> where() {
        return null;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}