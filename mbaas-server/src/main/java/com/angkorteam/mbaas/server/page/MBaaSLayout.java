package com.angkorteam.mbaas.server.page;

import org.apache.wicket.markup.html.border.Border;

/**
 * Created by socheat on 11/3/16.
 */
public class MBaaSLayout extends Border implements UUIDLayout {

    public MBaaSLayout(String id) {
        super(id);
    }

    @Override
    public String getLayoutUUID() {
        return MBaaSLayout.class.getName();
    }
}
