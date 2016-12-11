package com.angkorteam.mbaas.server.page.attribute;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.*;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.AttributePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.CollectionPojo;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeDeleteRequest;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.function.AttributeFunction;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.provider.AttributeProvider;
import com.google.common.collect.Maps;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/7/16.
 */
public class AttributeBrowsePage extends MBaaSPage {

    private String collectionId;

    private CollectionPojo collection;

    private DataTable<Map<String, Object>, String> dataTable;

    @Override
    public String getPageUUID() {
        return AttributeBrowsePage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);
        DSLContext context = Spring.getBean(DSLContext.class);
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");

        PageParameters pageParameters = getPageParameters();

        this.collectionId = pageParameters.get("collectionId").toString();
        this.collection = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(this.collectionId)).fetchOneInto(CollectionPojo.class);

        AttributeProvider provider = new AttributeProvider(this.collectionId);
        provider.selectField("attributeId", String.class);

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        layout.add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();

        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("name"), "name", this::getModelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.String, Model.of("type"), "type", this::getModelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.Boolean, Model.of("eav"), "eav", this::getModelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.Boolean, Model.of("system"), "system", this::getModelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.Boolean, Model.of("nullable"), "nullable", this::getModelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.Integer, Model.of("length"), "length", this::getModelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.Integer, Model.of("precision"), "precision", this::getModelValue));
        columns.add(new TextFilterColumn(provider, ItemClass.Integer, Model.of("order"), "order", this::getModelValue));
        columns.add(new ActionFilterColumn(Model.of("action"), this::actions, this::clickable, this::itemCss, this::itemClick));

        this.dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        this.dataTable.addTopToolbar(new FilterToolbar(this.dataTable, filterForm));
        filterForm.add(this.dataTable);

        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", this.collectionId);
        BookmarkablePageLink<Void> createLink = new BookmarkablePageLink<>("createLink", AttributeCreatePage.class, parameters);
        layout.add(createLink);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", AttributeBrowsePage.class, parameters);
        layout.add(refreshLink);
    }

    private void itemClick(String s, Map<String, Object> object, AjaxRequestTarget target) {
        String attributeId = (String) object.get("attributeId");
        if ("delete".equals(s)) {
            DSLContext context = Spring.getBean(DSLContext.class);
            AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
            AttributePojo attribute = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.ATTRIBUTE_ID.eq(attributeId)).fetchOneInto(AttributePojo.class);
            CollectionAttributeDeleteRequest requestBody = new CollectionAttributeDeleteRequest();
            requestBody.setAttributeName(attribute.getName());
            requestBody.setCollectionName(this.collection.getName());
            AttributeFunction.deleteAttribute(requestBody);
            target.add(this.dataTable);
        }
    }

    private Map<String, IModel<String>> actions() {
        Map<String, IModel<String>> actions = Maps.newHashMap();
        actions.put("delete", Model.of("Delete"));
        return actions;
    }

    private Boolean clickable(String s, Map<String, Object> model) {
        if ("delete".equals(s)) {
            Boolean system = (Boolean) model.get("system");
            if (!system) {
                return true;
            }
        }
        return false;
    }

    private ItemCss itemCss(String s, Map<String, Object> model) {
        return ItemCss.DANGER;
    }

    private Object getModelValue(String name, Map<String, Object> stringObjectMap) {
        return stringObjectMap.get(name);
    }
}
