package com.angkorteam.mbaas.server.page.attribute;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.server.provider.AttributeProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.Page;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/7/16.
 */
public class AttributeManagementPage extends Page implements ActionFilteredJooqColumn.Event {

    private String collectionId;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.collectionId = getPageParameters().get("collectionId").toString();

        AttributeProvider provider = new AttributeProvider(this.collectionId);

        provider.selectField(String.class, "attributeId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();

        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("name", this), "name", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("javaType", this), "javaType", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("sqlType", this), "sqlType", provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("virtual", this), "virtual", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("virtualAttributeId", this), "virtualAttributeId", provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("system", this), "system", provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("exposed", this), "exposed", provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("nullable", this), "nullable", provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("autoIncrement", this), "autoIncrement", provider));

        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Attribute", "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {

    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        return false;
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        return false;
    }
}
