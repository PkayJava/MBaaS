package com.angkorteam.mbaas.server.page.collection;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.server.page.document.DocumentManagementPage;
import com.angkorteam.mbaas.server.provider.CollectionProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Page;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/1/16.
 */
@Mount("/collection/management")
public class CollectionManagementPage extends Page implements ActionFilteredJooqColumn.ClickEvent, ActionFilteredJooqColumn.ClickableEvent {

    @Override
    protected void onInitialize() {
        super.onInitialize();

        CollectionProvider provider = new CollectionProvider();
        provider.selectField(String.class, "collectionId");
        provider.selectField(Boolean.class, "system");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("name", this), "name", this, provider));
        columns.add(new TextFilteredJooqColumn(Integer.class, JooqUtils.lookup("document", this), "document", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("owner", this), "owner", provider));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        if ("name".equals(link)) {
            String collectionId = (String) object.get("collectionId");
            PageParameters parameters = new PageParameters();
            parameters.add("collectionId", collectionId);
            setResponsePage(DocumentManagementPage.class, parameters);
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        Boolean system = (Boolean) object.get("system");
        if ("name".equals(link)) {
            if (!system) {
                return true;
            }
        }
        return false;
    }

}
