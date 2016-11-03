package com.angkorteam.mbaas.server.select2;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.MultipleChoiceProvider;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.RolePojo;
import com.angkorteam.mbaas.server.Spring;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.model.IModel;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by socheat on 7/5/16.
 */
public class RolesChoiceProvider extends MultipleChoiceProvider<RolePojo> {

    public RolesChoiceProvider() {
    }

    @Override
    public List<RolePojo> toChoices(List<String> ids) {
        DSLContext context = Spring.getBean(DSLContext.class);
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        List<RolePojo> pages = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.in(ids)).fetchInto(RolePojo.class);
        return pages;
    }

    @Override
    public List<Option> query(String term, int page) {
        List<Option> options = new ArrayList<>();
        DSLContext context = Spring.getBean(DSLContext.class);
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        List<RolePojo> pages = context.select(roleTable.fields()).from(roleTable).where(DSL.lower(roleTable.NAME).like(StringUtils.lowerCase(term + "%"))).orderBy(roleTable.NAME.asc()).limit((page - 1) * LIMIT, LIMIT).fetchInto(RolePojo.class);
        for (RolePojo p : pages) {
            options.add(new Option(p.getRoleId(), p.getName()));
        }
        return options;
    }

    @Override
    public boolean hasMore(String term, int page) {
        return true;
    }

    @Override
    public Gson getGson() {
        return Spring.getBean("gson", Gson.class);
    }

    @Override
    public int size() {
        DSLContext context = Spring.getBean(DSLContext.class);
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        return context.selectCount().from(roleTable).fetchOneInto(int.class);
    }

    @Override
    public RolePojo get(int index) {
        DSLContext context = Spring.getBean(DSLContext.class);
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        return context.select(roleTable.fields()).from(roleTable).orderBy(roleTable.NAME.asc()).limit(index, 1).fetchOneInto(RolePojo.class);
    }

    @Override
    public Object getDisplayValue(RolePojo object) {
        return object.getName();
    }

    @Override
    public String getIdValue(RolePojo object, int index) {
        return object.getRoleId();
    }

    @Override
    public RolePojo getObject(String id, IModel<? extends List<? extends RolePojo>> choices) {
        DSLContext context = Spring.getBean(DSLContext.class);
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        return context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(id)).fetchOneInto(RolePojo.class);
    }


}
