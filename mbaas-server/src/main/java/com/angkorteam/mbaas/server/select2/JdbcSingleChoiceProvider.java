package com.angkorteam.mbaas.server.select2;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.SingleChoiceProvider;
import com.angkorteam.mbaas.server.Spring;
import com.google.gson.Gson;
import org.apache.wicket.model.IModel;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by socheat on 12/5/16.
 */
public class JdbcSingleChoiceProvider extends SingleChoiceProvider<Item> {

    private final String table;

    private final String idField;

    private final String valueField;

    public JdbcSingleChoiceProvider(String table, String idField, String valueField) {
        this.table = table;
        this.idField = idField;
        this.valueField = valueField;
    }

    @Override
    public Item toChoice(String s) {
        Sql2o sql2o = Spring.getBean(Sql2o.class);
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT " + this.idField + " id, " + this.valueField + " value " + " FROM " + this.table + " WHERE " + idField + " = :id");
            query.addParameter("id", s);
            return query.executeAndFetchFirst(Item.class);
        }
    }

    @Override
    public List<Option> query(String s, int i) {
        List<Option> options = new ArrayList<>();
        Sql2o sql2o = Spring.getBean(Sql2o.class);

        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT " + this.idField + " id, " + this.valueField + " value " + " FROM " + this.table + " WHERE " + valueField + " LIKE :value ORDER BY " + valueField + " ASC");
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
            Query query = connection.createQuery("SELECT " + this.idField + " id, " + this.valueField + " value " + " FROM " + this.table + " WHERE " + idField + " = :id");
            query.addParameter("id", id);
            return query.executeAndFetchFirst(Item.class);
        }
    }
}
