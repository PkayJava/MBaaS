package com.angkorteam.mbaas.server.page.query;

import com.angkorteam.framework.extension.spring.SimpleJdbcUpdate;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.plain.enums.TypeEnum;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.template.QueryParameterSelectFieldPanel;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/19/16.
 */
@AuthorizeInstantiation({"administrator"})
@Mount("/query/parameter/modify")
public class QueryParameterModifyPage extends MasterPage {

    private String queryId;
    private String queryName;

    private boolean granted = false;

    private String query;
    private Label queryLabel;

    private Map<String, String> fields;

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Modify Query Parameter :: " + queryName;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.fields = new HashMap<>();
        this.queryId = getPageParameters().get("queryId").toString();
        this.granted = getPageParameters().get("granted").toBoolean(false);

        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();

        this.form = new Form<>("form");
        add(this.form);


        Map<String, Object> queryRecord = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.QUERY + " WHERE " + Jdbc.Query.QUERY_ID + " = ?", this.queryId);

        this.query = (String) queryRecord.get(Jdbc.Query.NAME);
        this.queryLabel = new Label("queryLabel", new PropertyModel<>(this, "query"));
        this.form.add(this.queryLabel);

        List<Map<String, Object>> queryParameters = jdbcTemplate.queryForList("SELECT * FROM " + Jdbc.QUERY_PARAMETER + " WHERE " + Jdbc.QueryParameter.QUERY_ID + " = ?", this.queryId);

        List<String> types = new ArrayList<>();
        for (TypeEnum typeEnum : TypeEnum.values()) {
            if (typeEnum.isQueryType()) {
                types.add(typeEnum.getLiteral());
            }
        }

        List<String> subTypes = new ArrayList<>();
        for (TypeEnum typeEnum : TypeEnum.values()) {
            if (typeEnum.isQuerySubType()) {
                subTypes.add(typeEnum.getLiteral());
            }
        }

        RepeatingView fields = new RepeatingView("fields");
        for (Map<String, Object> queryParameter : queryParameters) {
            QueryParameterSelectFieldPanel fieldPanel = new QueryParameterSelectFieldPanel(fields.newChildId(), form, queryParameter, types, subTypes, this.fields);
            fields.add(fieldPanel);
            this.fields.put((String) queryParameter.get(Jdbc.QueryParameter.NAME), (String) queryParameter.get(Jdbc.QueryParameter.TYPE));
            this.fields.put(queryParameter.get(Jdbc.QueryParameter.NAME) + "SubType", (String) queryParameter.get(Jdbc.QueryParameter.SUB_TYPE));
        }
        this.form.add(fields);

        BookmarkablePageLink<Void> closeLink = new BookmarkablePageLink<>("closeLink", QueryManagementPage.class);
        this.form.add(closeLink);

        Button saveButton = new Button("saveButton");
        saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        JdbcTemplate jdbcTemplate = getApplicationJdbcTemplate();
        SimpleJdbcUpdate jdbcUpdate = new SimpleJdbcUpdate(jdbcTemplate);
        jdbcUpdate.withTableName(Jdbc.QUERY_PARAMETER);
        for (Map.Entry<String, String> entry : this.fields.entrySet()) {
            if (!entry.getKey().endsWith("SubType")) {
                Map<String, Object> wheres = new HashMap<>();
                wheres.put(Jdbc.QueryParameter.QUERY_ID, this.queryId);
                wheres.put(Jdbc.QueryParameter.NAME, entry.getKey());
                Map<String, Object> fields = new HashMap<>();
                fields.put(Jdbc.QueryParameter.TYPE, entry.getValue());
                fields.put(Jdbc.QueryParameter.SUB_TYPE, this.fields.get(entry.getKey() + "SubType"));
                jdbcUpdate.execute(fields, wheres);
            }
        }

        if (this.granted) {
            jdbcTemplate.update("UPDATE " + Jdbc.QUERY + " SET " + Jdbc.Query.SECURITY + " = ? WHERE " + Jdbc.Query.QUERY_ID + " = ?", SecurityEnum.Granted.getLiteral(), this.queryId);
        }

        PageParameters parameters = new PageParameters();
        parameters.add("queryId", queryId);
        setResponsePage(QueryManagementPage.class, parameters);
    }
}
