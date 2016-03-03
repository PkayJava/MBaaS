package com.angkorteam.mbaas.server.page.document;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.CollectionPojo;
import com.angkorteam.mbaas.server.renderer.CollectionChoiceRenderer;
import com.angkorteam.mbaas.server.wicket.Page;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.List;

/**
 * Created by socheat on 3/3/16.
 */
public class DocumentManagementPage extends Page {

    private CollectionPojo collection;
    private DropDownChoice<CollectionPojo> collectionField;
    private TextFeedbackPanel collectionFeedback;

    private Form<Void> form;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = getDSLContext();
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");

        PageParameters parameters = getPageParameters();

        String collectionId = parameters.get("collectionId").toString();

        this.collection = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(collectionId)).fetchOneInto(CollectionPojo.class);

        this.form = new Form<>("form");
        add(this.form);

        List<CollectionPojo> collections = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.SYSTEM.eq(false)).fetchInto(CollectionPojo.class);
        this.collectionField = new DropDownChoice<CollectionPojo>("collectionField", new PropertyModel<>(this, "collection"), collections, new CollectionChoiceRenderer()) {
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }
        };
        this.collectionField.setRequired(true);
        this.collectionFeedback = new TextFeedbackPanel("collectionFeedback", collectionField);

        this.form.add(collectionField);
        this.form.add(collectionFeedback);

    }

}
