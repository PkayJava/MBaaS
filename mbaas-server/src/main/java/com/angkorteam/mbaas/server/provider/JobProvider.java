package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 3/25/16.
 */
public class JobProvider extends JooqProvider {

    private Table<?> userTable;
    private Table<?> jobTable;

    private TableLike<?> from;

    private final String applicationCode;

    public JobProvider(String applicationCode) {
        this.userTable = DSL.table(Jdbc.USER).as("userTable");
        this.jobTable = DSL.table(Jdbc.JOB).as("jobTable");
        this.applicationCode = applicationCode;
        this.from = this.jobTable.join(this.userTable).on(DSL.field(this.jobTable.getName() + "." + Jdbc.Job.USER_ID, String.class).eq(DSL.field(this.userTable.getName() + "." + Jdbc.User.USER_ID, String.class)));
    }

    public Field<String> getJobId() {
        return DSL.field(this.jobTable.getName() + "." + Jdbc.Job.JOB_ID, String.class);
    }

    public Field<String> getApplicationUser() {
        return DSL.field(this.userTable.getName() + "." + Jdbc.User.LOGIN, String.class);
    }

    public Field<String> getApplicationUserId() {
        return DSL.field(this.userTable.getName() + "." + Jdbc.Job.USER_ID, String.class);
    }

    public Field<String> getSecurity() {
        return DSL.field(this.jobTable.getName() + "." + Jdbc.Job.SECURITY, String.class);
    }

    public Field<Double> getConsume() {
        return DSL.field(this.jobTable.getName() + "." + Jdbc.Job.CONSUME, Double.class);
    }

    public Field<String> getName() {
        return DSL.field(this.jobTable.getName() + "." + Jdbc.Job.NAME, String.class);
    }

    public Field<String> getCron() {
        return DSL.field(this.jobTable.getName() + "." + Jdbc.Job.CRON, String.class);
    }

    public Field<String> getErrorMessage() {
        return DSL.field(this.jobTable.getName() + "." + Jdbc.Job.ERROR_MESSAGE, String.class);
    }

    public Field<String> getErrorClass() {
        return DSL.field(this.jobTable.getName() + "." + Jdbc.Job.ERROR_CLASS, String.class);
    }

    public Field<Date> getDateLastExecuted() {
        return DSL.field(this.jobTable.getName() + "." + Jdbc.Job.DATE_LAST_EXECUTED, Date.class);
    }

    public Field<Date> getDateCreated() {
        return DSL.field(this.jobTable.getName() + "." + Jdbc.Job.DATE_CREATED, Date.class);
    }

    @Override
    protected DSLContext getDSLContext() {
        Application application = ApplicationUtils.getApplication();
        return application.getDSLContext(this.applicationCode);
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