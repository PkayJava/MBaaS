package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornUrlTextField;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.validation.validator.UrlValidator;

import java.io.Serializable;

/**
 * Created by socheat on 6/11/16.
 */
public interface IUrlTextFieldFactory extends Serializable {

    NashornUrlTextField createUrlTextField(String id);

    NashornUrlTextField createUrlTextField(MarkupContainer container, String id);

    NashornUrlTextField createUrlTextField(String id, UrlValidator validator);

    NashornUrlTextField createUrlTextField(MarkupContainer container, String id, UrlValidator validator);

}
