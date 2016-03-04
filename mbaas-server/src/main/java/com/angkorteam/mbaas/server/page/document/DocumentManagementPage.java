package com.angkorteam.mbaas.server.page.document;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.table.DataTable;
import com.angkorteam.framework.extension.wicket.table.DefaultDataTable;
import com.angkorteam.framework.extension.wicket.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.table.filter.TextFilteredJooqColumn;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.AttributeTable;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.CollectionPojo;
import com.angkorteam.mbaas.model.entity.tables.records.AttributeRecord;
import com.angkorteam.mbaas.server.provider.DocumentProvider;
import com.angkorteam.mbaas.server.renderer.CollectionChoiceRenderer;
import com.angkorteam.mbaas.server.wicket.JooqUtils;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.Page;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/3/16.
 */
@Mount("/document/management")
public class DocumentManagementPage extends Page {

    private CollectionPojo collection;
    private DropDownChoice<CollectionPojo> collectionField;
    private TextFeedbackPanel collectionFeedback;

    private Form<Void> form;

    private DocumentProvider provider;

    private Button browseButton;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = getDSLContext();
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");

        PageParameters parameters = getPageParameters();

        String collectionId = parameters.get("collectionId").toString();

        this.collection = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(CollectionPojo.class);

        AttributeTable attributeTable = Tables.ATTRIBUTE.as("attributeTable");
        List<AttributeRecord> attributeRecords = context.select(attributeTable.fields()).from(attributeTable).where(attributeTable.COLLECTION_ID.eq(collection.getCollectionId())).fetchInto(attributeTable);

        this.provider = new DocumentProvider(this.collection.getName());

        this.form = new Form<>("form");
        add(this.form);

        List<CollectionPojo> collections = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.SYSTEM.eq(false)).fetchInto(CollectionPojo.class);
        this.collectionField = new DropDownChoice<CollectionPojo>("collectionField", new PropertyModel<>(this, "collection"), collections, new CollectionChoiceRenderer());
        this.collectionField.setRequired(true);
        this.collectionFeedback = new TextFeedbackPanel("collectionFeedback", collectionField);

        this.form.add(collectionField);
        this.form.add(collectionFeedback);

        this.browseButton = new Button("browseButton");
        this.browseButton.setOnSubmit(this::browseButtonOnSubmit);
        this.form.add(browseButton);

        FilterForm<Map<String, String>> filterForm = new FilterForm<>("filter-form", provider);
        add(filterForm);

        List<IColumn<Map<String, Object>, String>> columns = new ArrayList<>();

        for (AttributeRecord attributeRecord : attributeRecords) {
            if (!attributeRecord.getVirtual()) {
                if (String.class.getName().equals(attributeRecord.getJavaType())) {
                    String column = attributeRecord.getName();
                    columns.add(new TextFilteredJooqColumn(String.class, JooqUtils.lookup(column, this), column, provider));
                }
            }
        }

        DataTable<Map<String, Object>, String> dataTable = new DefaultDataTable<>("table", columns, provider, 20);
        dataTable.addTopToolbar(new FilterToolbar(dataTable, filterForm));
        filterForm.add(dataTable);
    }

    private void browseButtonOnSubmit(Button button) {
        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", this.collection.getCollectionId());
        setResponsePage(DocumentManagementPage.class, parameters);
    }
}
