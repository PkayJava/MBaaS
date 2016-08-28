package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornSelect2MultipleChoice;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by socheat on 6/1/16.
 */
public interface ISelect2MultipleChoiceFactory extends Serializable {

    NashornSelect2MultipleChoice createSelect2MultipleChoice(
            String id,
            String idField,
            String displayField);

    NashornSelect2MultipleChoice createSelect2MultipleChoice(
            MarkupContainer container,
            String id,
            String idField,
            String displayField);

}
