package com.angkorteam.mbaas.server.page.query;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.SQLTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.configuration.Constants;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
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
@AuthorizeInstantiation({"administrator"})
@Mount("/query/create")
public class QueryCreatePage extends MasterPage {

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
        this.nameField.add(new QueryNameValidator(getSession().getApplicationCode()));
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.scriptField = new SQLTextArea("scriptField", new PropertyModel<>(this, "script"));
        this.scriptField.setRequired(true);
        this.scriptField.add(new QueryScriptValidator());
        this.form.add(this.scriptField);
        this.scriptFeedback = new TextFeedbackPanel("scriptFeedback", this.scriptField);
        this.form.add(this.scriptFeedback);

        List<String> returnTypes = new ArrayList<>();
        for (TypeEnum typeEnum : TypeEnum.values()) {
            if (typeEnum.isQueryType()) {
                returnTypes.add(typeEnum.getLiteral());
            }
        }
        this.returnTypeField = new DropDownChoice<>("returnTypeField", new PropertyModel<>(this, "returnType"), returnTypes);
        this.returnTypeField.setRequired(true);
        this.form.add(this.returnTypeField);
        this.returnTypeFeedback = new TextFeedbackPanel("returnTypeFeedback", this.returnTypeField);
        this.form.add(returnTypeFeedback);

        List<String> returnSubTypes = new ArrayList<>();
        for (TypeEnum typeEnum : TypeEnum.values()) {
            if (typeEnum.isQuerySubType()) {
                returnSubTypes.add(typeEnum.getLiteral());
            }
        }
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
        String queryId = UUID.randomUUID().toString();
        Map<String, Object> fields = new HashMap<>();
        fields.put(Jdbc.Query.QUERY_ID, queryId);
        fields.put(Jdbc.Query.NAME, this.name);
        fields.put(Jdbc.Query.SECURITY, SecurityEnum.Denied.getLiteral());
        fields.put(Jdbc.Query.SCRIPT, this.script);
        fields.put(Jdbc.Query.USER_ID, getSession().getApplicationUserId());
        fields.put(Jdbc.Query.APPLICATION_CODE, getApplicationCode());
        fields.put(Jdbc.Query.DATE_CREATED, new Date());
        fields.put(Jdbc.Query.DESCRIPTION, this.description);
        fields.put(Jdbc.Query.RETURN_TYPE, this.returnType);
        fields.put(Jdbc.Query.RETURN_SUB_TYPE, this.returnSubType);
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(Jdbc.QUERY);
        jdbcInsert.execute(fields);

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

        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(Jdbc.QUERY_PARAMETER);
        for (String queryParameter : queryParameters) {
            fields = new HashMap<>();
            fields.put(Jdbc.QueryParameter.QUERY_PARAMETER_ID, UUID.randomUUID().toString());
            fields.put(Jdbc.QueryParameter.QUERY_ID, queryId);
            fields.put(Jdbc.QueryParameter.APPLICATION_CODE, getSession().getApplicationCode());
            fields.put(Jdbc.QueryParameter.NAME, queryParameter);
            jdbcInsert.execute(fields);
        }

        if (queryParameters.isEmpty()) {
            setResponsePage(QueryManagementPage.class);
        } else {
            PageParameters parameters = new PageParameters();
            parameters.add("queryId", queryId);
            setResponsePage(QueryParameterModifyPage.class, parameters);
        }
    }

}
