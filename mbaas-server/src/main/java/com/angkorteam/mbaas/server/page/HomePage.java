package com.angkorteam.mbaas.server.page;

import com.angkorteam.mbaas.server.wicket.Page;

/**
 * Created by socheat on 3/1/16.
 */
public class HomePage extends Page {

    @Override
    protected void onInitialize() {
        super.onInitialize();
        System.out.println("onInitialize");
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();
        System.out.println("onBeforeRender");
    }
}
