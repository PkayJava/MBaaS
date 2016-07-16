package com.angkorteam.mbaas.server.nashorn.wicket.protocol.ws.api;

import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.nashorn.Disk;
import com.angkorteam.mbaas.server.nashorn.Factory;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.angkorteam.mbaas.server.wicket.PushMessage;
import com.angkorteam.mbaas.server.wicket.Session;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.*;
import org.apache.wicket.protocol.ws.api.registry.PageIdKey;
import org.apache.wicket.protocol.ws.api.registry.ResourceNameKey;
import org.apache.wicket.request.cycle.RequestCycle;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.util.ReflectionUtils;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by socheat on 7/2/16.
 */
public class NashornWebSocketBehavior extends WebSocketBehavior {

    private String script;

    private Factory factory;

    private Disk disk;

    private Map<String, Object> pageModel;

    private String applicationCode;

    public NashornWebSocketBehavior(Factory factory, Disk disk, String script, Map<String, Object> pageModel, String applicationCode) {
        this.factory = factory;
        this.pageModel = pageModel;
        this.disk = disk;
        this.script = script;
        this.applicationCode = applicationCode;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        {
            StringBuilder body = new StringBuilder("");
            String scriptOnMessage = "Wicket.Event.subscribe('/websocket/message', function(event, message){ " + (body == null || "undefined".equals(body.toString()) ? "" : body.toString()) + " });";
            response.render(OnDomReadyHeaderItem.forScript(scriptOnMessage));
        }
        {
            StringBuilder body = new StringBuilder("");
            String scriptOnOpen = "Wicket.Event.subscribe('/websocket/open', function(event){" + (body == null || "undefined".equals(body.toString()) ? "" : body.toString()) + "});";
            response.render(OnDomReadyHeaderItem.forScript(scriptOnOpen));
        }
        {
            StringBuilder body = new StringBuilder("");
            String scriptOnClosed = "Wicket.Event.subscribe('/websocket/closed', function(event){" + (body == null || "undefined".equals(body.toString()) ? "" : body.toString()) + "});";
            response.render(OnDomReadyHeaderItem.forScript(scriptOnClosed));
        }
        {
            StringBuilder body = new StringBuilder("");
            String scriptOnError = "Wicket.Event.subscribe('/websocket/error', function(event){" + (body == null || "undefined".equals(body.toString()) ? "" : body.toString()) + "});";
            response.render(OnDomReadyHeaderItem.forScript(scriptOnError));
        }
    }

    @Override
    protected void onConnect(ConnectedMessage message) {
        ScriptEngine engine = ApplicationUtils.getApplication().getScriptEngine();
        try {
            engine.eval(this.script);
        } catch (ScriptException e) {
            throw new WicketRuntimeException(e);
        }
        Invocable invocable = (Invocable) engine;
        ISocketConnect connect = invocable.getInterface(ISocketConnect.class);
        if (connect == null) {
            throw new WicketRuntimeException("function socket_on_connect(requestCycle, disk, jdbcTemplate, factory, pageModel, connectedMessage){} is missing");
        }
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        Map<String, Object> fields = new HashMap<>();
        Session session = (Session) Session.get();
        fields.put(Jdbc.Socket.USER_ID, session.getApplicationUserId());
        fields.put(Jdbc.Socket.SOCKET_ID, UUID.randomUUID().toString());
        fields.put(Jdbc.Socket.SESSION_ID, message.getSessionId());
        fields.put(Jdbc.Socket.DATE_CREATED, new Date());
        if (message.getKey() instanceof PageIdKey) {
            Field field = ReflectionUtils.findField(PageIdKey.class, "pageId", Integer.class);
            field.setAccessible(true);
            Integer pageKey = (Integer) ReflectionUtils.getField(field, message.getKey());
            fields.put(Jdbc.Socket.PAGE_KEY, pageKey);
            jdbcInsert.usingColumns(Jdbc.Socket.SOCKET_ID, Jdbc.Socket.DATE_CREATED, Jdbc.Socket.USER_ID, Jdbc.Socket.SESSION_ID, Jdbc.Socket.PAGE_KEY);
        }
        if (message.getKey() instanceof ResourceNameKey) {
            Field field = ReflectionUtils.findField(PageIdKey.class, "resourceName", String.class);
            field.setAccessible(true);
            String resourceName = (String) ReflectionUtils.getField(field, message.getKey());
            fields.put(Jdbc.Socket.RESOURCE_NAME, resourceName);
            jdbcInsert.usingColumns(Jdbc.Socket.SOCKET_ID, Jdbc.Socket.DATE_CREATED, Jdbc.Socket.USER_ID, Jdbc.Socket.SESSION_ID, Jdbc.Socket.RESOURCE_NAME);
        }
        jdbcInsert.withTableName(Jdbc.SOCKET);
        jdbcInsert.execute(fields);
        connect.socket_on_connect(RequestCycle.get(), this.disk, jdbcTemplate, this.factory, this.pageModel, message);
    }

    @Override
    protected void onClose(ClosedMessage message) {
        ScriptEngine engine = ApplicationUtils.getApplication().getScriptEngine();
        try {
            engine.eval(this.script);
        } catch (ScriptException e) {
            throw new WicketRuntimeException(e);
        }
        Invocable invocable = (Invocable) engine;
        ISocketClose close = invocable.getInterface(ISocketClose.class);
        if (close == null) {
            throw new WicketRuntimeException("function socket_on_close(requestCycle, disk, jdbcTemplate, factory, pageModel, closedMessage){} is missing");
        }
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
        close.socket_on_close(RequestCycle.get(), this.disk, jdbcTemplate, this.factory, this.pageModel, message);
        if (message.getKey() instanceof PageIdKey) {
            Field field = ReflectionUtils.findField(PageIdKey.class, "pageId", Integer.class);
            field.setAccessible(true);
            Integer pageKey = (Integer) ReflectionUtils.getField(field, message.getKey());
            String sessionId = message.getSessionId();
            jdbcTemplate.update("DELETE FROM " + Jdbc.SOCKET + " WHERE " + Jdbc.Socket.SESSION_ID + " = ? AND " + Jdbc.Socket.PAGE_KEY + " = ?", sessionId, pageKey);
        }
        if (message.getKey() instanceof ResourceNameKey) {
            Field field = ReflectionUtils.findField(PageIdKey.class, "resourceName", String.class);
            field.setAccessible(true);
            String resourceName = (String) ReflectionUtils.getField(field, message.getKey());
            String sessionId = message.getSessionId();
            jdbcTemplate.update("DELETE FROM " + Jdbc.SOCKET + " WHERE " + Jdbc.Socket.SESSION_ID + " = ? AND " + Jdbc.Socket.RESOURCE_NAME + " = ?", sessionId, resourceName);
        }
    }

    @Override
    protected void onError(WebSocketRequestHandler handler, ErrorMessage message) {
        ScriptEngine engine = ApplicationUtils.getApplication().getScriptEngine();
        try {
            engine.eval(this.script);
        } catch (ScriptException e) {
            throw new WicketRuntimeException(e);
        }
        Invocable invocable = (Invocable) engine;
        ISocketError error = invocable.getInterface(ISocketError.class);
        if (error == null) {
            throw new WicketRuntimeException("function socket_on_error(requestCycle, disk, jdbcTemplate, factory, pageModel, handler, errorMessage){} is missing");
        }
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
        error.socket_on_error(RequestCycle.get(), this.disk, jdbcTemplate, this.factory, this.pageModel, handler, message);
    }

    @Override
    protected void onAbort(AbortedMessage message) {
        ScriptEngine engine = ApplicationUtils.getApplication().getScriptEngine();
        try {
            engine.eval(this.script);
        } catch (ScriptException e) {
            throw new WicketRuntimeException(e);
        }
        Invocable invocable = (Invocable) engine;
        ISocketAbort abort = invocable.getInterface(ISocketAbort.class);
        if (abort == null) {
            throw new WicketRuntimeException("function socket_on_abort(requestCycle, disk, jdbcTemplate, factory, pageModel, abortedMessage){} is missing");
        }
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
        abort.socket_on_abort(RequestCycle.get(), this.disk, jdbcTemplate, this.factory, this.pageModel, message);
    }

    @Override
    protected void onMessage(WebSocketRequestHandler handler, TextMessage message) {
        ScriptEngine engine = ApplicationUtils.getApplication().getScriptEngine();
        try {
            engine.eval(this.script);
        } catch (ScriptException e) {
            throw new WicketRuntimeException(e);
        }
        Invocable invocable = (Invocable) engine;
        ISocketTextMessage textMessage = invocable.getInterface(ISocketTextMessage.class);
        if (textMessage == null) {
            throw new WicketRuntimeException("function socket_on_text_message(requestCycle, disk, jdbcTemplate, factory, pageModel, handler, textMessage){} is missing");
        }
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
        textMessage.socket_on_text_message(RequestCycle.get(), this.disk, jdbcTemplate, this.factory, this.pageModel, handler, message);
    }

    @Override
    protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
        ScriptEngine engine = ApplicationUtils.getApplication().getScriptEngine();
        try {
            engine.eval(this.script);
        } catch (ScriptException e) {
            throw new WicketRuntimeException(e);
        }
        Invocable invocable = (Invocable) engine;
        ISocketPush push = invocable.getInterface(ISocketPush.class);
        if (push == null) {
            throw new WicketRuntimeException("function socket_on_push(requestCycle, disk, jdbcTemplate, factory, pageModel, handler, pushMessage){} is missing");
        }
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
        push.socket_on_push(RequestCycle.get(), this.disk, jdbcTemplate, this.factory, this.pageModel, handler, (PushMessage) message);
    }

    @Override
    protected void onMessage(WebSocketRequestHandler handler, BinaryMessage message) {
        ScriptEngine engine = ApplicationUtils.getApplication().getScriptEngine();
        try {
            engine.eval(this.script);
        } catch (ScriptException e) {
            throw new WicketRuntimeException(e);
        }
        Invocable invocable = (Invocable) engine;
        ISocketBinaryMessage binaryMessage = invocable.getInterface(ISocketBinaryMessage.class);
        if (binaryMessage == null) {
            throw new WicketRuntimeException("function socket_on_binary_message(requestCycle, disk, jdbcTemplate, factory, pageModel, handler, binaryMessage){} is missing");
        }
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
        binaryMessage.socket_on_binary_message(RequestCycle.get(), this.disk, jdbcTemplate, this.factory, this.pageModel, handler, message);
    }

    public interface ISocketPush extends Serializable {
        void socket_on_push(RequestCycle requestCycle, Disk disk, JdbcTemplate jdbcTemplate, Factory factory, Map<String, Object> pageModel, WebSocketRequestHandler handler, PushMessage message);
    }

    public interface ISocketConnect extends Serializable {
        void socket_on_connect(RequestCycle requestCycle, Disk disk, JdbcTemplate jdbcTemplate, Factory factory, Map<String, Object> pageModel, ConnectedMessage message);
    }

    public interface ISocketClose extends Serializable {
        void socket_on_close(RequestCycle requestCycle, Disk disk, JdbcTemplate jdbcTemplate, Factory factory, Map<String, Object> pageModel, ClosedMessage message);
    }

    public interface ISocketError extends Serializable {
        void socket_on_error(RequestCycle requestCycle, Disk disk, JdbcTemplate jdbcTemplate, Factory factory, Map<String, Object> pageModel, WebSocketRequestHandler handler, ErrorMessage message);
    }

    public interface ISocketAbort extends Serializable {
        void socket_on_abort(RequestCycle requestCycle, Disk disk, JdbcTemplate jdbcTemplate, Factory factory, Map<String, Object> pageModel, AbortedMessage message);
    }

    public interface ISocketTextMessage extends Serializable {
        void socket_on_text_message(RequestCycle requestCycle, Disk disk, JdbcTemplate jdbcTemplate, Factory factory, Map<String, Object> pageModel, WebSocketRequestHandler handler, TextMessage message);
    }

    public interface ISocketBinaryMessage extends Serializable {
        void socket_on_binary_message(RequestCycle requestCycle, Disk disk, JdbcTemplate jdbcTemplate, Factory factory, Map<String, Object> pageModel, WebSocketRequestHandler handler, BinaryMessage binaryMessage);
    }

}
