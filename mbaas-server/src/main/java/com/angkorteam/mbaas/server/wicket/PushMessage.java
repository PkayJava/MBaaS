//package com.angkorteam.mbaas.server.wicket;
//
//import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
//import org.apache.wicket.util.lang.Args;
//
//import java.io.Serializable;
//
///**
// * Created by socheat on 7/15/16.
// */
//public class PushMessage implements IWebSocketPushMessage, Serializable {
//
//    private final CharSequence text;
//
//    public PushMessage(final CharSequence text) {
//        this.text = Args.notEmpty(text, "text");
//    }
//
//    public final String getText() {
//        return text.toString();
//    }
//
//}
