package com.angkorteam.mbaas.server.select2;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.MultipleChoiceProvider;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.PageTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.PagePojo;
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
public class PagesChoiceProvider extends MultipleChoiceProvider<PagePojo> {

    public PagesChoiceProvider() {
    }

    @Override
    public List<PagePojo> toChoices(List<String> ids) {
        DSLContext context = Spring.getBean(DSLContext.class);
        PageTable pageTable = Tables.PAGE.as("pageTable");
        List<PagePojo> pages = context.select(pageTable.fields()).from(pageTable).where(pageTable.PAGE_ID.in(ids)).fetchInto(PagePojo.class);
        return pages;
    }

    @Override
    public List<Option> query(String term, int page) {
        List<Option> options = new ArrayList<>();
        DSLContext context = Spring.getBean(DSLContext.class);
        PageTable pageTable = Tables.PAGE.as("pageTable");
        List<PagePojo> pages = context.select(pageTable.fields()).from(pageTable).where(DSL.lower(pageTable.TITLE).like(StringUtils.lowerCase(term + "%"))).orderBy(pageTable.TITLE.asc()).limit((page - 1) * LIMIT, LIMIT).fetchInto(PagePojo.class);
        for (PagePojo p : pages) {
            options.add(new Option(p.getPageId(), p.getTitle()));
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
    public Object getDisplayValue(PagePojo object) {
        return object.getTitle();
    }

    @Override
    public String getIdValue(PagePojo object, int index) {
        return object.getPageId();
    }

    @Override
    public PagePojo getObject(String id, IModel<? extends List<? extends PagePojo>> choices) {
        DSLContext context = Spring.getBean(DSLContext.class);
        PageTable pageTable = Tables.PAGE.as("pageTable");
        return context.select(pageTable.fields()).from(pageTable).where(pageTable.PAGE_ID.eq(id)).fetchOneInto(PagePojo.class);
    }


}
