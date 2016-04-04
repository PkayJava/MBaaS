package com.angkorteam.mbaas.server.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;

public class SessionDestroyedListener implements ApplicationListener<HttpSessionDestroyedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionDestroyedListener.class);

    @Override
    public void onApplicationEvent(HttpSessionDestroyedEvent httpSessionDestroyedEvent) {
        LOGGER.info("session {} is revoke" + httpSessionDestroyedEvent.getId());
    }

}