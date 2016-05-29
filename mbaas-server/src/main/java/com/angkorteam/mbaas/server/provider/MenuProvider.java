package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.provider.JooqProvider;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by socheat on 5/29/16.
 */
public class MenuProvider extends JooqProvider {

    private final String applicationCode;

    private TableLike<?> from;

    private Table<?> menuTable;

    public MenuProvider(String applicationCode) {
        this.applicationCode = applicationCode;
        this.menuTable = DSL.table(Jdbc.MENU).asTable("menuTable");
        this.from = this.menuTable;
    }

    public Field<String> getMenuId() {
        return DSL.field(this.menuTable.getName() + "." + Jdbc.Menu.MENU_ID, String.class);
    }


    public Field<String> getTitle() {
        return DSL.field(this.menuTable.getName() + "." + Jdbc.Menu.TITLE, String.class);
    }

    public Field<Date> getDateCreated() {
        return DSL.field(this.menuTable.getName() + "." + Jdbc.Menu.DATE_CREATED, Date.class);
    }

    public Field<String> getUserId() {
        return DSL.field(this.menuTable.getName() + "." + Jdbc.Menu.USER_ID, String.class);
    }

    @Override
    protected TableLike<?> from() {
        return this.from;
    }

    @Override
    protected DSLContext getDSLContext() {
        Application application = ApplicationUtils.getApplication();
        return application.getDSLContext(this.applicationCode);
    }

    @Override
    protected List<Condition> where() {
        List<Condition> wheres = new ArrayList<>();
        wheres.add(DSL.field(this.menuTable.getName() + "." + Jdbc.Menu.PARENT_MENU_ID, String.class).isNotNull());
        return wheres;
    }

    @Override
    protected List<Condition> having() {
        return null;
    }
}
