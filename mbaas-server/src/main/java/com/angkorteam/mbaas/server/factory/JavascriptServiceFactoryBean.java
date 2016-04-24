package com.angkorteam.mbaas.server.factory;

import com.angkorteam.mbaas.server.spring.JavascriptTrigger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.TaskScheduler;

/**
 * Created by socheat on 4/23/16.
 */
public class JavascriptServiceFactoryBean implements InitializingBean {

    private TaskScheduler scheduler;

    public TaskScheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("Test");
            }
        }, new JavascriptTrigger("* * * * * *"));
    }

}
