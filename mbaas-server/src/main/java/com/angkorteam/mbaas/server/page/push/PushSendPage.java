//package com.angkorteam.mbaas.server.page.push;
//
//import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
//import com.angkorteam.framework.extension.wicket.html.form.Form;
//import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
//import com.angkorteam.mbaas.model.entity.Tables;
//import com.angkorteam.mbaas.model.entity.tables.ApplicationTable;
//import com.angkorteam.mbaas.model.entity.tables.records.ApplicationRecord;
//import com.angkorteam.mbaas.server.service.MessageDTORequest;
//import com.angkorteam.mbaas.server.service.MessageDTOResponse;
//import com.angkorteam.mbaas.server.service.PusherClient;
//import com.angkorteam.mbaas.server.validator.UserDataValidator;
//import com.angkorteam.mbaas.server.wicket.MasterPage;
//import com.angkorteam.mbaas.server.wicket.Mount;
//import com.google.gson.Gson;
//import org.apache.commons.codec.binary.Base64;
//import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
//import org.apache.wicket.markup.html.form.TextArea;
//import org.apache.wicket.markup.html.form.TextField;
//import org.apache.wicket.model.PropertyModel;
//import org.jooq.DSLContext;
//import retrofit2.Call;
//import retrofit2.Response;
//
//import java.io.IOException;
//import java.util.Map;
//
///**
// * Created by socheat on 4/10/16.
// */
//@AuthorizeInstantiation({"administrator", "backoffice"})
//@Mount("/push/send")
//public class PushSendPage extends MasterPage {
//
//    private String applicationId;
//
//    private String alert;
//    private TextField<String> alertField;
//    private TextFeedbackPanel alertFeedback;
//
//    private String userData;
//    private TextArea<String> userDataField;
//    private TextFeedbackPanel userDataFeedback;
//
//    private Form<Void> form;
//    private Button sendButton;
//
//    @Override
//    public String getPageHeader() {
//        return "Push Send";
//    }
//
//    @Override
//    protected void onInitialize() {
//        super.onInitialize();
//
//        this.applicationId = getPageParameters().get("applicationId").toString();
//
//        this.form = new Form<>("form");
//        add(this.form);
//
//        this.alertField = new TextField<>("alertField", new PropertyModel<>(this, "alert"));
//        this.alertField.setRequired(true);
//        this.form.add(this.alertField);
//        this.alertFeedback = new TextFeedbackPanel("alertFeedback", this.alertField);
//        this.form.add(this.alertFeedback);
//
//        this.userDataField = new TextArea<>("userDataField", new PropertyModel<>(this, "userData"));
//        this.userDataField.add(new UserDataValidator());
//        this.form.add(this.userDataField);
//        this.userDataFeedback = new TextFeedbackPanel("userDataFeedback", this.userDataField);
//        this.form.add(this.userDataFeedback);
//
//        this.sendButton = new Button("sendButton");
//        this.sendButton.setOnSubmit(this::sendButtonOnSubmit);
//        this.form.add(this.sendButton);
//    }
//
//    private void sendButtonOnSubmit(Button button) {
//        DSLContext context = getDSLContext();
//
//        ApplicationTable applicationTable = Tables.APPLICATION.as("applicationTable");
//        ApplicationRecord applicationRecord = context.select(applicationTable.fields()).from(applicationTable).where(applicationTable.APPLICATION_ID.eq(this.applicationId)).fetchOneInto(applicationTable);
//
//        String authorization = "Basic " + Base64.encodeBase64String((applicationRecord.getPushApplicationId() + ":" + applicationRecord.getPushMasterSecret()).getBytes());
//        PusherClient pusherClient = getPusherClient();
//
//        Gson gson = new Gson();
//        Map<String, Object> userData = null;
//        if (this.userData != null && !"".equals(this.userData)) {
//            userData = gson.fromJson(this.userData, Map.class);
//        }
//
//        MessageDTORequest request = new MessageDTORequest();
//        request.getMessage().setAlert(this.alert);
//        if (userData != null) {
//            for (Map.Entry<String, Object> item : userData.entrySet()) {
//                request.getMessage().getUserData().put(item.getKey(), item.getValue());
//            }
//        }
//        Call<MessageDTOResponse> responseCall = pusherClient.send(authorization, request);
//        try {
//            Response<MessageDTOResponse> response = responseCall.execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
