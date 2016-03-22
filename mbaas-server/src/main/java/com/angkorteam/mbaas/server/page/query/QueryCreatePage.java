package com.angkorteam.mbaas.server.page.query;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.QueryParameterTable;
import com.angkorteam.mbaas.model.entity.tables.QueryTable;
import com.angkorteam.mbaas.model.entity.tables.records.QueryParameterRecord;
import com.angkorteam.mbaas.model.entity.tables.records.QueryRecord;
import com.angkorteam.mbaas.plain.enums.QueryReturnTypeEnum;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.validator.QueryNameValidator;
import com.angkorteam.mbaas.server.validator.QueryPathValidator;
import com.angkorteam.mbaas.server.validator.QueryReturnSubTypeValidator;
import com.angkorteam.mbaas.server.validator.QueryScriptValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by socheat on 3/10/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/query/create")
public class QueryCreatePage extends MasterPage {

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private String pathText;
    private TextField<String> pathField;
    private TextFeedbackPanel pathFeedback;

    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private String script;
    private TextArea<String> scriptField;
    private TextFeedbackPanel scriptFeedback;

    private String returnType;
    private DropDownChoice<String> returnTypeField;
    private TextFeedbackPanel returnTypeFeedback;

    private String returnSubType;
    private DropDownChoice<String> returnSubTypeField;
    private TextFeedbackPanel returnSubTypeFeedback;

    private Button saveButton;

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Create New Query";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.form = new Form<>("form");
        add(this.form);

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.nameField.add(new QueryNameValidator());
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.pathField = new TextField<>("pathField", new PropertyModel<>(this, "pathText"));
        this.pathField.setRequired(true);
        this.pathField.add(new QueryPathValidator());
        this.form.add(this.pathField);
        this.pathFeedback = new TextFeedbackPanel("pathFeedback", this.pathField);
        this.form.add(this.pathFeedback);

        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.scriptField = new TextArea<>("scriptField", new PropertyModel<>(this, "script"));
        this.scriptField.setRequired(true);
        this.scriptField.add(new QueryScriptValidator());
        this.form.add(this.scriptField);
        this.scriptFeedback = new TextFeedbackPanel("scriptFeedback", this.scriptField);
        this.form.add(this.scriptFeedback);

        List<String> returnTypes = new ArrayList<>();
        List<String> returnSubTypes = new ArrayList<>();
        for (QueryReturnTypeEnum queryReturnTypeEnum : QueryReturnTypeEnum.values()) {
            returnTypes.add(queryReturnTypeEnum.getLiteral());
            if (queryReturnTypeEnum.isSubType()) {
                returnSubTypes.add(queryReturnTypeEnum.getLiteral());
            }
        }

        this.returnTypeField = new DropDownChoice<>("returnTypeField", new PropertyModel<>(this, "returnType"), returnTypes);
        this.returnTypeField.setRequired(true);
        this.form.add(this.returnTypeField);
        this.returnTypeFeedback = new TextFeedbackPanel("returnTypeFeedback", this.returnTypeField);
        this.form.add(returnTypeFeedback);

        this.returnSubTypeField = new DropDownChoice<>("returnSubTypeField", new PropertyModel<>(this, "returnSubType"), returnSubTypes);
        this.form.add(this.returnSubTypeField);
        this.returnSubTypeFeedback = new TextFeedbackPanel("returnSubTypeFeedback", this.returnSubTypeField);
        this.form.add(returnSubTypeFeedback);

        this.form.add(new QueryReturnSubTypeValidator(this.returnTypeField, this.returnSubTypeField));

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        QueryTable queryTable = Tables.QUERY.as("queryTable");

        String uuid = UUID.randomUUID().toString();

        QueryRecord queryRecord = context.newRecord(queryTable);

        queryRecord.setQueryId(uuid);
        queryRecord.setName(this.name);
        queryRecord.setSecurity(SecurityEnum.Denied.getLiteral());
        queryRecord.setPath(this.pathText);
        queryRecord.setScript(this.script);
        queryRecord.setOwnerUserId(getSession().getUserId());
        queryRecord.setDateCreated(new Date());
        queryRecord.setDescription(this.description);
        queryRecord.setReturnType(this.returnType);
        queryRecord.setReturnSubType(this.returnSubType);
        queryRecord.store();

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        Pattern pattern = Pattern.compile(configuration.getString(Constants.PATTERN_QUERY_PARAMETER_NAME));
        Matcher matcher = pattern.matcher(this.script);
        QueryParameterTable queryParameterTable = Tables.QUERY_PARAMETER.as("queryParameterTable");
        List<String> queryParameters = new LinkedList<>();
        while (matcher.find()) {
            String parameterName = matcher.group().substring(1);
            if (!queryParameters.contains(parameterName)) {
                queryParameters.add(parameterName);
            }
        }

        for (String queryParameter : queryParameters) {
            QueryParameterRecord queryParamRecord = context.newRecord(queryParameterTable);
            queryParamRecord.setQueryId(queryRecord.getQueryId());
            queryParamRecord.setQueryParameterId((UUID.randomUUID().toString()));
            queryParamRecord.setName(queryParameter);
            queryParamRecord.store();
        }

        if (queryParameters.isEmpty()) {
            setResponsePage(QueryManagementPage.class);
        } else {
            PageParameters parameters = new PageParameters();
            parameters.add("queryId", queryRecord.getQueryId());
            setResponsePage(QueryParameterModifyPage.class, parameters);
        }
    }

}
