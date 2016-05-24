//package com.angkorteam.mbaas.server.page;
//
//import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
//import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
//import com.angkorteam.mbaas.model.entity.Tables;
//import com.angkorteam.mbaas.server.wicket.MasterPage;
//import com.angkorteam.mbaas.server.wicket.Mount;
//import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
//import org.apache.wicket.markup.html.form.Form;
//import org.apache.wicket.markup.html.form.upload.FileUpload;
//import org.apache.wicket.markup.html.form.upload.FileUploadField;
//import org.apache.wicket.model.PropertyModel;
//import org.jooq.DSLContext;
//
//import java.io.File;
//import java.util.List;
//
///**
// * Created by socheat on 5/1/16.
// */
//@AuthorizeInstantiation({"administrator", "backoffice"})
//@Mount("/restore")
//public class RestorePage extends MasterPage {
//
//    private List<FileUpload> backup;
//    private FileUploadField backupField;
//    private TextFeedbackPanel backupFeedback;
//
//    private Button restoreButton;
//
//    private Form<Void> form;
//
//    @Override
//    public String getPageHeader() {
//        return "Restore Application";
//    }
//
//    @Override
//    protected void onInitialize() {
//        super.onInitialize();
//
//        this.form = new Form<>("form");
//        add(this.form);
//
//        this.backupField = new FileUploadField("backupField", new PropertyModel<>(this, "backup"));
//        this.backupField.setRequired(true);
//        this.form.add(this.backupField);
//        this.backupFeedback = new TextFeedbackPanel("backupFeedback", this.backupField);
//        this.form.add(backupFeedback);
//
//        this.restoreButton = new Button("restoreButton");
//        this.restoreButton.setOnSubmit(this::restoreButtonOnSubmit);
//        this.form.add(restoreButton);
//    }
//
//    private void restoreButtonOnSubmit(Button button) {
//        FileUpload file = this.backup.get(0);
//        try {
//            File backup = file.writeToTempFile();
//            // TODO
////            ApplicationFunction.restore(getJdbcTemplate(), backup, getSession().getUserId());
////            setResponsePage(ApplicationManagementPage.class);
//        } catch (Exception e) {
//        }
//        DSLContext context = getDSLContext();
//        // TODO
//        int count = 0;
////        count = context.selectCount().from(Tables.APPLICATION).where(Tables.APPLICATION.OWNER_USER_ID.eq(getSession().getUserId())).fetchOneInto(int.class);
//        if (count == 1) {
//            String applicationId = context.select(Tables.APPLICATION.APPLICATION_ID).from(Tables.APPLICATION).limit(1).fetchOneInto(String.class);
//            getSession().setApplicationId(applicationId);
//        }
//        setResponsePage(MBaaSDashboardPage.class);
//    }
//}
