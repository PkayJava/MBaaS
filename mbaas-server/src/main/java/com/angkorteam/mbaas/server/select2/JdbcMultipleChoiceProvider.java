package com.angkorteam.mbaas.server.select2;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.MultipleChoiceProvider;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
import com.angkorteam.mbaas.server.Spring;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.model.IModel;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by socheat on 12/5/16.
 */
public class JdbcMultipleChoiceProvider extends MultipleChoiceProvider<Item> {

    private final String table;

    private final String idField;

    private final String queryField;

    private final String labelField;

    private final List<String> where;

    public JdbcMultipleChoiceProvider(String table, String idField) {
        this(table, idField, idField);
    }

    public JdbcMultipleChoiceProvider(String table, String idField, String queryField) {
        this(table, idField, queryField, queryField);
    }

    public JdbcMultipleChoiceProvider(String table, String idField, String queryField, String labelField) {
        this.table = table;
        this.idField = idField;
        this.labelField = labelField;
        this.queryField = queryField;
        this.where = Lists.newArrayList();
    }

    public void addWhere(String filter) {
        this.where.add(filter);
    }

    @Override
    public List<Item> toChoices(List<String> list) {
        Sql2o sql2o = Spring.getBean(Sql2o.class);
        try (Connection connection = sql2o.open()) {
            List<String> where = new ArrayList<>();
            where.addAll(this.where);
            where.add(this.idField + " IN (:id)");
            Query query = connection.createQuery("SELECT " + this.idField + " id, " + this.labelField + " value " + " FROM " + this.table + " WHERE " + StringUtils.join(where, " AND "));
            query.addParameter("id", list);
            return query.executeAndFetch(Item.class);
        }
    }

    @Override
    public List<Option> query(String s, int i) {
        List<Option> options = new ArrayList<>();
        Sql2o sql2o = Spring.getBean(Sql2o.class);
        try (Connection connection = sql2o.open()) {
            List<String> where = new ArrayList<>();
            where.addAll(this.where);
            where.add(this.queryField + " LIKE :value");
            Query query = connection.createQuery("SELECT " + this.idField + " id, " + this.labelField + " value " + " FROM " + this.table + " WHERE " + StringUtils.join(where, " AND ") + " ORDER BY " + this.queryField + " ASC");
            query.addParameter("value", s + "%");
            List<Item> items = query.executeAndFetch(Item.class);
            for (Item item : items) {
                options.add(new Option(item.getId(), item.getValue()));
            }
        }
        return options;
    }

    @Override
    public boolean hasMore(String s, int i) {
        return false;
    }

    @Override
    public Gson getGson() {
        return Spring.getBean("gson", Gson.class);
    }

    @Override
    public Object getDisplayValue(Item object) {
        return object.getValue();
    }

    @Override
    public String getIdValue(Item object, int index) {
        return object.getId();
    }

    @Override
    public Item getObject(String id, IModel<? extends List<? extends Item>> choices) {
        Sql2o sql2o = Spring.getBean(Sql2o.class);
        try (Connection connection = sql2o.open()) {
            List<String> where = new ArrayList<>();
            where.addAll(this.where);
            where.add(this.idField + " = :id");
            Query query = connection.createQuery("SELECT " + this.idField + " id, " + this.labelField + " value " + " FROM " + this.table + " WHERE " + StringUtils.join(where, " AND "));
            query.addParameter("id", id);
            return query.executeAndFetchFirst(Item.class);
        }
    }

}
