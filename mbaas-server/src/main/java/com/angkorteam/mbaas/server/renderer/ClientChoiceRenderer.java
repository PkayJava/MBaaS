package com.angkorteam.mbaas.server.renderer;

import com.angkorteam.mbaas.model.entity.tables.pojos.ClientPojo;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Created by socheat on 3/3/16.
 */
public class ClientChoiceRenderer implements IChoiceRenderer<ClientPojo> {

    @Override
    public Object getDisplayValue(ClientPojo object) {
        return object.getName();
    }

    @Override
    public String getIdValue(ClientPojo object, int index) {
        return object.getClientId();
    }

    @Override
    public ClientPojo getObject(String id, IModel<? extends List<? extends ClientPojo>> choices) {
        for (ClientPojo client : choices.getObject()) {
            if (client.getClientId().equals(id)) {
                return client;
            }
        }
        return null;
    }
}
