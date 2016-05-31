package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.MultipleChoiceProvider;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2MultipleChoice;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 6/1/16.
 */
public interface ISelect2MultipleChoiceFactory extends Serializable {

    Select2MultipleChoice<Map<String, Object>> createSelect2MultipleChoice(
            String id,
            IModel<List<Map<String, Object>>> model,
            MultipleChoiceProvider<Map<String, Object>> provider,
            IChoiceRenderer<Map<String, Object>> renderer);

    Select2MultipleChoice<Map<String, Object>> createSelect2MultipleChoice(
            MarkupContainer container,
            String id,
            IModel<List<Map<String, Object>>> model,
            MultipleChoiceProvider<Map<String, Object>> provider,
            IChoiceRenderer<Map<String, Object>> renderer);

}
