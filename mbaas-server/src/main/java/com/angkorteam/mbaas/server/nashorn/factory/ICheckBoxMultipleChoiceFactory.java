package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornCheckBoxMultipleChoice;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 6/1/16.
 * <span wicket:id="checkbox"></span>
 */
public interface ICheckBoxMultipleChoiceFactory extends Serializable {

    NashornCheckBoxMultipleChoice createCheckBoxMultipleChoice(String id, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer);

    NashornCheckBoxMultipleChoice createCheckBoxMultipleChoice(MarkupContainer container, String id, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer);
}
