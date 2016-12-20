package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.LayoutTable;
import com.angkorteam.mbaas.model.entity.tables.PageTable;
import com.angkorteam.mbaas.server.Spring;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 10/27/16.
 */
public class PageProvider extends JooqProvider {

    private TableLike<?> from;

    private PageTable pageTable;

    private LayoutTable layoutTable;

    public PageProvider() {
        this.pageTable = Tables.PAGE.as("pageTable");
        this.layoutTable = Tables.LAYOUT.as("layoutTable");
        this.from = this.pageTable.leftJoin(this.layoutTable).on(this.pageTable.LAYOUT_ID.eq(this.layoutTable.LAYOUT_ID));
    }

    public Field<String> getPageId() {
        return this.pageTable.PAGE_ID;
    }

    public Field<String> getLayout() {
        return this.layoutTable.TITLE.as("layoutTitle");
    }

    public Field<String> getTitle() {
        return this.pageTable.TITLE.as("pageTitle");
    }

    public Field<Boolean> getSystem() {
        return this.pageTable.SYSTEM;
    }

    public Field<Boolean> getCmsPage() {
        return this.pageTable.CMS_PAGE;
    }

    public Field<Date> getDateModified() {
        return this.pageTable.DATE_MODIFIED;
    }

    public Field<String> getDescription() {
        return this.pageTable.DESCRIPTION;
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
