package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.LayoutTable;
import com.angkorteam.mbaas.server.Spring;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 10/26/16.
 */
public class LayoutProvider extends JooqProvider {

    private TableLike<?> from;

    private LayoutTable layoutTable = null;

    public LayoutProvider() {
        this.layoutTable = Tables.LAYOUT.as("layoutTable");
        this.from = this.layoutTable;
    }

    public Field<String> getLayoutId() {
        return this.layoutTable.LAYOUT_ID;
    }

    public Field<String> getTitle() {
        return this.layoutTable.TITLE;
    }

    public Field<String> getDescription() {
        return this.layoutTable.DESCRIPTION;
    }

    public Field<Boolean> getSystem() {
        return this.layoutTable.SYSTEM;
    }

    public Field<Date> getDateCreated() {
        return this.layoutTable.DATE_CREATED;
    }

    public Field<Date> getDateModifies() {
        return this.layoutTable.DATE_MODIFIED;
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

    @Override
    protected DSLContext getDSLContext() {
        return Spring.getBean(DSLContext.class);
    }
}
