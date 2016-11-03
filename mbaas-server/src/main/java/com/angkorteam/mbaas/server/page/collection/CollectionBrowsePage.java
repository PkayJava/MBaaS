package com.angkorteam.mbaas.server.page.collection;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.plain.request.collection.CollectionDeleteRequest;
import com.angkorteam.mbaas.server.function.CollectionFunction;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.page.attribute.AttributeBrowsePage;
import com.angkorteam.mbaas.server.page.document.DocumentBrowsePage;
import com.angkorteam.mbaas.server.provider.CollectionProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/1/16.
 */
public class CollectionBrowsePage extends MBaaSPage implements ActionFilteredJooqColumn.Event {

    @Override
    public String getPageUUID() {
        return CollectionBrowsePage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);
        CollectionProvider provider = new CollectionProvider();
        provider.selectField(String.class, "collectionId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        layout.add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, Model.of("name"), "name", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, Model.of("system"), "system", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, Model.of("locked"), "locked", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, Model.of("mutable"), "mutable", this, provider));
        columns.add(new ActionFilteredJooqColumn(Model.of("action"), Model.of("filter"), Model.of("clear"), this, "Attribute", "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", CollectionBrowsePage.class);
        layout.add(refreshLink);

        BookmarkablePageLink<Void> createLink = new BookmarkablePageLink<>("createLink", CollectionCreatePage.class);
        layout.add(createLink);
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        String collectionId = (String) object.get("collectionId");
        if ("name".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("collectionId", collectionId);
            setResponsePage(DocumentBrowsePage.class, parameters);
        }
        if ("Delete".equals(link)) {
            CollectionDeleteRequest requestBody = new CollectionDeleteRequest();
            requestBody.setCollectionName((String) object.get("name"));
            CollectionFunction.deleteCollection(requestBody);
            setResponsePage(CollectionBrowsePage.class);
        }
        if ("Attribute".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("collectionId", collectionId);
            setResponsePage(AttributeBrowsePage.class, parameters);
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        Boolean system = (Boolean) object.get("system");
        Boolean mutable = (Boolean) object.get("mutable");
        if ("name".equals(link)) {
            return true;
        }
        if ("Delete".equals(link)) {
            if (system) {
                return false;
            }
            return true;
        }
        if ("Attribute".equals(link)) {
            if (mutable) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        return true;
    }

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            return "btn-xs btn-danger";
        }
        if ("Attribute".equals(link)) {
            return "btn-xs btn-info";
        }
        return "";
    }
}
