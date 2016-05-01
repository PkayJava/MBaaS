package com.angkorteam.mbaas.server.page.collection;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.plain.request.collection.CollectionCreateRequest;
import com.angkorteam.mbaas.server.function.CollectionFunction;
import com.angkorteam.mbaas.server.validator.CollectionNameValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

/**
 * Created by socheat on 3/3/16.
 */
@Mount("/collection/create")
@AuthorizeInstantiation({"administrator", "backoffice"})
public class CollectionCreatePage extends MasterPage {

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private Form<Void> form;

    private Button saveButton;

    @Override
    public String getPageHeader() {
        return "Create New Collection";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.form = new Form<>("form");
        add(this.form);

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.add(new CollectionNameValidator());
        this.nameField.setRequired(true);
        this.form.add(this.nameField);

        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        CollectionCreateRequest requestBody = new CollectionCreateRequest();
        requestBody.setCollectionName(this.name);
        CollectionFunction.createCollection(getDSLContext(), getJdbcTemplate(), getSession().getApplicationId(), getSession().getUserId(), requestBody);
        setResponsePage(CollectionManagementPage.class);
    }
}
