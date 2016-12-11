package com.angkorteam.mbaas.server.page.collection;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.*;
import com.angkorteam.mbaas.plain.request.collection.CollectionDeleteRequest;
import com.angkorteam.mbaas.server.function.CollectionFunction;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.page.attribute.AttributeBrowsePage;
import com.angkorteam.mbaas.server.page.document.DocumentBrowsePage;
import com.angkorteam.mbaas.server.provider.CollectionProvider;
import com.google.common.collect.Maps;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/1/16.
 */
public class CollectionBrowsePage extends MBaaSPage {

    private DataTable<Map<String, Object>, String> dataTable;

    @Override
    public String getPageUUID() {
        return CollectionBrowsePage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);
        CollectionProvider provider = new CollectionProvider();
        provider.setSort("name", SortOrder.ASCENDING);
        provider.selectField("collectionId", String.class);

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        layout.add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("name"), "name", this::getModelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.Boolean, Model.of("system"), "system", this::getModelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.Boolean, Model.of("locked"), "locked", this::getModelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.Boolean, Model.of("mutable"), "mutable", this::getModelValue));
        columns.add(new ActionFilterColumn(Model.of("action"), this::actions, this::clickable, this::itemCss, this::itemClick));

        this.dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        this.dataTable.addTopToolbar(new FilterToolbar(this.dataTable, filterForm));
        filterForm.add(this.dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", CollectionBrowsePage.class);
        layout.add(refreshLink);

        BookmarkablePageLink<Void> createLink = new BookmarkablePageLink<>("createLink", CollectionCreatePage.class);
        layout.add(createLink);
    }

    private void itemClick(String link, Map<String, Object> object, AjaxRequestTarget target) {
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
            target.add(this.dataTable);
        }
        if ("Attribute".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("collectionId", collectionId);
            setResponsePage(AttributeBrowsePage.class, parameters);
        }
    }

    private Map<String, IModel<String>> actions() {
        Map<String, IModel<String>> actions = Maps.newHashMap();
        actions.put("Attribute", Model.of("Attribute"));
        actions.put("Delete", Model.of("Delete"));
        return actions;
    }

    private Boolean clickable(String link, Map<String, Object> object) {
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

    private ItemCss itemCss(String link, Map<String, Object> model) {
        if ("Delete".equals(link)) {
            return ItemCss.DANGER;
        }
        if ("Attribute".equals(link)) {
            return ItemCss.PRIMARY;
        }
        return ItemCss.NONE;
    }

    private Object getModelValue(String name, Map<String, Object> stringObjectMap) {
        return stringObjectMap.get(name);
    }
}
