package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.MenuTable;
import com.angkorteam.mbaas.model.entity.tables.SectionTable;
import com.angkorteam.mbaas.server.Spring;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.TableLike;

import java.util.List;

/**
 * Created by socheat on 10/27/16.
 */
public class MenuProvider extends JooqProvider {

    private TableLike<?> from;

    private MenuTable menuTable;

    private SectionTable sectionTable;

    private MenuTable parentMenuTable;

    public MenuProvider() {
        this.menuTable = Tables.MENU.as("menuTable");
        this.sectionTable = Tables.SECTION.as("sectionTable");
        this.parentMenuTable = Tables.MENU.as("parentMenuTable");
        this.from = this.menuTable.leftJoin(this.parentMenuTable).on(this.menuTable.PARENT_MENU_ID.eq(this.parentMenuTable.MENU_ID))
                .leftJoin(this.sectionTable).on(this.menuTable.SECTION_ID.eq(this.sectionTable.SECTION_ID));
    }

    public Field<String> getMenuId() {
        return this.menuTable.MENU_ID;
    }

    public Field<String> getTitle() {
        return this.menuTable.TITLE.as("menuTitle");
    }

    public Field<Boolean> getSystem() {
        return this.menuTable.SYSTEM;
    }

    public Field<String> getIcon() {
        return this.menuTable.ICON;
    }

    public Field<String> getSection() {
        return this.sectionTable.TITLE.as("sectionTitle");
    }

    public Field<String> getParent() {
        return this.parentMenuTable.PATH.as("parentPath");
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
