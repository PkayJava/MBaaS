package com.angkorteam.mbaas.server.spring;

import org.jooq.DSLContext;

/**
 * Created by socheat on 4/23/16.
 */
public abstract class JavascripTask implements Runnable {

    private final DSLContext context;

    private final String jobId;

    public JavascripTask(DSLContext context, String jobId) {
        this.jobId = jobId;
        this.context = context;
    }

    @Override
    public final void run() {
        doRun();
    }

    protected abstract void doRun();

}
