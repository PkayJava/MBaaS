package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2SingleChoice;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.SingleChoiceProvider;
import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornValidator;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.ValidatorAdapter;

import java.util.Map;

/**
 * Created by socheat on 6/1/16.
 */
public class NashornSelect2SingleChoice extends Select2SingleChoice<Map<String, Object>> {

    private String script;

    public NashornSelect2SingleChoice(String id, IModel<Map<String, Object>> model, SingleChoiceProvider<Map<String, Object>> provider, IChoiceRenderer<Map<String, Object>> renderer) {
        super(id, model, provider, renderer);
    }

    @Override
    public Component add(Behavior... behaviors) {
        for (Behavior behavior : behaviors) {
            if (behavior instanceof ValidatorAdapter) {
                if (((ValidatorAdapter) behavior).getValidator() instanceof NashornValidator) {
                    throw new WicketRuntimeException("use registerValidator");
                }
            }
        }
        return super.add(behaviors);
    }

    public void registerValidator(String event) {
        NashornValidator validator = new NashornValidator(getId(), event, this.script);
        super.add(validator);
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

}
