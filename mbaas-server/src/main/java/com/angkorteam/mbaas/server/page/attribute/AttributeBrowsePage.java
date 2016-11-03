package com.angkorteam.mbaas.server.page.attribute;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
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
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/7/16.
 */
public class AttributeBrowsePage extends MBaaSPage implements ActionFilteredJooqColumn.Event {

    private String collectionId;

    private CollectionPojo collection;

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
        provider.selectField(String.class, "attributeId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        layout.add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();

        columns.add(new TextFilteredJooqColumn(String.class, Model.of("name"), "name", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, Model.of("type"), "type", this, provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, Model.of("eav"), "eav", this, provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, Model.of("system"), "system", this, provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, Model.of("nullable"), "nullable", this, provider));
        columns.add(new TextFilteredJooqColumn(Integer.class, Model.of("length"), "length", this, provider));
        columns.add(new TextFilteredJooqColumn(Integer.class, Model.of("precision"), "precision", this, provider));
        columns.add(new TextFilteredJooqColumn(Integer.class, Model.of("order"), "order", this, provider));
        columns.add(new ActionFilteredJooqColumn(Model.of("action"), Model.of("filter"), Model.of("clear"), this, "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", this.collectionId);
        BookmarkablePageLink<Void> createLink = new BookmarkablePageLink<>("createLink", AttributeCreatePage.class, parameters);
        layout.add(createLink);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", AttributeBrowsePage.class, parameters);
        layout.add(refreshLink);
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        String attributeId = (String) object.get("attributeId");
        if ("Delete".equals(link)) {
            DSLContext context = Spring.getBean(DSLContext.class);
            AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
            AttributePojo attribute = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.ATTRIBUTE_ID.eq(attributeId)).fetchOneInto(AttributePojo.class);
            CollectionAttributeDeleteRequest requestBody = new CollectionAttributeDeleteRequest();
            requestBody.setAttributeName(attribute.getName());
            requestBody.setCollectionName(this.collection.getName());
            AttributeFunction.deleteAttribute(requestBody);
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        return isAccess(link, object);
    }

    private boolean isAccess(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            Boolean system = (Boolean) object.get("system");
            if (!system) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        return isAccess(link, object);
    }

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            return "btn-xs btn-danger";
        }
        return "";
    }
}
