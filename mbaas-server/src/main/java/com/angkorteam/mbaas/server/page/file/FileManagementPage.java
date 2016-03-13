package com.angkorteam.mbaas.server.page.file;

import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.*;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.FileTable;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.model.entity.tables.records.CollectionRecord;
import com.angkorteam.mbaas.model.entity.tables.records.FileRecord;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.server.provider.FileProvider;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.pages.RedirectPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/11/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/file/management")
public class FileManagementPage extends MasterPage implements ActionFilteredJooqColumn.Event {

    @Override
    public String getPageHeader() {
        return "File Management";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        DSLContext context = getDSLContext();
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");
        CollectionRecord collectionRecord = context.select(collectionTable.fields())
                .from(collectionTable)
                .where(collectionTable.NAME.eq(Tables.FILE.getName()))
                .fetchOneInto(collectionTable);
        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");

        List<AttributeRecord> attributeRecords = context.select(attributeTable.fields())
                .from(attributeTable)
                .where(attributeTable.COLLECTION_ID.eq(collectionRecord.getCollectionId()))
                .fetchInto(attributeTable);

        Map<String, AttributeRecord> virtualAttributeRecords = new HashMap<>();
        for (AttributeRecord attributeRecord : attributeRecords) {
            virtualAttributeRecords.put(attributeRecord.getAttributeId(), attributeRecord);
        }

        FileProvider provider = new FileProvider();

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("fileId", this), "fileId", this, provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("owner", this), "owner", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("name", this), "name", provider));
        columns.add(new TextFilteredJooqColumn(Integer.class, JooqUtils.lookup("length", this), "length", provider));
        columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup("mime", this), "mime", provider));
        for (AttributeRecord attributeRecord : attributeRecords) {
            if (attributeRecord.getSystem()) {
                continue;
            }
            if (TypeEnum.Boolean.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new TextFilteredJooqColumn(Boolean.class, JooqUtils.lookup(column, this), column, provider));
            } else if (TypeEnum.Byte.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new TextFilteredJooqColumn(Byte.class, JooqUtils.lookup(column, this), column, provider));
            } else if (TypeEnum.Short.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new TextFilteredJooqColumn(Short.class, JooqUtils.lookup(column, this), column, provider));
            } else if (TypeEnum.Integer.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new TextFilteredJooqColumn(Integer.class, JooqUtils.lookup(column, this), column, provider));
            } else if (TypeEnum.Long.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new TextFilteredJooqColumn(Long.class, JooqUtils.lookup(column, this), column, provider));
            } else if (TypeEnum.Float.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new TextFilteredJooqColumn(Float.class, JooqUtils.lookup(column, this), column, provider));
            } else if (TypeEnum.Double.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new TextFilteredJooqColumn(Double.class, JooqUtils.lookup(column, this), column, provider));
            } else if (TypeEnum.Character.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new TextFilteredJooqColumn(Character.class, JooqUtils.lookup(column, this), column, provider));
            } else if (TypeEnum.String.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup(column, this), column, provider));
            } else if (TypeEnum.Time.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new TimeFilteredJooqColumn(JooqUtils.lookup(column, this), column, provider));
            } else if (TypeEnum.Date.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new DateFilteredJooqColumn(JooqUtils.lookup(column, this), column, provider));
            } else if (TypeEnum.DateTime.getLiteral().equals(attributeRecord.getJavaType())) {
                String column = attributeRecord.getName();
                columns.add(new DateTimeFilteredJooqColumn(JooqUtils.lookup(column, this), column, provider));
            }
        }
        columns.add(new ActionFilteredJooqColumn(JooqUtils.lookup("action", this), JooqUtils.lookup("filter", this), JooqUtils.lookup("clear", this), this, "View", "Edit", "Delete"));

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 17);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);

        BookmarkablePageLink<Void> refreshLink = new BookmarkablePageLink<>("refreshLink", FileManagementPage.class, getPageParameters());
        add(refreshLink);
    }

    @Override
    public void onClickEventLink(String link, Map<String, Object> object) {
        DSLContext context = getDSLContext();
        FileTable fileTable = Tables.FILE.as("fileTable");
        String fileId = (String) object.get("fileId");
        if ("Delete".equals(link)) {
            FileRecord fileRecord = context.select(fileTable.fields()).from(fileTable).where(fileTable.FILE_ID.eq(fileId)).fetchOneInto(fileTable);
            XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
            String repo = configuration.getString(Constants.RESOURCE_REPO);
            File file = new File(repo + "/file" + fileRecord.getPath() + "/" + fileRecord.getName());
            FileUtils.deleteQuietly(file);
            context.delete(fileTable).where(fileTable.FILE_ID.eq(fileId)).execute();
            return;
        }
        if ("Edit".equals(link)) {
            PageParameters parameters = new PageParameters();
            parameters.add("fileId", fileId);
            setResponsePage(FileModifyPage.class, parameters);
            return;
        }
        if ("View".equals(link)) {
            FileRecord fileRecord = context.select(fileTable.fields()).from(fileTable).where(fileTable.FILE_ID.eq(fileId)).fetchOneInto(fileTable);
            StringBuffer address = new StringBuffer();
            address.append(getHttpAddress()).append("/api/resource/file").append(fileRecord.getPath()).append("/").append(fileRecord.getName());
            RedirectPage page = new RedirectPage(address);
            setResponsePage(page);
            return;
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
        if ("View".equals(link)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isVisibleEventLink(String link, Map<String, Object> object) {
        if ("Delete".equals(link)) {
            return true;
        }
        if ("Edit".equals(link)) {
            return true;
        }
        if ("View".equals(link)) {
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
        if ("View".equals(link)) {
            return "btn-xs btn-info";
        }
        return "";
    }
}
