//package com.angkorteam.mbaas.server.page.oauth2;
//
//import com.angkorteam.framework.extension.wicket.AdminLTEPage;
//import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
//import com.angkorteam.framework.extension.wicket.html.form.AjaxButton;
//import com.angkorteam.framework.extension.wicket.html.form.Form;
//import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
//import com.angkorteam.mbaas.model.entity.Tables;
//import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
//import com.angkorteam.mbaas.model.entity.tables.ClientTable;
//import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
//import com.angkorteam.mbaas.model.entity.tables.records.ClientRecord;
//import com.angkorteam.mbaas.server.wicket.JooqUtils;
//import com.angkorteam.mbaas.server.wicket.Mount;
//import com.angkorteam.mbaas.server.wicket.Session;
//import org.apache.commons.lang3.RandomStringUtils;
//import org.apache.wicket.ajax.AjaxRequestTarget;
//import org.apache.wicket.markup.html.basic.Label;
//import org.apache.wicket.markup.html.form.TextField;
//import org.apache.wicket.model.PropertyModel;
//import org.jooq.DSLContext;
//import org.springframework.mail.MailAuthenticationException;
//import org.springframework.mail.MailSender;
//import org.springframework.mail.SimpleMailMessage;
//
///**
// * Created by socheat on 3/27/16.
// */
//@Mount("/oauth2/verify")
//public class VerifyPage extends AdminLTEPage {
//
//    private String applicationText;
//    private Label applicationLabel;
//
//    private String client;
//    private Label clientLabel;
//
//    private String applicationId;
//    private String clientId;
//    private String userId;
//    private String responseType;
//    private String redirectUri;
//    private String state;
//    private String scope;
//    private Integer verify;
//    private String type;
//    private String emailAddress;
//
//    private Integer otp;
//    private TextField<Integer> otpField;
//    private TextFeedbackPanel otpFeedback;
//
//    private Button okayButton;
//    private AjaxButton resendButton;
//
//    private Form<Void> form;
//
//    public VerifyPage(String applicationId, String clientId, String userId, String responseType, String redirectUri, String state, String scope, String emailAddress, String type, Integer verify) {
//        this.applicationId = applicationId;
//        this.clientId = clientId;
//        this.userId = userId;
//        this.responseType = responseType;
//        this.redirectUri = redirectUri;
//        this.state = state;
//        this.scope = scope;
//        this.verify = verify;
//        this.type = type;
//        this.emailAddress = emailAddress;
//    }
//
//    @Override
//    protected void onInitialize() {
//        super.onInitialize();
//
//        DSLContext context = getSession().getDSLContext();
//        ClientTable clientTable = Tables.CLIENT.as("clientTable");
//        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
//
//        ClientRecord clientRecord = context.select(clientTable.fields()).from(clientTable).where(clientTable.CLIENT_ID.eq(this.clientId)).fetchOneInto(clientTable);
//
//        ApplicationRecord applicationRecord = null;
//        if (clientRecord != null) {
//            this.client = clientRecord.getName();
//            applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(clientRecord.getApplicationId())).fetchOneInto(applicationTable);
//        }
//
//        if (applicationRecord != null) {
//            this.applicationId = applicationRecord.getApplicationId();
//            this.applicationText = applicationRecord.getName();
//        }
//
//        this.form = new Form<>("form");
//        this.form.setOutputMarkupId(true);
//        add(this.form);
//
//        this.applicationLabel = new Label("applicationLabel", new PropertyModel<>(this, "applicationText"));
//        this.form.add(this.applicationLabel);
//
//        this.clientLabel = new Label("clientLabel", new PropertyModel<>(this, "client"));
//        this.form.add(this.clientLabel);
//
//        this.otpField = new TextField<>("otpField", new PropertyModel<>(this, "otp"));
//        this.otpField.setRequired(true);
//        this.otpField.setLabel(JooqUtils.lookup("One Time Password", this));
//        this.form.add(this.otpField);
//        this.otpFeedback = new TextFeedbackPanel("otpFeedback", this.otpField);
//        this.form.add(this.otpFeedback);
//
//        this.resendButton = new AjaxButton("resendButton");
//        this.resendButton.setDefaultFormProcessing(false);
//        this.resendButton.setVisible(false);
//        this.resendButton.setOnSubmit(this::resendButtonOnSubmit);
//        this.form.add(this.resendButton);
//
//        this.okayButton = new Button("okayButton");
//        this.okayButton.setOnSubmit(this::okayButtonOnSubmit);
//        this.form.add(this.okayButton);
//    }
//
//    @Override
//    public Session getSession() {
//        return (Session) super.getSession();
//    }
//
//    private void resendButtonOnSubmit(AjaxButton self, AjaxRequestTarget target, org.apache.wicket.markup.html.form.Form<?> form) {
//        this.verify = Integer.valueOf(RandomStringUtils.randomNumeric(6));
//        MailSender mailSender = getSession().getMailSender();
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setSubject("One Time Password");
//        message.setText(String.valueOf(verify));
//        message.setTo(this.emailAddress);
//        try {
//            mailSender.send(message);
//        } catch (MailAuthenticationException e) {
//        }
//        this.otp = null;
//        otpField.clearInput();
//        target.add(form);
//    }
//
//    private void okayButtonOnSubmit(Button components) {
//        if (!this.otp.equals(this.verify)) {
//            this.otpField.error("incorrect");
//            this.resendButton.setVisible(true);
//        } else {
//            PermissionPage permissionPage = new PermissionPage(this.applicationId, this.clientId, this.userId, this.responseType, this.redirectUri, this.state, this.scope);
//            setResponsePage(permissionPage);
//        }
//    }
//}
