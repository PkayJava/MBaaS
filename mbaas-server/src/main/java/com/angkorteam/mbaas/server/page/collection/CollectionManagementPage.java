package com.angkorteam.mbaas.server.page.collection;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.model.entity.tables.UserTable;
import com.angkorteam.mbaas.server.provider.*;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Page;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by socheat on 3/1/16.
 */
@Mount("/collection/management")
public class CollectionManagementPage extends Page {

    @Override
    protected void onInitialize() {
        super.onInitialize();

        CollectionProvider provider = new CollectionProvider();
        provider.selectField("collectionId", provider.getCollectionId());

        FilterForm<CollectionFilterModel> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<CollectionItemModel, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn<>(String.class, JooqUtils.lookup("name", this), CollectionFilterModel.class, "name", provider, provider.getName()));
        columns.add(new TextFilteredJooqColumn<>(Integer.class, JooqUtils.lookup("count", this), CollectionFilterModel.class, "count", provider, provider.getCount()));

        DataTable<CollectionItemModel, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

    }
}
