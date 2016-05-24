package com.angkorteam.mbaas.server.page.query;

import com.angkorteam.framework.extension.spring.SimpleJdbcUpdate;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.form.SQLTextArea;
import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.plain.enums.SubTypeEnum;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.server.Jdbc;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

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
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();

        Map<String, Object> queryRecord = null;
        queryRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.QUERY + " WHERE " + Jdbc.Query.QUERY_ID + " = ?", this.queryId);

        this.form = new Form<>("form");
        add(this.form);

        this.name = (String) queryRecord.get(Jdbc.Query.NAME);
        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.nameField.add(new QueryNameValidator(this.queryId));
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.description = (String) queryRecord.get(Jdbc.Query.DESCRIPTION);
        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.script = (String) queryRecord.get(Jdbc.Query.SCRIPT);
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
        this.returnType = (String) queryRecord.get(Jdbc.Query.RETURN_TYPE);
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
        this.returnSubType = (String) queryRecord.get(Jdbc.Query.RETURN_SUB_TYPE);
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

    private void saveButtonOnSubmit(Button button) {
        saveQuery();
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();

        int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + Jdbc.QUERY_PARAMETER + " WHERE " + Jdbc.QueryParameter.QUERY_ID + " = ? AND " + Jdbc.QueryParameter.TYPE + " IS NULL ", int.class, this.queryId);
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
        Map<String, Object> wheres = new HashMap<>();
        wheres.put(Jdbc.Query.QUERY_ID, this.queryId);
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.Query.NAME, this.name);
        fields.put(Jdbc.Query.SCRIPT, this.script);
        fields.put(Jdbc.Query.DESCRIPTION, this.description);
        fields.put(Jdbc.Query.RETURN_TYPE, this.returnType);
        fields.put(Jdbc.Query.RETURN_SUB_TYPE, this.returnSubType);

        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        SimpleJdbcUpdate jdbcUpdate = new SimpleJdbcUpdate(jdbcTemplate);
        jdbcUpdate.withTableName(Jdbc.QUERY);
        jdbcUpdate.execute(fields, wheres);

        Map<String, Map<String, Object>> queryParameterRecords = new LinkedHashMap<>();
        for (Map<String, Object> queryParameterRecord : jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.QUERY_PARAMETER + " WHERE " + Jdbc.QueryParameter.QUERY_ID + " = ?", this.queryId)) {
            queryParameterRecords.put((String) queryParameterRecord.get(Jdbc.QueryParameter.NAME), queryParameterRecord);
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
        for (Map.Entry<String, Map<String, Object>> queryParameterRecord : queryParameterRecords.entrySet()) {
            if (!queryParameters.contains(queryParameterRecord.getKey())) {
                String queryParameterId = (String) queryParameterRecord.getValue().get(Jdbc.QueryParameter.QUERY_PARAMETER_ID);
                jdbcTemplate.update("DELETE FROM " + Jdbc.QUERY_PARAMETER + " WHERE " + Jdbc.QueryParameter.QUERY_PARAMETER_ID + " = ?", queryParameterId);
            } else {
                params.add(queryParameterRecord.getKey());
            }
        }

        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(Jdbc.QUERY_PARAMETER);
        for (String queryParameter : queryParameters) {
            if (!params.contains(queryParameter)) {
                Map<String, Object> pFields = new HashMap<>();
                pFields.put(Jdbc.QueryParameter.QUERY_PARAMETER_ID, UUID.randomUUID().toString());
                pFields.put(Jdbc.QueryParameter.QUERY_ID, this.queryId);
                pFields.put(Jdbc.QueryParameter.APPLICATION_CODE, getSession().getApplicationCode());
                pFields.put(Jdbc.QueryParameter.NAME, queryParameter);
                jdbcInsert.execute(pFields);
            }
        }
    }

}
