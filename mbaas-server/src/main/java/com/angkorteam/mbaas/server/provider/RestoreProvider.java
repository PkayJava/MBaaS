package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 7/11/16.
 */
public class RestoreProvider extends JooqProvider {

    private final String applicationCode;

    private Table<?> restoreTable;

    private TableLike<?> from;

    public RestoreProvider(String applicationCode) {
        this.applicationCode = applicationCode;
        this.restoreTable = DSL.table("restore").as("restoreTable");
        this.from = this.restoreTable;
        setSort("dateCreated", SortOrder.DESCENDING);
    }

    public Field<String> getRestoreId() {
        return DSL.field(this.restoreTable.getName() + "." + Jdbc.Restore.RESTORE_ID, String.class);
    }

    public Field<String> getTableName() {
        return DSL.field(this.restoreTable.getName() + "." + Jdbc.Restore.TABLE_NAME, String.class);
    }

    public Field<Date> getDateCreated() {
        return DSL.field(this.restoreTable.getName() + "." + Jdbc.Restore.DATE_CREATED, Date.class);
    }

    @Override
    protected DSLContext getDSLContext() {
        Application application = ApplicationUtils.getApplication();
        return application.getDSLContext(this.applicationCode);
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
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
