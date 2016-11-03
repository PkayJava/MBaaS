package com.angkorteam.mbaas.server.page.collection;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.plain.enums.IndexEnum;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.plain.request.collection.CollectionCreateRequest;
import com.angkorteam.mbaas.server.function.CollectionFunction;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.validator.CollectionNameValidator;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;

/**
 * Created by socheat on 3/3/16.
 */
public class CollectionCreatePage extends MBaaSPage {

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private Form<Void> form;
    private Button saveButton;
    private BookmarkablePageLink<Void> closeButton;

    @Override
    public String getPageUUID() {
        return CollectionCreatePage.class.getName();
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

        this.closeButton = new BookmarkablePageLink<>("closeButton", CollectionBrowsePage.class);
        this.form.add(this.closeButton);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        CollectionCreateRequest requestBody = new CollectionCreateRequest();
        CollectionCreateRequest.Attribute attribute = new CollectionCreateRequest.Attribute();
        attribute.setIndex(IndexEnum.INDEX.getLiteral());
        attribute.setType(TypeEnum.Boolean.getLiteral());
        attribute.setLength(1);
        attribute.setName("system");
        attribute.setNullable(false);
        attribute.setPrecision(0);
        requestBody.getAttributes().add(attribute);
        requestBody.setCollectionName(this.name);
        CollectionFunction.createCollection(requestBody);
        setResponsePage(CollectionBrowsePage.class);
    }
}
