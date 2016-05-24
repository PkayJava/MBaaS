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
        this.userTable = DSL.table(Jdbc.APPLICATION_USER).as("userTable");
        this.jobTable = DSL.table(Jdbc.JOB).as("jobTable");
        this.applicationCode = applicationCode;
        this.from = this.jobTable.join(this.userTable).on(this.jobTable.field(Jdbc.Job.APPLICATION_USER_ID, String.class).eq(this.userTable.field(Jdbc.ApplicationUser.APPLICATION_USER_ID, String.class)));
    }

    public Field<String> getJobId() {
        return this.jobTable.field(Jdbc.Job.JOB_ID, String.class);
    }

    public Field<String> getApplicationUser() {
        return this.userTable.field(Jdbc.ApplicationUser.LOGIN, String.class);
    }

    public Field<String> getApplicationUserId() {
        return this.userTable.field(Jdbc.Job.APPLICATION_USER_ID, String.class);
    }

    public Field<String> getSecurity() {
        return this.jobTable.field(Jdbc.Job.SECURITY, String.class);
    }

    public Field<Double> getConsume() {
        return this.jobTable.field(Jdbc.Job.CONSUME, Double.class);
    }

    public Field<String> getName() {
        return this.jobTable.field(Jdbc.Job.NAME, String.class);
    }

    public Field<String> getCron() {
        return this.jobTable.field(Jdbc.Job.CRON, String.class);
    }

    public Field<String> getErrorMessage() {
        return this.jobTable.field(Jdbc.Job.ERROR_MESSAGE, String.class);
    }

    public Field<String> getErrorClass() {
        return this.jobTable.field(Jdbc.Job.ERROR_CLASS, String.class);
    }

    public Field<Date> getDateLastExecuted() {
        return this.jobTable.field(Jdbc.Job.DATE_LAST_EXECUTED, Date.class);
    }

    public Field<Date> getDateCreated() {
        return this.jobTable.field(Jdbc.Job.DATE_CREATED, Date.class);
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