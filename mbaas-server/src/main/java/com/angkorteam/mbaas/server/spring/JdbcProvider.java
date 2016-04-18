package com.angkorteam.mbaas.server.spring;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Khauv Socheat on 4/17/2016.
 */
public class JdbcProvider extends SortableDataProvider<Map<String, Object>, String> implements IFilterStateLocator<Map<String, String>> {
    @Override
    public Map<String, String> getFilterState() {
        return null;
    }

    @Override
    public void setFilterState(Map<String, String> state) {

    }

    @Override
    public Iterator<? extends Map<String, Object>> iterator(long first, long count) {
        return null;
    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public IModel<Map<String, Object>> model(Map<String, Object> object) {
        return null;
    }
}
