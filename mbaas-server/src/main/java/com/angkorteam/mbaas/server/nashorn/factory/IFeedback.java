package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;

import java.io.Serializable;

/**
 * Created by socheat on 6/18/16.
 */
public interface IFeedback extends Serializable {

    TextFeedbackPanel createFeedback(String id, FormComponent<?> component);

    TextFeedbackPanel createFeedback(MarkupContainer container, String id, FormComponent<?> component);

}
