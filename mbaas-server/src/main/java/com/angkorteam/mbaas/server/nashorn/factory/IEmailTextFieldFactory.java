package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornEmailTextField;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidator;

/**
 * Created by socheat on 6/11/16.
 */
public interface IEmailTextFieldFactory {

    NashornEmailTextField createEmailTextField(String id, IModel<String> model);

    NashornEmailTextField createEmailTextField(MarkupContainer container, String id, IModel<String> model);

    NashornEmailTextField createEmailTextField(String id, IModel<String> model, IValidator<String> emailValidator);

    NashornEmailTextField createEmailTextField(MarkupContainer container, String id, IModel<String> model, IValidator<String> emailValidator);

}
