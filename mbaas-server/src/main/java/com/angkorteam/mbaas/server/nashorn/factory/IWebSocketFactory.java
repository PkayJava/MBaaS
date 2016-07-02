package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.protocol.ws.api.NashornWebSocketBehavior;
import org.apache.wicket.protocol.ws.api.registry.IKey;

/**
 * Created by socheat on 7/2/16.
 */
public interface IWebSocketFactory {

    public NashornWebSocketBehavior registerWebSocket();

    void pushMessage(String message);

    void pushMessage(String sessionId, String message);

    void pushMessage(String sessionId, IKey key, String message);

}
