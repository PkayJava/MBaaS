package com.angkorteam.mbaas.server.page.attribute;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.CollectionPojo;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.plain.enums.VisibilityEnum;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeDeleteRequest;
import com.angkorteam.mbaas.server.function.AttributeFunction;
import com.angkorteam.mbaas.server.page.document.DocumentManagementPage;
import com.angkorteam.mbaas.server.provider.AttributeProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/7/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/attribute/management")
public class AttributeManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    private String collectionId;

    private CollectionPojo collection;

    @Override
    public String getPageHeader() {
        return "Collection Attribute Management :: " + this.collection.getName();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.collectionId = getPageParameters().get("collectionId").toString();

        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        DSLContext context = getDSLContext();
        this.collection = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(CollectionPojo.class);

        AttributeProvider provider = new AttributeProvider(this.collectionId);
        provider.selectField(Boolean.class, "system");
        provider.selectField(String.class, "attributeId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();

        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("name", this), "name", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("attributeType", this), "attributeType", provider));
        columns.add(new TextFilteredJooqColumn(Integer.class, JooqUtils.lookup("extra", this), "extra", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("visibility", this), "visibility", provider));

        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Show", "Hide", "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", this.collectionId);
        BookmarkablePageLink<Void> newAttributeLink = new BookmarkablePageLink<>("newAttributeLink", AttributeCreatePage.class, parameters);
        add(newAttributeLink);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", AttributeManagementPage.class, getPageParameters());
        add(refreshLink);

        BookmarkablePageLink<Void> documentLink = new BookmarkablePageLink<>("documentLink", DocumentManagementPage.class, getPageParameters());
        add(documentLink);
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        DSLContext context = getDSLContext();
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        if ("Delete".equals(link)) {
            CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");

            String attributeId = (String) object.get("attributeId");

            AttributeRecord attributeRecord = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.ATTRIBUTE_ID.eq(attributeId)).fetchOneInto(attributeTable);
            CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(collectionTable);

            JdbcTemplate jdbcTemplate = getJdbcTemplate();
            CollectionAttributeDeleteRequest requestBody = new CollectionAttributeDeleteRequest();
            requestBody.setAttributeName(attributeRecord.getName());
            requestBody.setCollectionName(collectionRecord.getName());
            AttributeFunction.deleteAttribute(context, jdbcTemplate, requestBody);
        }
        if ("Hide".equals(link)) {
            String attributeId = (String) object.get("attributeId");
            context.update(attributeTable).set(attributeTable.VISIBILITY, VisibilityEnum.Hided.getLiteral()).where(attributeTable.ATTRIBUTE_ID.eq(attributeId)).execute();
        }
        if ("Show".equals(link)) {
            String attributeId = (String) object.get("attributeId");
            context.update(attributeTable).set(attributeTable.VISIBILITY, VisibilityEnum.Shown.getLiteral()).where(attributeTable.ATTRIBUTE_ID.eq(attributeId)).execute();
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            Boolean system = (Boolean) object.get("system");
            if (!system) {
                return true;
            }
        }
        if ("Hide".equals(link)) {
            String visibility = (String) object.get("visibility");
            if (VisibilityEnum.Shown.getLiteral().equals(visibility)) {
                return true;
            }
        }
        if ("Show".equals(link)) {
            String visibility = (String) object.get("visibility");
            if (VisibilityEnum.Hided.getLiteral().equals(visibility)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            Boolean system = (Boolean) object.get("system");
            if (!system) {
                return true;
            }
        }
        if ("Hide".equals(link)) {
            String visibility = (String) object.get("visibility");
            if (VisibilityEnum.Shown.getLiteral().equals(visibility)) {
                return true;
            }
        }
        if ("Show".equals(link)) {
            String visibility = (String) object.get("visibility");
            if (VisibilityEnum.Hided.getLiteral().equals(visibility)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            return "btn-xs btn-danger";
        }
        if ("Show".equals(link)) {
            return "btn-xs btn-info";
        }
        if ("Hide".equals(link)) {
            return "btn-xs btn-info";
        }
        return "";
    }
}
