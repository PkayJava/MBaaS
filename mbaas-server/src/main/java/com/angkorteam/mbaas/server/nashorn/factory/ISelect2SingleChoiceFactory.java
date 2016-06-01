package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornSelect2SingleChoice;
import com.angkorteam.mbaas.server.nashorn.wicket.provider.select2.NashornChoiceRenderer;
import com.angkorteam.mbaas.server.nashorn.wicket.provider.select2.NashornSingleChoiceProvider;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by socheat on 6/1/16.
 */
public interface ISelect2SingleChoiceFactory extends Serializable {

    NashornSelect2SingleChoice createSelect2SingleChoice(
            String id,
            IModel<Map<String, Object>> model,
            NashornSingleChoiceProvider provider,
            NashornChoiceRenderer renderer);

    NashornSelect2SingleChoice createSelect2SingleChoice(
            MarkupContainer container,
            String id,
            IModel<Map<String, Object>> model,
            NashornSingleChoiceProvider provider,
            NashornChoiceRenderer renderer);

}
