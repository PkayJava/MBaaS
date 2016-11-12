package com.angkorteam.mbaas.server.page.role;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2MultipleChoice;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.PageRoleTable;
import com.angkorteam.mbaas.model.entity.tables.RestRoleTable;
import com.angkorteam.mbaas.model.entity.tables.RoleTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.PagePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.RestPojo;
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
import org.jooq.DSLContext;

import java.util.List;

/**
 * Created by socheat on 10/24/16.
 */
public class RoleCreatePage extends MBaaSPage {

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
        return RoleCreatePage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);

        this.form = new Form<>("form");
        layout.add(this.form);

        this.pageField = new Select2MultipleChoice<>("pageField", new PropertyModel<>(this, "cmsPage"), new PagesChoiceProvider());
        this.form.add(this.pageField);
        this.pageFeedback = new TextFeedbackPanel("pageFeedback", this.pageField);
        this.form.add(this.pageFeedback);

        this.restField = new Select2MultipleChoice<>("restField", new PropertyModel<>(this, "rest"), new RestsChoiceProvider());
        this.form.add(this.restField);
        this.restFeedback = new TextFeedbackPanel("restFeedback", this.restField);
        this.form.add(this.restFeedback);

        this.nameField = new TextField<>("nameField", new PropertyModel<>(this, "name"));
        this.nameField.setRequired(true);
        this.form.add(this.nameField);
        this.nameFeedback = new TextFeedbackPanel("nameFeedback", this.nameField);
        this.form.add(this.nameFeedback);

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
        System system = Spring.getBean(System.class);
        String roleId = system.randomUUID();
        DSLContext context = Spring.getBean(DSLContext.class);
        PageRoleTable pageRoleTable = Tables.PAGE_ROLE.as("pageRoleTable");
        RestRoleTable restRoleTable = Tables.REST_ROLE.as("restRoleTable");
        RoleTable roleTable = Tables.ROLE.as("roleTable");
        RoleRecord roleRecord = context.newRecord(roleTable);
        roleRecord.setRoleId(roleId);
        roleRecord.setName(this.name);
        roleRecord.setDescription(this.description);
        roleRecord.setSystem(false);
        roleRecord.store();

        if (this.cmsPage != null && !this.cmsPage.isEmpty()) {
            for (PagePojo page : this.cmsPage) {
                PageRoleRecord pageRoleRecord = context.newRecord(pageRoleTable);
                pageRoleRecord.setPageRoleId(system.randomUUID());
                pageRoleRecord.setPageId(page.getPageId());
                pageRoleRecord.setRoleId(roleId);
                pageRoleRecord.store();
            }
        }

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
