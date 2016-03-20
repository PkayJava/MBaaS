package com.angkorteam.mbaas.server.page.query;

import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.QueryParameterTable;
import com.angkorteam.mbaas.model.entity.tables.QueryTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.QueryParameterPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.QueryPojo;
import com.angkorteam.mbaas.model.entity.tables.records.QueryParameterRecord;
import com.angkorteam.mbaas.plain.enums.QueryInputParamTypeEnum;
import com.angkorteam.mbaas.plain.enums.SecurityEnum;
import com.angkorteam.mbaas.server.template.SelectFieldPanel;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import com.angkorteam.mbaas.server.wicket.Mount;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 3/19/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/query/parameter/modify")
public class QueryParameterModifyPage extends MasterPage {

    private String queryId;
    private QueryPojo queryPojo;

    private boolean granted = false;

    private String query;
    private Label queryLabel;

    private Map<String, String> fields;

    private Form<Void> form;

    @Override
    public String getPageHeader() {
        return "Modify Query Parameter :: " + queryPojo.getName();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.fields = new HashMap<>();
        this.queryId = getPageParameters().get("queryId").toString();
        this.granted = getPageParameters().get("granted").toBoolean(false);

        DSLContext context = getDSLContext();

        this.form = new Form<>("form");
        add(this.form);

        QueryTable queryTable = Tables.QUERY.as("queryTable");
        this.queryPojo = context.select(queryTable.fields()).from(queryTable).where(queryTable.QUERY_ID.eq(this.queryId)).fetchOneInto(QueryPojo.class);

        this.query = queryPojo.getName();
        this.queryLabel = new Label("queryLabel", new PropertyModel<>(this, "query"));
        this.form.add(this.queryLabel);

        QueryParameterTable queryParameterTable = Tables.QUERY_PARAMETER.as("queryParameterTable");
        List<QueryParameterPojo> queryParameters = context.select(queryParameterTable.fields())
                .from(queryParameterTable)
                .where(queryParameterTable.QUERY_ID.eq(queryId))
                .fetchInto(QueryParameterPojo.class);

        List<String> types = new ArrayList<>();
        for (QueryInputParamTypeEnum queryInputParamTypeEnum : QueryInputParamTypeEnum.values()) {
            types.add(queryInputParamTypeEnum.getLiteral());
        }

        RepeatingView fields = new RepeatingView("fields");
        for (QueryParameterPojo queryParameter : queryParameters) {
            SelectFieldPanel fieldPanel = new SelectFieldPanel(fields.newChildId(), queryParameter, types, this.fields);
            fields.add(fieldPanel);
            this.fields.put(queryParameter.getName(), queryParameter.getType());
        }
        this.form.add(fields);

        BookmarkablePageLink<Void> closeLink = new BookmarkablePageLink<>("closeLink", QueryManagementPage.class);
        this.form.add(closeLink);

        Button saveButton = new Button("saveButton");
        saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(saveButton);
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = getDSLContext();
        QueryParameterTable queryParameterTable = Tables.QUERY_PARAMETER.as("queryParameterTable");

        for (Map.Entry<String, String> entry : this.fields.entrySet()) {
            QueryParameterRecord queryParameterRecord = context.select(queryParameterTable.fields()).from(queryParameterTable).where(queryParameterTable.QUERY_ID.eq(this.queryId)).and(queryParameterTable.NAME.eq(entry.getKey())).fetchOneInto(queryParameterTable);
            queryParameterRecord.setType(entry.getValue());
            queryParameterRecord.update();
        }

        if (this.granted) {
            context.update(Tables.QUERY).set(Tables.QUERY.SECURITY, SecurityEnum.Granted.getLiteral()).where(Tables.QUERY.QUERY_ID.eq(this.queryId)).execute();
        }

        PageParameters parameters = new PageParameters();
        parameters.add("queryId", queryId);
        setResponsePage(QueryManagementPage.class, parameters);
    }
}
