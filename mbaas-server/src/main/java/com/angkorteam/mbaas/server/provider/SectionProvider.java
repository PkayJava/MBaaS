package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.SectionTable;
import com.angkorteam.mbaas.server.Spring;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.List;

/**
 * Created by socheat on 10/26/16.
 */
public class SectionProvider extends JooqProvider {

    private TableLike<?> from;

    private SectionTable sectionTable = null;

    public SectionProvider() {
        this.sectionTable = Tables.SECTION.as("sectionTable");
        this.from = this.sectionTable;
    }

    public Field<String> getSectionId() {
        return this.sectionTable.SECTION_ID;
    }

    public Field<String> getTitle() {
        return this.sectionTable.TITLE;
    }

    public Field<Boolean> getSystem() {
        return this.sectionTable.SYSTEM;
    }

    public Field<Integer> getOrder() {
        return this.sectionTable.ORDER;
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
