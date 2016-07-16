package com.angkorteam.mbaas.server.page.socket;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.wicket.*;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.WebSocketPushBroadcaster;
import org.apache.wicket.protocol.ws.api.message.ConnectedMessage;
import org.apache.wicket.protocol.ws.api.registry.PageIdKey;
import org.apache.wicket.protocol.ws.api.registry.ResourceNameKey;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

/**
 * Created by socheat on 7/16/16.
 */
@AuthorizeInstantiation("administrator")
@Mount("/socket/push/message")
public class PushMessagePage extends MasterPage {

    private String socketId;

    private String message;
    private TextField<String> messageField;
    private TextFeedbackPanel messageFeedback;

    private Button sendButton;

    private Form<Void> form;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.socketId = getPageParameters().get("socketId").toString("");

        this.form = new Form<>("form");
        this.add(this.form);

        this.messageField = new TextField<>("messageField", new PropertyModel<>(this, "message"));
        this.messageField.setRequired(true);
        this.form.add(this.messageField);
        this.messageFeedback = new TextFeedbackPanel("messageFeedback", this.messageField);
        this.form.add(this.messageFeedback);

        this.sendButton = new Button("sendButton");
        this.sendButton.setOnSubmit(this::sendButtonOnSubmit);
        this.form.add(this.sendButton);
    }

    private void sendButtonOnSubmit(Button button) {
        Application application = ApplicationUtils.getApplication();
        JdbcTemplate jdbcTemplate = application.getJdbcTemplate(getSession().getApplicationCode());
        Map<String, Object> socket = jdbcTemplate.queryForMap("SELECT * FROM " + Jdbc.SOCKET + " WHERE " + Jdbc.Socket.SOCKET_ID + " = ?", this.socketId);
        WebSocketSettings webSocketSettings = WebSocketSettings.Holder.get(application);
        WebSocketPushBroadcaster broadcaster = new WebSocketPushBroadcaster(webSocketSettings.getConnectionRegistry());
        String sessionId = (String) socket.get(Jdbc.Socket.SESSION_ID);
        Integer pageKey = (Integer) socket.get(Jdbc.Socket.PAGE_KEY);
        if (pageKey != null) {
            PushMessage pushMessage = new PushMessage(this.message);
            ConnectedMessage connectedMessage = new ConnectedMessage(application, sessionId, new PageIdKey(pageKey));
            broadcaster.broadcast(connectedMessage, pushMessage);
        }
        String resourceName = (String) socket.get(Jdbc.Socket.RESOURCE_NAME);
        if (resourceName != null && !"".equals(resourceName)) {
            PushMessage pushMessage = new PushMessage(this.message);
            ConnectedMessage connectedMessage = new ConnectedMessage(application, sessionId, new ResourceNameKey(resourceName));
            broadcaster.broadcast(connectedMessage, pushMessage);
        }
    }
}
