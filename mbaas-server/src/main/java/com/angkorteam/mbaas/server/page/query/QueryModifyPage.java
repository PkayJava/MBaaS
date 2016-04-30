package com.angkorteam.mbaas.server.page.query;

import com.angkorteam.framework.extension.wicket.extensions.markup.html.form.SQLTextArea;
import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.QueryParameterTable;
import com.angkorteam.mbaas.model.entity.tables.QueryTable;
import com.angkorteam.mbaas.model.entity.tables.records.QueryParameterRecord;
import com.angkorteam.mbaas.model.entity.tables.records.QueryRecord;
import com.angkorteam.mbaas.plain.enums.SubTypeEnum;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.server.page.job.JobManagementPage;
import com.angkorteam.mbaas.server.validator.QueryNameValidator;
import com.angkorteam.mbaas.server.validator.QueryReturnSubTypeValidator;
import com.angkorteam.mbaas.server.validator.QueryScriptValidator;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.commons.configuration.XMLPropertiesConfiguration;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
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
@AuthorizeInstantiation({"administrator", "backoffice"})
@Mount("/query/modify")
public class QueryModifyPage extends MasterPage {

    private String queryId;
    private Integer optimistic;

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;
    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private String script;
    private SQLTextArea scriptField;
    private TextFeedbackPanel scriptFeedback;

    private String returnType;
    private DropDownChoice<String> returnTypeField;
    private TextFeedbackPanel returnTypeFeedback;

    private String returnSubType;
    private DropDownChoice<String> returnSubTypeField;
    private TextFeedbackPanel returnSubTypeFeedback;

    private Button saveButton;
    private Button saveAndContinueButton;

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Modify Query";
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.queryId = getPageParameters().get("queryId").toString();
        DSLContext context = getDSLContext();
        QueryTable queryTable = Tables.QUERY.as("queryTable");
        QueryRecord queryRecord = context.select(queryTable.fields()).from(queryTable).where(queryTable.QUERY_ID.eq(this.queryId)).fetchOneInto(queryTable);

        this.form = new Form<>("form");
        add(this.form);

        this.optimistic = queryRecord.getOptimistic();

        this.name = queryRecord.getName();
        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.nameField.add(new QueryNameValidator(this.queryId));
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.description = queryRecord.getDescription();
        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.script = queryRecord.getScript();
        this.scriptField = new SQLTextArea("scriptField", new PropertyModel<>(this, "script"));
        this.scriptField.setRequired(true);
        this.scriptField.add(new QueryScriptValidator());
        this.form.add(this.scriptField);
        this.scriptFeedback = new TextFeedbackPanel("scriptFeedback", this.scriptField);
        this.form.add(this.scriptFeedback);

        List<String> returnTypes = new ArrayList<>();
        for (TypeEnum typeEnum : TypeEnum.values()) {
            if (typeEnum.isSubType()) {
                returnTypes.add(typeEnum.getLiteral());
            }
        }
        this.returnType = queryRecord.getReturnType();
        this.returnTypeField = new DropDownChoice<>("returnTypeField", new PropertyModel<>(this, "returnType"), returnTypes);
        this.returnTypeField.setRequired(true);
        this.form.add(this.returnTypeField);
        this.returnTypeFeedback = new TextFeedbackPanel("returnTypeFeedback", this.returnTypeField);
        this.form.add(returnTypeFeedback);

        List<String> returnSubTypes = new ArrayList<>();
        for (SubTypeEnum typeEnum : SubTypeEnum.values()) {
            if (typeEnum.isSubType()) {
                returnSubTypes.add(typeEnum.getLiteral());
            }
        }
        this.returnSubType = queryRecord.getReturnSubType();
        this.returnSubTypeField = new DropDownChoice<>("returnSubTypeField", new PropertyModel<>(this, "returnSubType"), returnSubTypes);
        this.form.add(this.returnSubTypeField);
        this.returnSubTypeFeedback = new TextFeedbackPanel("returnSubTypeFeedback", this.returnSubTypeField);
        this.form.add(returnSubTypeFeedback);

        this.form.add(new QueryReturnSubTypeValidator(this.returnTypeField, this.returnSubTypeField));

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);

        this.saveAndContinueButton = new Button("saveAndContinueButton");
        this.saveAndContinueButton.setOnSubmit(this::saveAndContinueButtonOnSubmit);
        this.form.add(this.saveAndContinueButton);
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        DSLContext context = getDSLContext();
        QueryTable queryTable = Tables.QUERY.as("queryTable");
        QueryRecord queryRecord = context.select(queryTable.fields()).from(queryTable).where(queryTable.QUERY_ID.eq(this.queryId)).fetchOneInto(queryTable);
        if (getSession().isBackOffice() && !queryRecord.getOwnerUserId().equals(getSession().getUserId())) {
            setResponsePage(QueryManagementPage.class);
        }
    }

    private void saveButtonOnSubmit(Button button) {
        saveQuery();
        DSLContext context = getDSLContext();

        int count = context.selectCount().from(Tables.QUERY_PARAMETER).where(Tables.QUERY_PARAMETER.QUERY_ID.eq(this.queryId)).and(Tables.QUERY_PARAMETER.TYPE.isNull()).fetchOneInto(int.class);

        if (count == 0) {
            setResponsePage(QueryManagementPage.class);
        } else {
            PageParameters parameters = new PageParameters();
            parameters.add("queryId", this.queryId);
            setResponsePage(QueryParameterModifyPage.class, parameters);
        }

    }

    private void saveAndContinueButtonOnSubmit(Button button) {
        saveQuery();

        PageParameters parameters = new PageParameters();
        parameters.add("queryId", this.queryId);

        setResponsePage(QueryModifyPage.class, parameters);
    }

    private void saveQuery() {
        DSLContext context = getDSLContext();
        QueryTable queryTable = Tables.QUERY.as("queryTable");
        QueryRecord queryRecord = context.select(queryTable.fields()).from(queryTable).where(queryTable.QUERY_ID.eq(this.queryId)).fetchOneInto(queryTable);

        queryRecord.setName(this.name);
        queryRecord.setScript(this.script);
        queryRecord.setDescription(this.description);
        queryRecord.setOptimistic(this.optimistic);
        queryRecord.setReturnType(this.returnType);
        queryRecord.setReturnSubType(this.returnSubType);
        queryRecord.update();

        QueryParameterTable queryParameterTable = Tables.QUERY_PARAMETER.as("queryParameterTable");

        Map<String, QueryParameterRecord> queryParameterRecords = new LinkedHashMap<>();
        for (QueryParameterRecord queryParameterRecord : context.select(queryParameterTable.fields()).from(queryParameterTable).where(queryParameterTable.QUERY_ID.eq(this.queryId)).fetchInto(queryParameterTable)) {
            queryParameterRecords.put(queryParameterRecord.getName(), queryParameterRecord);
        }

        XMLPropertiesConfiguration configuration = Constants.getXmlPropertiesConfiguration();
        Pattern pattern = Pattern.compile(configuration.getString(Constants.PATTERN_QUERY_PARAMETER_NAME));
        Matcher matcher = pattern.matcher(this.script);
        List<String> queryParameters = new LinkedList<>();
        while (matcher.find()) {
            String parameterName = matcher.group().substring(1);
            if (!queryParameters.contains(parameterName)) {
                queryParameters.add(parameterName);
            }
        }

        List<String> params = new LinkedList<>();
        for (Map.Entry<String, QueryParameterRecord> queryParameterRecord : queryParameterRecords.entrySet()) {
            if (!queryParameters.contains(queryParameterRecord.getKey())) {
                queryParameterRecord.getValue().delete();
            } else {
                params.add(queryParameterRecord.getKey());
            }
        }

        for (String queryParameter : queryParameters) {
            if (!params.contains(queryParameter)) {
                QueryParameterRecord queryParamRecord = context.newRecord(queryParameterTable);
                queryParamRecord.setQueryId(queryRecord.getQueryId());
                queryParamRecord.setQueryParameterId((UUID.randomUUID().toString()));
                queryParamRecord.setName(queryParameter);
                queryParamRecord.store();
            }
        }
    }

}
