package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.protocol.ws.api.NashornWebSocketBehavior;
import org.apache.wicket.protocol.ws.api.registry.IKey;

import java.io.Serializable;

/**
 * Created by socheat on 7/2/16.
 */
public interface IWebSocketFactory extends Serializable {

    NashornWebSocketBehavior registerWebSocket();

    void sendMessage(String message);

    void sendMessage(String sessionId, IKey key, String message);

    void pushMessage(String message);

    void pushMessage(String sessionId, IKey key, String message);

}
