package com.angkorteam.mbaas.server.page.attribute;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeDeleteRequest;
import com.angkorteam.mbaas.server.function.AttributeFunction;
import com.angkorteam.mbaas.server.provider.AttributeProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Page;
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
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("virtualAttribute", this), "virtualAttribute", provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("system", this), "system", provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("exposed", this), "exposed", provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("nullable", this), "nullable", provider));
        columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup("autoIncrement", this), "autoIncrement", provider));

        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", this.collectionId);
        BookmarkablePageLink<Void> newAttributeLink = new BookmarkablePageLink<Void>("newAttributeLink", AttributeCreatePage.class, parameters);
        add(newAttributeLink);
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
            CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");

            String attributeId = (String) object.get("attributeId");
            DSLContext context = getDSLContext();

            AttributeRecord attributeRecord = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.ATTRIBUTE_ID.eq(attributeId)).fetchOneInto(attributeTable);
            CollectionRecord collectionRecord = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(collectionTable);

            JdbcTemplate jdbcTemplate = getJdbcTemplate();
            CollectionAttributeDeleteRequest requestBody = new CollectionAttributeDeleteRequest();
            requestBody.setAttributeName(attributeRecord.getName());
            requestBody.setCollectionName(collectionRecord.getName());
            AttributeFunction.deleteAttribute(context, jdbcTemplate, requestBody);
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
        return false;
    }
}
