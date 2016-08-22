package com.angkorteam.mbaas.server.page.rest;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.JavascriptTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2MultipleChoice;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2SingleChoice;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.server.renderer.HttpHeaderRenderer;
import com.angkorteam.mbaas.server.renderer.HttpQueryRenderer;
import com.angkorteam.mbaas.server.renderer.JsonChoiceRenderer;
import com.angkorteam.mbaas.server.select2.EnumChoiceProvider;
import com.angkorteam.mbaas.server.select2.HttpHeaderChoiceProvider;
import com.angkorteam.mbaas.server.select2.HttpQueryChoiceProvider;
import com.angkorteam.mbaas.server.select2.RestJsonChoiceProvider;
import com.angkorteam.mbaas.server.validator.RestHttpHeaderValidator;
import com.angkorteam.mbaas.server.validator.RestHttpQueryValidator;
import com.angkorteam.mbaas.server.validator.RestNameValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import com.angkorteam.mbaas.server.wicket.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 8/3/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/rest/create")
public class RestCreatePage extends MasterPage {

    private String requestPath;
    private TextField<String> requestPathField;
    private TextFeedbackPanel requestPathFeedback;

    private List<String> methods;
    private String method;
    private DropDownChoice<String> methodField;
    private TextFeedbackPanel methodFeedback;

    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private List<String> requestContentTypes;
    private String requestContentType;
    private DropDownChoice<String> requestContentTypeField;
    private TextFeedbackPanel requestContentTypeFeedback;

    private List<String> requestBodyTypes;
    private String requestBodyType;
    private DropDownChoice<String> requestBodyTypeField;
    private TextFeedbackPanel requestBodyTypeFeedback;

    private List<String> requestBodySubTypes;
    private String requestBodySubType;
    private DropDownChoice<String> requestBodySubTypeField;
    private TextFeedbackPanel requestBodySubTypeFeedback;

    private RestJsonChoiceProvider requestBodyMapJsonProvider;
    private Map<String, Object> requestBodyMapJson;
    private Select2SingleChoice<Map<String, Object>> requestBodyMapJsonField;
    private TextFeedbackPanel requestBodyMapJsonFeedback;

    private Map<String, Object> requestBodyEnum;
    private Select2SingleChoice<Map<String, Object>> requestBodyEnumField;
    private TextFeedbackPanel requestBodyEnumFeedback;

    private List<Map<String, Object>> requestHeaderRequired;
    private Select2MultipleChoice<Map<String, Object>> requestHeaderRequiredField;
    private TextFeedbackPanel requestHeaderRequiredFeedback;

    private List<Map<String, Object>> requestHeaderOptional;
    private Select2MultipleChoice<Map<String, Object>> requestHeaderOptionalField;
    private TextFeedbackPanel requestHeaderOptionalFeedback;

    private List<Map<String, Object>> requestQueryRequired;
    private Select2MultipleChoice<Map<String, Object>> requestQueryRequiredField;
    private TextFeedbackPanel requestQueryRequiredFeedback;

    private List<Map<String, Object>> requestQueryOptional;
    private Select2MultipleChoice<Map<String, Object>> requestQueryOptionalField;
    private TextFeedbackPanel requestQueryOptionalFeedback;

    private List<String> responseContentTypes;
    private String responseContentType;
    private DropDownChoice<String> responseContentTypeField;
    private TextFeedbackPanel responseContentTypeFeedback;

    private List<String> responseBodyTypes;
    private String responseBodyType;
    private DropDownChoice<String> responseBodyTypeField;
    private TextFeedbackPanel responseBodyTypeFeedback;

    private List<String> responseBodySubTypes;
    private String responseBodySubType;
    private DropDownChoice<String> responseBodySubTypeField;
    private TextFeedbackPanel responseBodySubTypeFeedback;

    private Map<String, Object> responseBodyMapJson;
    private Select2SingleChoice<Map<String, Object>> responseBodyMapJsonField;
    private TextFeedbackPanel responseBodyMapJsonFeedback;

    private Map<String, Object> responseBodyEnum;
    private Select2SingleChoice<Map<String, Object>> responseBodyEnumField;
    private TextFeedbackPanel responseBodyEnumFeedback;

    private List<Map<String, Object>> responseHeaderRequired;
    private Select2MultipleChoice<Map<String, Object>> responseHeaderRequiredField;
    private TextFeedbackPanel responseHeaderRequiredFeedback;

    private List<Map<String, Object>> responseHeaderOptional;
    private Select2MultipleChoice<Map<String, Object>> responseHeaderOptionalField;
    private TextFeedbackPanel responseHeaderOptionalFeedback;

    private String script;
    private JavascriptTextArea scriptField;
    private TextFeedbackPanel scriptFeedback;

    private Form<Void> form;
    private Button saveButton;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.form = new Form<>("form");
        this.form.setOnError(this::formOnError);
        this.add(this.form);

        this.methods = Arrays.asList(HttpMethod.GET.name(), HttpMethod.DELETE.name(), HttpMethod.POST.name(), HttpMethod.PUT.name());
        this.methodField = new DropDownChoice<>("methodField", new PropertyModel<>(this, "method"), new PropertyModel<>(this, "methods"));
        this.methodField.setOutputMarkupId(true);
        this.methodField.setRequired(true);
        this.methodField.add(new OnChangeAjaxBehavior(this::methodFieldAjaxUpdate));
        this.form.add(this.methodField);
        this.methodFeedback = new TextFeedbackPanel("methodFeedback", this.methodField);
        this.form.add(this.methodFeedback);

        this.requestPathField = new TextField<>("requestPathField", new PropertyModel<>(this, "requestPath"));
        this.requestPathField.setOutputMarkupId(true);
        this.requestPathField.setRequired(true);
        this.form.add(this.requestPathField);
        this.requestPathFeedback = new TextFeedbackPanel("requestPathFeedback", this.requestPathField);
        this.form.add(this.requestPathFeedback);

        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setOutputMarkupId(true);
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.requestContentTypes = new ArrayList<>();
        this.requestContentTypeField = new DropDownChoice<>("requestContentTypeField", new PropertyModel<>(this, "requestContentType"), new PropertyModel<>(this, "requestContentTypes"));
        this.requestContentTypeField.setOutputMarkupId(true);
        this.requestContentTypeField.add(new OnChangeAjaxBehavior(this::requestContentTypeFieldAjaxUpdate));
        this.form.add(this.requestContentTypeField);
        this.requestContentTypeFeedback = new TextFeedbackPanel("requestContentTypeFeedback", this.requestContentTypeField);
        this.form.add(this.requestContentTypeFeedback);

        this.requestBodyTypes = new ArrayList<>();
        this.requestBodyTypeField = new DropDownChoice<>("requestBodyTypeField", new PropertyModel<>(this, "requestBodyType"), new PropertyModel<>(this, "requestBodyTypes"));
        this.requestBodyTypeField.setOutputMarkupId(true);
        this.requestBodyTypeField.add(new OnChangeAjaxBehavior(this::requestBodyTypeFieldAjaxUpdate));
        this.form.add(this.requestBodyTypeField);
        this.requestBodyTypeFeedback = new TextFeedbackPanel("requestBodyTypeFeedback", this.requestBodyTypeField);
        this.form.add(this.requestBodyTypeFeedback);

        this.requestBodySubTypes = new ArrayList<>();
        this.requestBodySubTypeField = new DropDownChoice<>("requestBodySubTypeField", new PropertyModel<>(this, "requestBodySubType"), new PropertyModel<>(this, "requestBodySubTypes"));
        this.requestBodySubTypeField.setOutputMarkupId(true);
        this.requestBodySubTypeField.add(new OnChangeAjaxBehavior(this::requestBodySubTypeFieldAjaxUpdate));
        this.form.add(this.requestBodySubTypeField);
        this.requestBodySubTypeFeedback = new TextFeedbackPanel("requestBodySubTypeFeedback", this.requestBodySubTypeField);
        this.form.add(this.requestBodySubTypeFeedback);

        this.requestBodyMapJsonProvider = new RestJsonChoiceProvider(getSession().getApplicationCode());
        this.requestBodyMapJsonField = new Select2SingleChoice<>("requestBodyMapJsonField", new PropertyModel<>(this, "requestBodyMapJson"), this.requestBodyMapJsonProvider, new JsonChoiceRenderer());
        this.requestBodyMapJsonField.setOutputMarkupId(true);
        this.form.add(this.requestBodyMapJsonField);
        this.requestBodyMapJsonFeedback = new TextFeedbackPanel("requestBodyMapJsonFeedback", this.requestBodyMapJsonField);
        this.form.add(this.requestBodyMapJsonFeedback);

        this.requestBodyEnumField = new Select2SingleChoice<>("requestBodyEnumField", new PropertyModel<>(this, "requestBodyEnum"), new EnumChoiceProvider(getSession().getApplicationCode()), new JsonChoiceRenderer());
        this.requestBodyEnumField.setOutputMarkupId(true);
        this.form.add(this.requestBodyEnumField);
        this.requestBodyEnumFeedback = new TextFeedbackPanel("requestBodyEnumFeedback", this.requestBodyEnumField);
        this.form.add(this.requestBodyEnumFeedback);

        this.requestHeaderRequiredField = new Select2MultipleChoice<>("requestHeaderRequiredField", new PropertyModel<>(this, "requestHeaderRequired"), new HttpHeaderChoiceProvider(getSession().getApplicationCode()), new HttpHeaderRenderer());
        this.requestHeaderRequiredField.setOutputMarkupId(true);
        this.form.add(this.requestHeaderRequiredField);
        this.requestHeaderRequiredFeedback = new TextFeedbackPanel("requestHeaderRequiredFeedback", this.requestHeaderRequiredField);
        this.form.add(this.requestHeaderRequiredFeedback);

        this.requestHeaderOptionalField = new Select2MultipleChoice<>("requestHeaderOptionalField", new PropertyModel<>(this, "requestHeaderOptional"), new HttpHeaderChoiceProvider(getSession().getApplicationCode()), new HttpHeaderRenderer());
        this.requestHeaderOptionalField.setOutputMarkupId(true);
        this.form.add(this.requestHeaderOptionalField);
        this.requestHeaderOptionalFeedback = new TextFeedbackPanel("requestHeaderOptionalFeedback", this.requestHeaderOptionalField);
        this.form.add(this.requestHeaderOptionalFeedback);

        this.requestQueryRequiredField = new Select2MultipleChoice<>("requestQueryRequiredField", new PropertyModel<>(this, "requestQueryRequired"), new HttpQueryChoiceProvider(getSession().getApplicationCode()), new HttpQueryRenderer());
        this.requestQueryRequiredField.setOutputMarkupId(true);
        this.form.add(this.requestQueryRequiredField);
        this.requestQueryRequiredFeedback = new TextFeedbackPanel("requestQueryRequiredFeedback", this.requestQueryRequiredField);
        this.form.add(this.requestQueryRequiredFeedback);

        this.requestQueryOptionalField = new Select2MultipleChoice<>("requestQueryOptionalField", new PropertyModel<>(this, "requestQueryOptional"), new HttpQueryChoiceProvider(getSession().getApplicationCode()), new HttpQueryRenderer());
        this.requestQueryOptionalField.setOutputMarkupId(true);
        this.form.add(this.requestQueryOptionalField);
        this.requestQueryOptionalFeedback = new TextFeedbackPanel("requestQueryOptionalFeedback", this.requestQueryOptionalField);
        this.form.add(this.requestQueryOptionalFeedback);

        this.responseContentTypes = new ArrayList<>();
        this.responseContentTypes.add(MediaType.APPLICATION_JSON_VALUE);
        this.responseContentTypes.add(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        this.responseContentTypeField = new DropDownChoice<>("responseContentTypeField", new PropertyModel<>(this, "responseContentType"), new PropertyModel<>(this, "responseContentTypes"));
        this.responseContentTypeField.setOutputMarkupId(true);
        this.responseContentTypeField.add(new OnChangeAjaxBehavior(this::responseContentTypeFieldAjaxUpdate));
        this.form.add(this.responseContentTypeField);
        this.responseContentTypeFeedback = new TextFeedbackPanel("responseContentTypeFeedback", this.responseContentTypeField);
        this.form.add(this.responseContentTypeFeedback);

        this.responseBodyTypes = new ArrayList<>();
        this.responseBodyTypeField = new DropDownChoice<>("responseBodyTypeField", new PropertyModel<>(this, "responseBodyType"), new PropertyModel<>(this, "responseBodyTypes"));
        this.responseBodyTypeField.setOutputMarkupId(true);
        this.responseBodyTypeField.add(new OnChangeAjaxBehavior(this::responseBodyTypeFieldAjaxUpdate));
        this.form.add(this.responseBodyTypeField);
        this.responseBodyTypeFeedback = new TextFeedbackPanel("responseBodyTypeFeedback", this.responseBodyTypeField);
        this.form.add(this.responseBodyTypeFeedback);

        this.responseBodySubTypes = new ArrayList<>();
        this.responseBodySubTypeField = new DropDownChoice<>("responseBodySubTypeField", new PropertyModel<>(this, "responseBodySubType"), new PropertyModel<>(this, "responseBodySubTypes"));
        this.responseBodySubTypeField.setOutputMarkupId(true);
        this.responseBodySubTypeField.add(new OnChangeAjaxBehavior(this::responseBodySubTypeFieldAjaxUpdate));
        this.form.add(this.responseBodySubTypeField);
        this.responseBodySubTypeFeedback = new TextFeedbackPanel("responseBodySubTypeFeedback", this.responseBodySubTypeField);
        this.form.add(this.responseBodySubTypeFeedback);

        this.responseBodyMapJsonField = new Select2SingleChoice<>("responseBodyMapJsonField", new PropertyModel<>(this, "responseBodyMapJson"), new RestJsonChoiceProvider(getSession().getApplicationCode(), MediaType.APPLICATION_JSON_VALUE), new JsonChoiceRenderer());
        this.responseBodyMapJsonField.setOutputMarkupId(true);
        this.form.add(this.responseBodyMapJsonField);
        this.responseBodyMapJsonFeedback = new TextFeedbackPanel("responseBodyMapJsonFeedback", this.responseBodyMapJsonField);
        this.form.add(this.responseBodyMapJsonFeedback);

        this.responseBodyEnumField = new Select2SingleChoice<>("responseBodyEnumField", new PropertyModel<>(this, "responseBodyEnum"), new EnumChoiceProvider(getSession().getApplicationCode()), new JsonChoiceRenderer());
        this.responseBodyEnumField.setOutputMarkupId(true);
        this.form.add(this.responseBodyEnumField);
        this.responseBodyEnumFeedback = new TextFeedbackPanel("responseBodyEnumFeedback", this.responseBodyEnumField);
        this.form.add(this.responseBodyEnumFeedback);

        this.responseHeaderRequiredField = new Select2MultipleChoice<>("responseHeaderRequiredField", new PropertyModel<>(this, "responseHeaderRequired"), new HttpHeaderChoiceProvider(getSession().getApplicationCode()), new HttpHeaderRenderer());
        this.responseHeaderRequiredField.setOutputMarkupId(true);
        this.form.add(this.responseHeaderRequiredField);
        this.responseHeaderRequiredFeedback = new TextFeedbackPanel("responseHeaderRequiredFeedback", this.responseHeaderRequiredField);
        this.form.add(this.responseHeaderRequiredFeedback);

        this.responseHeaderOptionalField = new Select2MultipleChoice<>("responseHeaderOptionalField", new PropertyModel<>(this, "responseHeaderOptional"), new HttpHeaderChoiceProvider(getSession().getApplicationCode()), new HttpHeaderRenderer());
        this.responseHeaderOptionalField.setOutputMarkupId(true);
        this.form.add(this.responseHeaderOptionalField);
        this.responseHeaderOptionalFeedback = new TextFeedbackPanel("responseHeaderOptionalFeedback", this.responseHeaderOptionalField);
        this.form.add(this.responseHeaderOptionalFeedback);

        this.script = getString("javascript.script");
        this.scriptField = new JavascriptTextArea("scriptField", new PropertyModel<>(this, "script"));
        this.scriptField.setRequired(true);
        this.form.add(this.scriptField);
        this.scriptFeedback = new TextFeedbackPanel("scriptFeedback", this.scriptField);
        this.form.add(this.scriptFeedback);

        this.form.add(new RestNameValidator(getSession().getApplicationCode(), this.requestPathField, this.methodField));
        this.form.add(new RestHttpHeaderValidator(getSession().getApplicationCode(), this.requestHeaderRequiredField, this.requestHeaderOptionalField));
        this.form.add(new RestHttpHeaderValidator(getSession().getApplicationCode(), this.responseHeaderRequiredField, this.responseHeaderOptionalField));
        this.form.add(new RestHttpQueryValidator(getSession().getApplicationCode(), this.requestQueryRequiredField, this.requestQueryOptionalField));

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forCSS(".CodeMirror-fullscreen {padding-left:230px !important; padding-top:50px !important;}", "CodeMirror-fullscreen"));
    }

    private void saveButtonOnSubmit(Button button) {

    }

    private void requestBodySubTypeFieldAjaxUpdate(AjaxRequestTarget target) {
        if (TypeEnum.Map.getLiteral().equals(this.requestBodySubType)) {
            this.requestBodyMapJsonField.setRequired(true);
        } else {
            this.requestBodyMapJsonField.setRequired(false);
        }
        if (TypeEnum.Enum.getLiteral().equals(this.requestBodySubType)) {
            this.requestBodyEnumField.setRequired(true);
        } else {
            this.requestBodyEnumField.setRequired(false);
        }
    }

    private void responseBodySubTypeFieldAjaxUpdate(AjaxRequestTarget target) {
        if (TypeEnum.Map.getLiteral().equals(this.responseBodySubType)) {
            this.responseBodyMapJsonField.setRequired(true);
        } else {
            this.responseBodyMapJsonField.setRequired(false);
        }
        if (TypeEnum.Enum.getLiteral().equals(this.responseBodySubType)) {
            this.responseBodyEnumField.setRequired(true);
        } else {
            this.responseBodyEnumField.setRequired(false);
        }
    }

    private void requestBodyTypeFieldAjaxUpdate(AjaxRequestTarget target) {
        target.add(this.requestBodySubTypeField);
        if (TypeEnum.List.getLiteral().equals(this.requestBodyType)) {
            this.requestBodySubType = null;
            this.requestBodySubTypes.clear();
            this.requestBodySubTypes.add(TypeEnum.Boolean.getLiteral());
            this.requestBodySubTypes.add(TypeEnum.Byte.getLiteral());
            this.requestBodySubTypes.add(TypeEnum.Long.getLiteral());
            this.requestBodySubTypes.add(TypeEnum.Double.getLiteral());
            this.requestBodySubTypes.add(TypeEnum.String.getLiteral());
            this.requestBodySubTypes.add(TypeEnum.Date.getLiteral());
            this.requestBodySubTypes.add(TypeEnum.Time.getLiteral());
            this.requestBodySubTypes.add(TypeEnum.DateTime.getLiteral());
            this.requestBodySubTypes.add(TypeEnum.Enum.getLiteral());
            this.requestBodySubTypes.add(TypeEnum.Map.getLiteral());
            this.requestBodySubTypeField.setRequired(true);
        } else {
            this.requestBodySubTypeField.setRequired(false);
            this.requestBodySubType = null;
            this.requestBodySubTypes.clear();
        }
        if (TypeEnum.Map.getLiteral().equals(this.requestBodyType)) {
            this.requestBodyMapJsonField.setRequired(true);
        } else {
            this.requestBodyMapJsonField.setRequired(false);
        }
        if (TypeEnum.Enum.getLiteral().equals(this.requestBodyType)) {
            this.requestBodyEnumField.setRequired(true);
        } else {
            this.requestBodyEnumField.setRequired(false);
        }
    }

    private void responseBodyTypeFieldAjaxUpdate(AjaxRequestTarget target) {
        target.add(this.responseBodySubTypeField);
        if (TypeEnum.List.getLiteral().equals(this.responseBodyType)) {
            this.responseBodySubType = null;
            this.responseBodySubTypes.clear();
            this.responseBodySubTypes.add(TypeEnum.Boolean.getLiteral());
            this.responseBodySubTypes.add(TypeEnum.Byte.getLiteral());
            this.responseBodySubTypes.add(TypeEnum.Long.getLiteral());
            this.responseBodySubTypes.add(TypeEnum.Double.getLiteral());
            this.responseBodySubTypes.add(TypeEnum.String.getLiteral());
            this.responseBodySubTypes.add(TypeEnum.Date.getLiteral());
            this.responseBodySubTypes.add(TypeEnum.Time.getLiteral());
            this.responseBodySubTypes.add(TypeEnum.DateTime.getLiteral());
            this.responseBodySubTypes.add(TypeEnum.Enum.getLiteral());
            this.responseBodySubTypes.add(TypeEnum.Map.getLiteral());
            this.responseBodySubTypeField.setRequired(true);
        } else {
            this.responseBodySubTypeField.setRequired(false);
            this.responseBodySubType = null;
            this.responseBodySubTypes.clear();
        }
        if (TypeEnum.Map.getLiteral().equals(this.responseBodyType)) {
            this.responseBodyMapJsonField.setRequired(true);
        } else {
            this.responseBodyMapJsonField.setRequired(false);
        }
        if (TypeEnum.Enum.getLiteral().equals(this.responseBodyType)) {
            this.responseBodyEnumField.setRequired(true);
        } else {
            this.responseBodyEnumField.setRequired(false);
        }
    }

    private void requestContentTypeFieldAjaxUpdate(AjaxRequestTarget target) {
        this.requestBodyMapJsonProvider.setContentType(this.requestContentType);
        target.add(this.requestBodyTypeField);
        target.add(this.requestContentTypeField);
        if (MediaType.APPLICATION_OCTET_STREAM_VALUE.equals(this.requestContentType)) {
            this.requestBodyType = TypeEnum.File.getLiteral();
            this.requestBodyTypes.clear();
            this.requestBodyTypes.add(TypeEnum.File.getLiteral());
        } else if (MediaType.MULTIPART_FORM_DATA_VALUE.equals(this.requestContentType) || MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(this.requestContentType)) {
            this.requestBodyType = TypeEnum.Map.getLiteral();
            this.requestBodyTypes.clear();
            this.requestBodyTypes.add(TypeEnum.Map.getLiteral());
        } else if (MediaType.APPLICATION_JSON_VALUE.equals(this.requestContentType)) {
            this.requestBodyType = null;
            this.requestBodyTypes.clear();
            this.requestBodyTypes.add(TypeEnum.Boolean.getLiteral());
            this.requestBodyTypes.add(TypeEnum.Long.getLiteral());
            this.requestBodyTypes.add(TypeEnum.Double.getLiteral());
            this.requestBodyTypes.add(TypeEnum.String.getLiteral());
            this.requestBodyTypes.add(TypeEnum.Date.getLiteral());
            this.requestBodyTypes.add(TypeEnum.Time.getLiteral());
            this.requestBodyTypes.add(TypeEnum.DateTime.getLiteral());
            this.requestBodyTypes.add(TypeEnum.Enum.getLiteral());
            this.requestBodyTypes.add(TypeEnum.Map.getLiteral());
            this.requestBodyTypes.add(TypeEnum.List.getLiteral());
        }
    }

    private void responseContentTypeFieldAjaxUpdate(AjaxRequestTarget target) {
        target.add(this.responseBodyTypeField);
        target.add(this.responseContentTypeField);
        if (MediaType.APPLICATION_OCTET_STREAM_VALUE.equals(this.responseContentType)) {
            this.responseBodyType = TypeEnum.File.getLiteral();
            this.responseBodyTypes.clear();
            this.responseBodyTypes.add(TypeEnum.File.getLiteral());
        } else if (MediaType.MULTIPART_FORM_DATA_VALUE.equals(this.responseContentType) || MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(this.responseContentType)) {
            this.responseBodyType = TypeEnum.Map.getLiteral();
            this.responseBodyTypes.clear();
            this.responseBodyTypes.add(TypeEnum.Map.getLiteral());
        } else if (MediaType.APPLICATION_JSON_VALUE.equals(this.responseContentType)) {
            this.responseBodyType = null;
            this.responseBodyTypes.clear();
            this.responseBodyTypes.add(TypeEnum.Boolean.getLiteral());
            this.responseBodyTypes.add(TypeEnum.Long.getLiteral());
            this.responseBodyTypes.add(TypeEnum.Double.getLiteral());
            this.responseBodyTypes.add(TypeEnum.String.getLiteral());
            this.responseBodyTypes.add(TypeEnum.Date.getLiteral());
            this.responseBodyTypes.add(TypeEnum.Time.getLiteral());
            this.responseBodyTypes.add(TypeEnum.DateTime.getLiteral());
            this.responseBodyTypes.add(TypeEnum.Enum.getLiteral());
            this.responseBodyTypes.add(TypeEnum.Map.getLiteral());
            this.responseBodyTypes.add(TypeEnum.List.getLiteral());
        }
    }

    private void methodFieldAjaxUpdate(AjaxRequestTarget target) {
        target.add(this.requestContentTypeField);
        target.add(this.methodField);
        if (this.method.equals(HttpMethod.GET.name()) || this.method.equals(HttpMethod.DELETE.name())) {
            this.requestContentType = null;
            this.requestContentTypes.clear();
            this.requestContentTypeField.setRequired(false);
            this.requestBodyTypeField.setRequired(false);
            this.requestBodySubTypeField.setRequired(false);
        } else if (this.method.equals(HttpMethod.PUT.name()) || this.method.equals(HttpMethod.POST.name())) {
            this.requestContentType = null;
            this.requestContentTypes.clear();
            this.requestContentTypes.add(MediaType.MULTIPART_FORM_DATA_VALUE);
            this.requestContentTypes.add(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            this.requestContentTypes.add(MediaType.APPLICATION_JSON_VALUE);
            this.requestContentTypes.add(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            this.requestContentTypeField.setRequired(true);
            this.requestBodyTypeField.setRequired(true);
        }
    }

    private void formOnError(Form<Void> form) {
        System.out.println("");
    }

}
