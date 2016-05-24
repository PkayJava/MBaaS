package com.angkorteam.mbaas.server.spring;

import org.springframework.context.ApplicationListener;
import org.springframework.security.web.session.HttpSessionCreatedEvent;

public class SessionCreatedListener implements ApplicationListener<HttpSessionCreatedEvent> {

    @Override
    public void onApplicationEvent(HttpSessionCreatedEvent httpSessionCreatedEvent) {
    }

}