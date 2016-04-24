package com.angkorteam.mbaas.server.spring;

import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;
import java.util.TimeZone;

/**
 * Created by socheat on 4/23/16.
 */
public class JavascriptTrigger extends CronTrigger {

    private int index = 0;

    public JavascriptTrigger(String expression) {
        super(expression);
    }

    public JavascriptTrigger(String expression, TimeZone timeZone) {
        super(expression, timeZone);
    }

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        if (index >= 3) {
            return null;
        }
        index++;
        return super.nextExecutionTime(triggerContext);
    }
}
