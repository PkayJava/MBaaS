package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.MenuItemTable;
import com.angkorteam.mbaas.model.entity.tables.MenuTable;
import com.angkorteam.mbaas.model.entity.tables.PageTable;
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
public class MenuItemProvider extends JooqProvider {

    private TableLike<?> from;

    private MenuTable menuTable;

    private SectionTable sectionTable;

    private MenuItemTable menuItemTable;

    private PageTable pageTable;

    public MenuItemProvider() {
        this.menuTable = Tables.MENU.as("menuTable");
        this.sectionTable = Tables.SECTION.as("sectionTable");
        this.pageTable = Tables.PAGE.as("pageTable");
        this.menuItemTable = Tables.MENU_ITEM.as("menuItemTable");
        this.from = this.menuItemTable.leftJoin(this.menuTable).on(this.menuItemTable.MENU_ID.eq(this.menuTable.MENU_ID))
                .leftJoin(this.sectionTable).on(this.menuItemTable.SECTION_ID.eq(this.sectionTable.SECTION_ID))
                .leftJoin(this.pageTable).on(this.menuItemTable.PAGE_ID.eq(this.pageTable.PAGE_ID));
    }

    public Field<String> getMenuItemId() {
        return this.menuItemTable.MENU_ITEM_ID;
    }

    public Field<String> getTitle() {
        return this.menuItemTable.TITLE.as("menuItemTitle");
    }

    public Field<Boolean> getSystem() {
        return this.menuItemTable.SYSTEM;
    }

    public Field<String> getIcon() {
        return this.menuItemTable.ICON;
    }

    public Field<String> getSection() {
        return this.sectionTable.TITLE.as("sectionTitle");
    }

    public Field<String> getMenu() {
        return this.menuTable.PATH.as("menuPath");
    }

    public Field<String> getPage() {
        return this.pageTable.TITLE.as("pageTitle");
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
