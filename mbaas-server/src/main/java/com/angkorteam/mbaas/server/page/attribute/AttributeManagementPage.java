package com.angkorteam.mbaas.server.page.attribute;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.ActionFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.DateTimeFilteredJooqColumn;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.plain.enums.VisibilityEnum;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeDeleteRequest;
import com.angkorteam.mbaas.server.Jdbc;
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
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/7/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/attribute/management")
public class AttributeManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    private String collectionId;
    private String collectionName;

    @Override
    public String getPageHeader() {
        return "Collection Attribute Management :: " + this.collectionName;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.collectionId = getPageParameters().get("collectionId").toString();

        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();

        Map<String, Object> collectionRecord = null;
        collectionRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.COLLECTION + " WHERE " + Jdbc.Collection.COLLECTION_ID + " = ?", this.collectionId);
        this.collectionName = (String) collectionRecord.get(Jdbc.Collection.NAME);

        AttributeProvider provider = new AttributeProvider(getSession().getApplicationCode(), this.collectionId);
        provider.selectField(Boolean.class, "system");
        provider.selectField(String.class, "attributeId");

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();

        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("name", this), "name", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("attributeType", this), "attributeType", provider));
        columns.add(new DateTimeFilteredJooqColumn(JooqUtils.lookup("dateCreated", this), "dateCreated", provider));
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
        String attributeId = (String) object.get("attributeId");
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        if ("Delete".equals(link)) {
            Map<String, Object> attributeRecord = null;
            attributeRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.ATTRIBUTE + " WHERE " + Jdbc.Attribute.ATTRIBUTE_ID + " = ?", attributeId);
            Map<String, Object> collectionRecord = null;
            collectionRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.COLLECTION + " WHERE " + Jdbc.Collection.COLLECTION_ID + " = ?", collectionId);
            CollectionAttributeDeleteRequest requestBody = new CollectionAttributeDeleteRequest();
            requestBody.setAttributeName((String) attributeRecord.get(Jdbc.Attribute.NAME));
            requestBody.setCollectionName((String) collectionRecord.get(Jdbc.Collection.NAME));
            AttributeFunction.deleteAttribute(jdbcTemplate, requestBody);
        }
        if ("Hide".equals(link)) {
            jdbcTemplate.update("UPDATE " + Jdbc.ATTRIBUTE + " SET " + Jdbc.Attribute.VISIBILITY + " = ? WHERE " + Jdbc.Attribute.ATTRIBUTE_ID + " = ?", VisibilityEnum.Hided.getLiteral(), attributeId);
        }
        if ("Show".equals(link)) {
            jdbcTemplate.update("UPDATE " + Jdbc.ATTRIBUTE + " SET " + Jdbc.Attribute.VISIBILITY + " = ? WHERE " + Jdbc.Attribute.ATTRIBUTE_ID + " = ?", VisibilityEnum.Shown.getLiteral(), attributeId);
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
        return isAccess(link, object);
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
