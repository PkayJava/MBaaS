package com.angkorteam.mbaas.web;

import com.angkorteam.mbaas.ApplicationContext;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.jooq.DSLContext;

/**
 * Application object for your web application.
 * If you want to run this application without deploying, run the Start class.
 */
public class Application extends WebApplication {
    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<? extends WebPage> getHomePage() {
        return HomePage.class;
    }

    /**
     * @see org.apache.wicket.Application#init()
     */
    @Override
    public void init() {
        super.init();
        ApplicationContext applicationContext = ApplicationContext.get(getServletContext());
    }

    public final DSLContext getDSLContext() {
        ApplicationContext applicationContext = ApplicationContext.get(getServletContext());
        return applicationContext.getDSLContext();
    }
}
