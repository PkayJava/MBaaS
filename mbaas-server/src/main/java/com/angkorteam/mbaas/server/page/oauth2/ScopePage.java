package com.angkorteam.mbaas.server.page.oauth2;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.mbaas.server.wicket.Mount;

/**
 * Created by socheat on 3/30/16.
 */
@Mount("/oauth2/scope")
public class ScopePage extends AdminLTEPage {

    private String code;

    private String state;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        
    }
}
