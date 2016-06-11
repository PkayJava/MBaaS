package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornSelect2SingleChoice;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by socheat on 6/1/16.
 */
public interface ISelect2SingleChoiceFactory extends Serializable {

    NashornSelect2SingleChoice createSelect2SingleChoice(
            String id,
            IChoiceRenderer<Map<String, Object>> renderer);

    NashornSelect2SingleChoice createSelect2SingleChoice(
            MarkupContainer container,
            String id,
            IChoiceRenderer<Map<String, Object>> renderer);

}
