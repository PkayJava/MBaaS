package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.List;

/**
 * Created by socheat on 5/28/16.
 */
public class PageProvider extends JooqProvider {

    private TableLike<?> from;

    private final String applicationCode;

    private Table<?> pageTable;

    public PageProvider(String applicationCode) {
        this.applicationCode = applicationCode;
        this.pageTable = DSL.table(Jdbc.PAGE).as("pageTable");
        this.from = this.pageTable;
    }

    public Field<String> getPageId() {
        return DSL.field(this.pageTable.getName() + "." + Jdbc.Page.PAGE_ID, String.class);
    }

    public Field<String> getSecurity() {
        return DSL.field(this.pageTable.getName() + "." + Jdbc.Page.SECURITY, String.class);
    }

    public Field<String> getUserId() {
        return DSL.field(this.pageTable.getName() + "." + Jdbc.Page.USER_ID, String.class);
    }

    public Field<String> getTitle() {
        return DSL.field(this.pageTable.getName() + "." + Jdbc.Page.TITLE, String.class);
    }

    public Field<String> getDescription() {
        return DSL.field(this.pageTable.getName() + "." + Jdbc.Page.DESCRIPTION, String.class);
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
