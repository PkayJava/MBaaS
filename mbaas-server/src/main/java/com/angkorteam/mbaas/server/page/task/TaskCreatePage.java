package com.angkorteam.mbaas.server.page.task;

import com.angkorteam.framework.extension.wicket.feedback.TextFeedbackPanel;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import org.apache.wicket.markup.html.form.TextField;

/**
 * Created by socheat on 4/24/16.
 */
public class TaskCreatePage extends MasterPage {

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private String javascript;
    private TextField<String> javascriptField;
    private TextFeedbackPanel javascriptFeedback;

    private String cron;
    private TextField<String> cronField;
    private TextFeedbackPanel cronFeedback;

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }
}
