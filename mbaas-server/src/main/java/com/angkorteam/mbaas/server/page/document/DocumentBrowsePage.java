package com.angkorteam.mbaas.server.page.document;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilterColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ItemCss;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.AttributePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.CollectionPojo;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.function.DocumentFunction;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.page.collection.CollectionBrowsePage;
import com.angkorteam.mbaas.server.provider.DocumentProvider;
import com.angkorteam.mbaas.server.wicket.ProviderUtils;
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
 * Created by socheat on 3/3/16.
 */
public class DocumentBrowsePage extends MBaaSPage {

    private CollectionPojo collection;

    private DocumentProvider provider;

    @Override
    public String getPageUUID() {
        return DocumentBrowsePage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);

        DSLContext context = Spring.getBean(DSLContext.class);
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        String collectionId = getPageParameters().get("collectionId").toString();
        this.collection = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(CollectionPojo.class);
        List<AttributePojo> attributes = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collectionId)).fetchInto(AttributePojo.class);

        this.provider = new DocumentProvider(collectionId);

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", this.provider);
        layout.add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        for (AttributePojo attribute : attributes) {
            TypeEnum type = TypeEnum.valueOf(attribute.getType());
            ProviderUtils.addColumn(this.provider, columns, attribute, type);
        }

        columns.add(new ActionFilterColumn(Model.of("action"), this::actions, this::clickable, this::itemCss, this::itemClick));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", DocumentBrowsePage.class, getPageParameters());
        layout.add(refreshLink);

        BookmarkablePageLink<Void> createLink = new BookmarkablePageLink<>("createLink", DocumentCreatePage.class, getPageParameters());
        layout.add(createLink);

        BookmarkablePageLink<Void> collectionBrowseLink = new BookmarkablePageLink<>("collectionBrowseLink", CollectionBrowsePage.class);
        layout.add(collectionBrowseLink);
    }

    private Map<String, IModel<String>> actions() {
        Map<String, IModel<String>> actions = Maps.newHashMap();
        actions.put("Edit", Model.of("Edit"));
        actions.put("Delete", Model.of("Delete"));
        return actions;
    }

    private Boolean clickable(String link, Map<String, Object> object) {
        Boolean system = (Boolean) object.get("system");
        if ("Delete".equals(link)) {
            if (system) {
                return false;
            }
            return true;
        }
        if ("Edit".equals(link)) {
            if (system) {
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
        if ("Edit".equals(link)) {
            return ItemCss.PRIMARY;
        }
        return ItemCss.NONE;
    }

    private void itemClick(String link, Map<String, Object> object, AjaxRequestTarget ajaxRequestTarget) {
        if ("Delete".equals(link)) {
            String documentId = (String) object.get(this.collection.getName() + "_id");
            DocumentFunction.deleteDocument(this.collection.getCollectionId(), documentId);
        }
        if ("Edit".equals(link)) {
            String documentId = (String) object.get(this.collection.getName() + "_id");
            PageParameters parameters = new PageParameters();
            parameters.add("documentId", documentId);
            parameters.add("collectionId", this.collection.getCollectionId());
            setResponsePage(DocumentModifyPage.class, parameters);
        }
    }

    private Object getModelValue(String name, Map<String, Object> stringObjectMap) {
        return stringObjectMap.get(name);
    }

}
