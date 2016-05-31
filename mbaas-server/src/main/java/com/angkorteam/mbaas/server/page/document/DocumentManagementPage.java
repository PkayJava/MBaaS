package com.angkorteam.mbaas.server.page.document;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.plain.enums.AttributeTypeEnum;
import com.angkorteam.mbaas.plain.enums.VisibilityEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.function.DocumentFunction;
import com.angkorteam.mbaas.server.page.attribute.AttributeManagementPage;
import com.angkorteam.mbaas.server.provider.DocumentProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.ProviderUtils;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/3/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/document/management")
public class DocumentManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    private Map<String, Object> collection;
    private DropDownChoice<Map<String, Object>> collectionField;
    private TextFeedbackPanel collectionFeedback;

    private DocumentProvider provider;

    @Override
    public String getPageHeader() {
        return "Document Management :: " + collection.get(Jdbc.Collection.NAME);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        String collectionId = getPageParameters().get("collectionId").toString();

        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();

        this.collection = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.COLLECTION + " WHERE " + Jdbc.Collection.COLLECTION_ID + " = ?", collectionId);
        String collectionName = (String) this.collection.get(Jdbc.Collection.NAME);

        List<Map<String, Object>> attributeRecords = jdbcTemplate.queryForList("SELECT  * FROM " + Jdbc.ATTRIBUTE + " WHERE " + Jdbc.Attribute.COLLECTION_ID + " = ? AND " + Jdbc.Attribute.VISIBILITY + " = ?", collectionId, VisibilityEnum.Shown.getLiteral());

        this.provider = new DocumentProvider(getSession().getApplicationCode(), collectionId, collectionName);

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup(collectionName + "_id", this), collectionName + "_id", provider));
        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        String jdbcColumnOwnerApplicationUserId = configuration.getString(Constants.JDBC_COLUMN_OWNER_USER_ID);
        for (Map<String, Object> attributeRecord : attributeRecords) {
            if (attributeRecord.get(Jdbc.Attribute.NAME).equals(jdbcColumnOwnerApplicationUserId) || attributeRecord.get(Jdbc.Attribute.NAME).equals(collectionName + "_id")) {
                continue;
            }
            AttributeTypeEnum attributeType = AttributeTypeEnum.valueOf((String) attributeRecord.get(Jdbc.Attribute.ATTRIBUTE_TYPE));
            ProviderUtils.addColumn(provider, columns, attributeRecord, attributeType, this);
        }

        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("owner", this), "owner", provider));
        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "Edit", "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", collectionId);
        BookmarkablePageLink<Void> newDocumentLink = new BookmarkablePageLink<>("newDocumentLink", DocumentCreatePage.class, parameters);
        add(newDocumentLink);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", DocumentManagementPage.class, getPageParameters());
        add(refreshLink);

        BookmarkablePageLink<Void> attributeLink = new BookmarkablePageLink<>("attributeLink", AttributeManagementPage.class, getPageParameters());
        add(attributeLink);
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        if ("Delete".equals(link)) {
            String documentId = (String) object.get(this.collection.get(Jdbc.Collection.NAME) + "_id");
            DocumentFunction.deleteDocument(jdbcTemplate, (String) this.collection.get(Jdbc.Collection.NAME), documentId);
        }
        if ("Edit".equals(link)) {
            String collectionId = (String) this.collection.get(Jdbc.Collection.COLLECTION_ID);
            String documentId = (String) object.get(this.collection.get(Jdbc.Collection.NAME) + "_id");
            PageParameters parameters = new PageParameters();
            parameters.add("collectionId", collectionId);
            parameters.add("documentId", documentId);
            setResponsePage(DocumentModifyPage.class, parameters);
        }
    }

    @Override
    public boolean isClickableEventLink(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            return true;
        }
        if ("Delete".equals(link)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        if ("Edit".equals(link)) {
            return true;
        }
        if ("Delete".equals(link)) {
            return true;
        }
        return false;
    }

    @Override
    public String onCSSLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            return "btn-xs btn-danger";
        }
        if ("Edit".equals(link)) {
            return "btn-xs btn-info";
        }
        return "";
    }
}
