package com.angkorteam.mbaas.server.page.collection;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.server.provider.CollectionProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Page;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.MapModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/1/16.
 */
@Mount("/collection/management")
public class CollectionManagementPage extends Page {

    @Override
    protected void onInitialize() {
        super.onInitialize();

        CollectionProvider provider = new CollectionProvider();
        provider.selectField(String.class, "collectionId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("name", this), "name", provider));
        columns.add(new TextFilteredJooqColumn(Integer.class, JooqUtils.lookup("count", this), "count", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("owner", this), "login", provider));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

    }

    public static void main(String[] arga) {
        Map<String, String> pp = new HashMap<>();
        pp.put("test", "111");
        MapModel<String, String> pps = new MapModel<>(pp);
        PropertyModel<String> stringPropertyModel = new PropertyModel<>(pps, "test");
        System.out.println(stringPropertyModel.getObject());
    }
}
