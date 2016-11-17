package com.angkorteam.mbaas.server.page.attribute;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.CollectionTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.CollectionPojo;
import com.angkorteam.mbaas.plain.enums.IndexEnum;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.plain.request.collection.CollectionAttributeCreateRequest;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.function.AttributeFunction;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.validator.AttributeNameValidator;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.validation.validator.RangeValidator;
import org.elasticsearch.common.Strings;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by socheat on 3/8/16.
 */
public class AttributeCreatePage extends MBaaSPage {

    private String collectionId;
    private CollectionPojo collection;

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private List<String> types;
    private String type;
    private DropDownChoice<String> typeField;
    private TextFeedbackPanel typeFeedback;

    private List<String> indexes;
    private String index;
    private DropDownChoice<String> indexField;
    private TextFeedbackPanel indexFeedback;

    private boolean eav;
    private CheckBox eavField;

    private Integer length;
    private TextField<Integer> lengthField;
    private TextFeedbackPanel lengthFeedback;

    private Integer order;
    private TextField<Integer> orderField;
    private TextFeedbackPanel orderFeedback;

    private Integer precision;
    private TextField<Integer> precisionField;
    private TextFeedbackPanel precisionFeedback;

    private boolean nullable;
    private CheckBox nullableField;

    private BookmarkablePageLink<Void> closeButton;
    private Form<Void> form;
    private Button saveButton;

    private RangeValidator<Integer> rangeValidator2_255;
    private RangeValidator<Integer> rangeValidator1_11;
    private RangeValidator<Integer> rangeValidator1_15;
    private RangeValidator<Integer> rangeValidator0_4;

    @Override
    public String getPageUUID() {
        return AttributeCreatePage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);
        this.rangeValidator2_255 = RangeValidator.range(2, 255);
        this.rangeValidator1_11 = RangeValidator.range(1, 11);
        this.rangeValidator1_15 = RangeValidator.range(1, 15);
        this.rangeValidator0_4 = RangeValidator.range(0, 4);
        DSLContext context = Spring.getBean(DSLContext.class);
        CollectionTable collectionTable = Tables.COLLECTION.as("collectionTable");

        PageParameters pageParameters = getPageParameters();

        this.collectionId = pageParameters.get("collectionId").toString();
        this.collection = context.select(collectionTable.fields()).from(collectionTable).where(collectionTable.COLLECTION_ID.eq(this.collectionId)).fetchOneInto(CollectionPojo.class);

        this.form = new Form<>("form");
        layout.add(this.form);

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.nameField.add(new AttributeNameValidator(this.collectionId));
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.types = new ArrayList<>(Arrays.asList(TypeEnum.Boolean.getLiteral(), TypeEnum.Long.getLiteral(), TypeEnum.Double.getLiteral(), TypeEnum.Character.getLiteral(), TypeEnum.String.getLiteral(), TypeEnum.Text.getLiteral(), TypeEnum.Time.getLiteral(), TypeEnum.Date.getLiteral(), TypeEnum.DateTime.getLiteral()));
        this.type = TypeEnum.String.getLiteral();
        this.typeField = new DropDownChoice<String>("typeField", new PropertyModel<>(this, "type"), new PropertyModel<>(this, "types")) {
            @Override
            protected boolean wantOnSelectionChangedNotifications() {
                return true;
            }

            @Override
            protected void onSelectionChanged(String newSelection) {
                typeSelectionChanged(newSelection);
            }
        };
        this.typeField.setRequired(true);
        this.form.add(this.typeField);
        this.typeFeedback = new TextFeedbackPanel("typeFeedback", this.typeField);
        this.form.add(typeFeedback);

        this.indexes = new ArrayList<>(Arrays.asList(IndexEnum.INDEX.getLiteral(), IndexEnum.UNIQUE.getLiteral(), IndexEnum.FULLTEXT.getLiteral()));
        this.index = IndexEnum.INDEX.getLiteral();
        this.indexField = new DropDownChoice<>("indexField", new PropertyModel<>(this, "index"), new PropertyModel<>(this, "indexes"));
        this.indexField.setNullValid(true);
        this.form.add(this.indexField);
        this.indexFeedback = new TextFeedbackPanel("indexFeedback", this.indexField);
        this.form.add(indexFeedback);

        this.nullable = true;
        this.nullableField = new CheckBox("nullableField", new PropertyModel<>(this, "nullable"));
        this.nullableField.setRequired(true);
        this.form.add(this.nullableField);

        this.eav = false;
        this.eavField = new CheckBox("eavField", new PropertyModel<>(this, "eav"));
        this.eavField.setRequired(true);
        this.form.add(this.eavField);

        this.length = TypeEnum.String.getLength();
        this.lengthField = new TextField<>("lengthField", new PropertyModel<>(this, "length"));
        this.form.add(this.lengthField);
        this.lengthFeedback = new TextFeedbackPanel("lengthFeedback", this.lengthField);
        this.form.add(this.lengthFeedback);

        this.orderField = new TextField<>("orderField", new PropertyModel<>(this, "order"));
        this.form.add(this.orderField);
        this.orderFeedback = new TextFeedbackPanel("orderFeedback", this.orderField);
        this.form.add(this.orderFeedback);

        this.precision = TypeEnum.String.getPrecision();
        this.precisionField = new TextField<>("precisionField", new PropertyModel<>(this, "precision"));
        this.form.add(this.precisionField);
        this.precisionFeedback = new TextFeedbackPanel("precisionFeedback", this.precisionField);
        this.form.add(this.precisionFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);

        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", this.collectionId);
        closeButton = new BookmarkablePageLink<>("closeButton", AttributeBrowsePage.class, parameters);
        this.form.add(closeButton);

        typeSelectionChanged(TypeEnum.String.getLiteral());
    }

    private void typeSelectionChanged(String newValue) {
        this.lengthField.setRequired(false);
        this.lengthField.remove(this.rangeValidator0_4, this.rangeValidator1_11, this.rangeValidator1_15, this.rangeValidator2_255);
        this.precisionField.setRequired(false);
        this.precisionField.remove(this.rangeValidator0_4, this.rangeValidator1_11, this.rangeValidator1_15, this.rangeValidator2_255);
        if (!Strings.isNullOrEmpty(newValue)) {
            if (TypeEnum.String.equals(newValue)) {
                this.lengthField.setRequired(true);
                this.lengthField.setType(Integer.class);
                this.lengthField.remove(this.rangeValidator0_4, this.rangeValidator1_11, this.rangeValidator1_15, this.rangeValidator2_255);
                this.lengthField.add(this.rangeValidator2_255);
            } else if (TypeEnum.Long.equals(newValue)) {
                this.lengthField.setRequired(true);
                this.lengthField.setType(Integer.class);
                this.lengthField.remove(this.rangeValidator0_4, this.rangeValidator1_11, this.rangeValidator1_15, this.rangeValidator2_255);
                this.lengthField.add(this.rangeValidator1_11);
            } else if (TypeEnum.Double.equals(newValue)) {
                this.lengthField.setRequired(true);
                this.lengthField.setType(Integer.class);
                this.lengthField.remove(this.rangeValidator0_4, this.rangeValidator1_11, this.rangeValidator1_15, this.rangeValidator2_255);
                this.lengthField.add(this.rangeValidator1_15);
                this.precisionField.setRequired(true);
                this.precisionField.setType(Integer.class);
                this.precisionField.add(RangeValidator.range(0, 4));
                this.precisionField.remove(this.rangeValidator0_4, this.rangeValidator1_11, this.rangeValidator1_15, this.rangeValidator2_255);
                this.precisionField.add(this.rangeValidator0_4);
            } else if (TypeEnum.Boolean.equals(newValue)) {
            } else if (TypeEnum.Text.equals(newValue)) {
            } else if (TypeEnum.Time.equals(newValue)) {
            } else if (TypeEnum.Date.equals(newValue)) {
            } else if (TypeEnum.Character.equals(newValue)) {
            } else if (TypeEnum.DateTime.equals(newValue)) {
            }
        }
    }

    private void saveButtonOnSubmit(Button button) {
        CollectionAttributeCreateRequest requestBody = new CollectionAttributeCreateRequest();
        requestBody.setAttributeName(this.name);
        requestBody.setNullable(this.nullable);
        requestBody.setEav(this.eav);
        requestBody.setType(this.type);
        requestBody.setCollectionName(this.collection.getName());
        if (this.type.equals(TypeEnum.Boolean.getLiteral()) || this.type.equals(TypeEnum.Character.getLiteral())) {
            requestBody.setLength(1);
            requestBody.setPrecision(-1);
        } else if (this.type.equals(TypeEnum.Long.getLiteral())) {
            requestBody.setLength(this.length);
            requestBody.setPrecision(-1);
        } else if (this.type.equals(TypeEnum.Double.getLiteral())) {
            requestBody.setLength(this.length);
            requestBody.setPrecision(this.precision);
        } else if (this.type.equals(TypeEnum.String.getLiteral())) {
            requestBody.setLength(this.length);
            requestBody.setPrecision(-1);
        } else if (this.type.equals(TypeEnum.Text.getLiteral())
                || this.type.equals(TypeEnum.DateTime.getLiteral())
                || this.type.equals(TypeEnum.Date.getLiteral())
                || this.type.equals(TypeEnum.Time.getLiteral())) {
            requestBody.setLength(-1);
            requestBody.setPrecision(-1);
        } else {
            requestBody.setLength(this.length);
            requestBody.setPrecision(this.precision);
        }

        if (IndexEnum.INDEX.getLiteral().equals(this.index)) {
            requestBody.setIndex("KEY");
        } else if (IndexEnum.UNIQUE.getLiteral().equals(this.index)) {
            requestBody.setIndex("UNIQUE KEY");
        } else if (IndexEnum.FULLTEXT.getLiteral().equals(this.index)) {
            requestBody.setIndex("FULLTEXT KEY");
        }


        AttributeFunction.createAttribute(requestBody);

        PageParameters parameters = new PageParameters();
        parameters.add("collectionId", this.collectionId);
        setResponsePage(AttributeBrowsePage.class, parameters);
    }
}
