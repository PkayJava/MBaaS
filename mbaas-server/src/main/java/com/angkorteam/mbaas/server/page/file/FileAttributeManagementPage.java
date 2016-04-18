package com.angkorteam.mbaas.server.page.file;

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
import com.angkorteam.mbaas.server.function.FileAttributeFunction;
import com.angkorteam.mbaas.server.provider.AttributeProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.jooq.DSLContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/11/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/file/attribute/management")
public class FileAttributeManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    private String collectionId;

    @Override
    public String getPageHeader() {
        return "File Attribute Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = getDSLContext();
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");

        this.collectionId = context.select(collectionTable.COLLECTION_ID).from(collectionTable).where(collectionTable.NAME.eq(Tables.FILE.getName())).fetchOneInto(String.class);

        AttributeProvider provider = new AttributeProvider(this.collectionId);
        provider.selectField(Boolean.class, "system");
        provider.selectField(String.class, "attributeId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();

        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("name", this), "name", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("attributeType", this), "attributeType", provider));
        columns.add(new TextFilteredJooqColumn(Integer.class, JooqUtils.lookup("extra", this), "extra", provider));

        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", FileAttributeManagementPage.class, getPageParameters());
        add(refreshLink);
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
            FileAttributeFunction.deleteAttribute(context, jdbcTemplate, requestBody);
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

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            return "btn-xs btn-danger";
        }
        return "";
    }
}
