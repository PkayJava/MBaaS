package com.angkorteam.mbaas.server.page.rest;

import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.server.wicket.MasterPage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;

/**
 * Created by socheat on 8/3/16.
 */
public class EnumCreatePage extends MasterPage {

    private String name;
    private TextField<String> nameField;
    private TextFeedbackPanel nameFeedback;

    private String type;
    private DropDownChoice<String> typeField;
    private TextFeedbackPanel typeFeedback;

    private String format;
    private TextField<String> formatField;
    private TextFeedbackPanel formatFeedback;

    private String description;
    private TextArea<String> descriptionField;
    private TextFeedbackPanel descriptionFeedback;

}
