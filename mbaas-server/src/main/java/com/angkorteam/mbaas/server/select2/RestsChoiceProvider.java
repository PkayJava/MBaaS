package com.angkorteam.mbaas.server.select2;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.MultipleChoiceProvider;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RestTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.RestPojo;
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
public class RestsChoiceProvider extends MultipleChoiceProvider<RestPojo> {

    public RestsChoiceProvider() {
    }

    @Override
    public List<RestPojo> toChoices(List<String> ids) {
        DSLContext context = Spring.getBean(DSLContext.class);
        RestTable restTable = Tables.REST.as("restTable");
        List<RestPojo> pages = context.select(restTable.fields()).from(restTable).where(restTable.REST_ID.in(ids)).fetchInto(RestPojo.class);
        return pages;
    }

    @Override
    public List<Option> query(String term, int page) {
        List<Option> options = new ArrayList<>();
        DSLContext context = Spring.getBean(DSLContext.class);
        RestTable restTable = Tables.REST.as("restTable");
        List<RestPojo> rests = context.select(restTable.fields()).from(restTable).where(DSL.lower(restTable.NAME).like(StringUtils.lowerCase(term + "%"))).orderBy(restTable.NAME.asc()).limit((page - 1) * LIMIT, LIMIT).fetchInto(RestPojo.class);
        for (RestPojo p : rests) {
            options.add(new Option(p.getRestId(), p.getName()));
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
        RestTable restTable = Tables.REST.as("restTable");
        return context.selectCount().from(restTable).fetchOneInto(int.class);
    }

    @Override
    public RestPojo get(int index) {
        DSLContext context = Spring.getBean(DSLContext.class);
        RestTable restTable = Tables.REST.as("restTable");
        return context.select(restTable.fields()).from(restTable).orderBy(restTable.NAME.asc()).limit(index, 1).fetchOneInto(RestPojo.class);
    }

    @Override
    public Object getDisplayValue(RestPojo object) {
        return object.getName();
    }

    @Override
    public String getIdValue(RestPojo object, int index) {
        return object.getRestId();
    }

    @Override
    public RestPojo getObject(String id, IModel<? extends List<? extends RestPojo>> choices) {
        DSLContext context = Spring.getBean(DSLContext.class);
        RestTable restTable = Tables.REST.as("restTable");
        return context.select(restTable.fields()).from(restTable).where(restTable.REST_ID.eq(id)).fetchOneInto(RestPojo.class);
    }


}
