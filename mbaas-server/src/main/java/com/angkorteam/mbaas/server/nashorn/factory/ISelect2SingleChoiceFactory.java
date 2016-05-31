package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2SingleChoice;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.select2.NashornChoiceRenderer;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.select2.NashornSingleChoiceProvider;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by socheat on 6/1/16.
 */
public interface ISelect2SingleChoiceFactory extends Serializable {

    Select2SingleChoice<Map<String, Object>> createSelect2SingleChoice(
            String id,
            IModel<Map<String, Object>> model,
            NashornSingleChoiceProvider provider,
            NashornChoiceRenderer renderer);

    Select2SingleChoice<Map<String, Object>> createSelect2SingleChoice(
            MarkupContainer container,
            String id,
            IModel<Map<String, Object>> model,
            NashornSingleChoiceProvider provider,
            NashornChoiceRenderer renderer);

}
