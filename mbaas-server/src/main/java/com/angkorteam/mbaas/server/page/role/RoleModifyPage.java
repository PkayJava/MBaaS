package com.angkorteam.mbaas.server.page.role;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2MultipleChoice;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.pojos.PagePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.RestPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.RolePojo;
import com.angkorteam.mbaas.model.entity.tables.records.PageRoleRecord;
import com.angkorteam.mbaas.model.entity.tables.records.RestRoleRecord;
import com.angkorteam.mbaas.model.entity.tables.records.RoleRecord;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.System;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.select2.PagesChoiceProvider;
import com.angkorteam.mbaas.server.select2.RestsChoiceProvider;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.List;

/**
 * Created by socheat on 10/24/16.
 */
public class RoleModifyPage extends MBaaSPage {

    private String roleId;

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private String description;
    private TextField<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

    private List<PagePojo> cmsPage;
    private Select2MultipleChoice<PagePojo> pageField;
    private TextFeedbackPanel pageFeedback;

    private List<RestPojo> rest;
    private Select2MultipleChoice<RestPojo> restField;
    private TextFeedbackPanel restFeedback;

    private Form<Void> form;
    private Button saveButton;
    private BookmarkablePageLink<Void> closeButton;

    @Override
    public String getPageUUID() {
        return RoleModifyPage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);

        DSLContext context = Spring.getBean(DSLContext.class);
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        PageTable pageTable = Tables.PAGE.as("pageTable");
        PageRoleTable pageRoleTable = Tables.PAGE_ROLE.as("pageRoleTable");
        RestRoleTable restRoleTable = Tables.REST_ROLE.as("restRoleTable");
        RestTable restTable = Tables.REST.as("restTable");

        PageParameters parameters = getPageParameters();
        this.roleId = parameters.get("roleId").toString("");

        RolePojo role = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(this.roleId)).fetchOneInto(RolePojo.class);
        this.name = role.getName();
        this.description = role.getDescription();
        this.cmsPage = context.select(pageTable.fields()).from(pageTable).innerJoin(pageRoleTable).on(pageTable.PAGE_ID.eq(pageRoleTable.PAGE_ID)).where(pageRoleTable.ROLE_ID.eq(this.roleId)).fetchInto(PagePojo.class);
        this.rest = context.select(restTable.fields()).from(restTable).innerJoin(restRoleTable).on(restTable.REST_ID.eq(restRoleTable.REST_ID)).where(restRoleTable.ROLE_ID.eq(this.roleId)).fetchInto(RestPojo.class);

        this.form = new Form<>("form");
        layout.add(this.form);

        this.pageField = new Select2MultipleChoice<>("pageField", new PropertyModel<>(this, "cmsPage"), new PagesChoiceProvider());
        this.form.add(this.pageField);
        this.pageFeedback = new TextFeedbackPanel("pageFeedback", this.pageField);
        this.form.add(this.pageFeedback);

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

        this.restField = new Select2MultipleChoice<>("restField", new PropertyModel<>(this, "rest"), new RestsChoiceProvider());
        this.form.add(this.restField);
        this.restFeedback = new TextFeedbackPanel("restFeedback", this.restField);
        this.form.add(this.restFeedback);

        this.descriptionField = new TextField<>("descriptionField", new PropertyModel<>(this, "description"));
        this.descriptionField.setRequired(true);
        this.form.add(this.descriptionField);
        this.descriptionFeedback = new TextFeedbackPanel("descriptionFeedback", this.descriptionField);
        this.form.add(this.descriptionFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);

        this.closeButton = new BookmarkablePageLink<>("closeButton", RoleBrowsePage.class);
        this.form.add(this.closeButton);
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = Spring.getBean(DSLContext.class);
        System system = Spring.getBean(System.class);
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        PageRoleTable pageRoleTable = Tables.PAGE_ROLE.as("pageRoleTable");
        RestRoleTable restRoleTable = Tables.REST_ROLE.as("restRoleTable");
        RoleRecord roleRecord = context.select(roleTable.fields()).from(roleTable).where(roleTable.ROLE_ID.eq(this.roleId)).fetchOneInto(roleTable);
        roleRecord.setName(this.name);
        roleRecord.setDescription(this.description);
        roleRecord.update();

        context.delete(pageRoleTable).where(pageRoleTable.ROLE_ID.eq(this.roleId)).execute();

        if (this.cmsPage != null && !this.cmsPage.isEmpty()) {
            for (PagePojo page : this.cmsPage) {
                PageRoleRecord pageRoleRecord = context.newRecord(pageRoleTable);
                pageRoleRecord.setPageRoleId(system.randomUUID());
                pageRoleRecord.setPageId(page.getPageId());
                pageRoleRecord.setRoleId(this.roleId);
                pageRoleRecord.store();
            }
        }

        context.delete(restRoleTable).where(restRoleTable.ROLE_ID.eq(this.roleId)).execute();

        if (this.rest != null && !this.rest.isEmpty()) {
            for (RestPojo rest : this.rest) {
                RestRoleRecord restRoleRecord = context.newRecord(restRoleTable);
                restRoleRecord.setRestRoleId(system.randomUUID());
                restRoleRecord.setRestId(rest.getRestId());
                restRoleRecord.setRoleId(roleId);
                restRoleRecord.store();
            }
        }

        setResponsePage(RoleBrowsePage.class);
    }

}
