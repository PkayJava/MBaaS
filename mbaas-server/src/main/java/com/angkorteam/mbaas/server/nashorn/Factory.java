package com.angkorteam.mbaas.server.nashorn;

import com.angkorteam.framework.extension.wicket.markup.html.form.ColorTextField;
import com.angkorteam.framework.extension.wicket.markup.html.form.DateTextField;
import com.angkorteam.framework.extension.wicket.markup.html.form.TimeTextField;
import com.angkorteam.mbaas.server.nashorn.factory.*;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.*;
import com.angkorteam.mbaas.server.nashorn.wicket.provider.select2.NashornChoiceRenderer;
import com.angkorteam.mbaas.server.nashorn.wicket.provider.select2.NashornMultipleChoiceProvider;
import com.angkorteam.mbaas.server.nashorn.wicket.provider.select2.NashornSingleChoiceProvider;
import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornFormValidator;
import com.angkorteam.mbaas.server.nashorn.wicket.validation.NashornValidator;
import com.angkorteam.mbaas.server.page.flow.FlowPage;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.*;

/**
 * Created by socheat on 5/30/16.
 */
public class Factory implements Serializable,
        ITextFieldFactory,
        IFormFactory,
        IFormValidatorFactory,
        IButtonFactory,
        IWebMarkupContainerFactory,
        ILabelFactory,
        IPropertyModelFactory,
        IChoiceRendererFactory,
        ISelect2MultipleChoiceFactory,
        IMultipleChoiceProviderFactory,
        IDateTextFieldFactory,
        IDropDownChoiceFactory,
        IListMultipleChoiceFactory,
        IValidatorFactory,
        IColorTextFieldFactory,
        ITimeTextFieldFactory,
        IListChoiceFactory,
        IRadioChoiceFactory,
        ICheckBoxMultipleChoiceFactory,
        ISelect2SingleChoiceFactory,
        ICheckBoxFactory,
        ISingleChoiceProviderFactory {

    private FlowPage container;

    private Map<String, Object> children;

    private Map<String, Object> userModel;

    private String script;

    private String applicationCode;

    public Factory(FlowPage container, String applicationCode, String script, Map<String, Object> userModel) {
        this.container = container;
        this.userModel = userModel;
        this.script = script;
        this.applicationCode = applicationCode;
        this.children = new HashMap<>();
    }

    @Override
    public <T> NashornValidator<T> createValidator() {
        NashornValidator<T> object = new NashornValidator<>();
        object.setScript(this.script);
        return object;
    }

    @Override
    public NashornFormValidator createFormValidator() {
        NashornFormValidator object = new NashornFormValidator();
        object.setScript(this.script);
        object.setChildren(this.children);
        return object;
    }

    @Override
    public <T> PropertyModel<T> createPropertyModel(Object model, String expression) {
        PropertyModel<T> object = new PropertyModel<>(model, expression);
        return object;
    }

    @Override
    public NashornSingleChoiceProvider createSingleChoiceProvider() {
        NashornSingleChoiceProvider object = new NashornSingleChoiceProvider();
        object.setScript(this.script);
        return object;
    }

    @Override
    public NashornMultipleChoiceProvider createMultipleChoiceProvider() {
        NashornMultipleChoiceProvider object = new NashornMultipleChoiceProvider();
        object.setScript(this.script);
        return object;
    }

    @Override
    public NashornChoiceRenderer createChoiceRenderer(String id, String text) {
        NashornChoiceRenderer object = new NashornChoiceRenderer(id, text);
        return object;
    }

    @Override
    public Label createLabel(String id) {
        return createLabel(container, id);
    }

    @Override
    public Label createLabel(MarkupContainer container, String id) {
        Label object = new Label(id);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public WebMarkupContainer createWebMarkupContainer(String id) {
        return createWebMarkupContainer(container, id);
    }

    @Override
    public WebMarkupContainer createWebMarkupContainer(MarkupContainer container, String id) {
        WebMarkupContainer object = new WebMarkupContainer(id);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public <T> NashornForm<T> createForm(String id) {
        return createForm(container, id);
    }

    @Override
    public <T> NashornForm<T> createForm(MarkupContainer container, String id) {
        NashornForm<T> object = new NashornForm<>(id);
        object.setScript(this.script);
        object.setUserModel(this.userModel);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public <T> NashornTextField<T> createTextField(String id, IModel<T> model) {
        return createTextField(container, id, model);
    }

    @Override
    public <T> NashornTextField<T> createTextField(MarkupContainer container, String id, IModel<T> model) {
        NashornTextField<T> object = new NashornTextField<>(id, model);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public <T> NashornTextField<T> createTextField(String id, IModel<T> model, Class<T> type) {
        return createTextField(container, id, model, type);
    }

    @Override
    public <T> NashornTextField<T> createTextField(MarkupContainer container, String id, IModel<T> model, Class<T> type) {
        NashornTextField<T> object = new NashornTextField<>(id, model, type);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornButton createButton(String id) {
        return createButton(container, id);
    }

    @Override
    public NashornButton createButton(MarkupContainer container, String id) {
        NashornButton object = new NashornButton(id);
        object.setScript(this.script);
        object.setUserModel(this.userModel);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornSelect2MultipleChoice createSelect2MultipleChoice(String id, IModel<List<Map<String, Object>>> model, NashornMultipleChoiceProvider provider, NashornChoiceRenderer renderer) {
        return createSelect2MultipleChoice(container, id, model, provider, renderer);
    }

    @Override
    public NashornSelect2MultipleChoice createSelect2MultipleChoice(MarkupContainer container, String id, IModel<List<Map<String, Object>>> model, NashornMultipleChoiceProvider provider, NashornChoiceRenderer renderer) {
        NashornSelect2MultipleChoice object = new NashornSelect2MultipleChoice(id, model, provider, renderer);
        container.add(object);
        provider.setId(id);
        renderer.setId(id);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornSelect2SingleChoice createSelect2SingleChoice(String id, IModel<Map<String, Object>> model, NashornSingleChoiceProvider provider, NashornChoiceRenderer renderer) {
        return createSelect2SingleChoice(container, id, model, provider, renderer);
    }

    @Override
    public NashornSelect2SingleChoice createSelect2SingleChoice(MarkupContainer container, String id, IModel<Map<String, Object>> model, NashornSingleChoiceProvider provider, NashornChoiceRenderer renderer) {
        NashornSelect2SingleChoice object = new NashornSelect2SingleChoice(id, model, provider, renderer);
        container.add(object);
        renderer.setId(id);
        provider.setId(id);
        this.children.put(id, object);
        return object;
    }

    @Override
    public DateTextField createDateTextField(String id, IModel<Date> model) {
        return createDateTextField(container, id, model);
    }

    @Override
    public DateTextField createDateTextField(MarkupContainer container, String id, IModel<Date> model) {
        DateTextField object = new DateTextField(id, model);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public ColorTextField createColorTextField(String id, IModel<String> model) {
        return createColorTextField(container, id, model);
    }

    @Override
    public ColorTextField createColorTextField(MarkupContainer container, String id, IModel<String> model) {
        ColorTextField object = new ColorTextField(id, model);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public TimeTextField createTimeTextField(String id, IModel<String> model) {
        return createTimeTextField(container, id, model);
    }

    @Override
    public TimeTextField createTimeTextField(MarkupContainer container, String id, IModel<String> model) {
        TimeTextField object = new TimeTextField(id, model);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornDropDownChoice createDropDownChoice(String id, IModel<Map<String, Object>> model, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        return createDropDownChoice(container, id, model, choices, renderer);
    }

    @Override
    public NashornDropDownChoice createDropDownChoice(MarkupContainer container, String id, IModel<Map<String, Object>> model, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        NashornDropDownChoice object = new NashornDropDownChoice(id, model, choices, renderer);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornListMultipleChoice createListMultipleChoice(String id, IModel<Collection<Map<String, Object>>> model, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        return createListMultipleChoice(container, id, model, choices, renderer);
    }

    @Override
    public NashornListMultipleChoice createListMultipleChoice(MarkupContainer container, String id, IModel<Collection<Map<String, Object>>> model, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        NashornListMultipleChoice object = new NashornListMultipleChoice(id, model, choices, renderer);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornListMultipleChoice createListMultipleChoice(String id, IModel<Collection<Map<String, Object>>> model, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer, int maxRows) {
        return createListMultipleChoice(container, id, model, choices, renderer, maxRows);
    }

    @Override
    public NashornListMultipleChoice createListMultipleChoice(MarkupContainer container, String id, IModel<Collection<Map<String, Object>>> model, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer, int maxRows) {
        NashornListMultipleChoice object = new NashornListMultipleChoice(id, model, choices, renderer, maxRows);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornCheckBoxMultipleChoice createCheckBoxMultipleChoice(String id, IModel<Collection<Map<String, Object>>> model, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        return createCheckBoxMultipleChoice(container, id, model, choices, renderer);
    }

    @Override
    public NashornCheckBoxMultipleChoice createCheckBoxMultipleChoice(MarkupContainer container, String id, IModel<Collection<Map<String, Object>>> model, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        NashornCheckBoxMultipleChoice object = new NashornCheckBoxMultipleChoice(id, model, choices, renderer);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornListChoice createListChoice(String id, IModel<Map<String, Object>> model, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        return createListChoice(container, id, model, choices, renderer);
    }

    @Override
    public NashornListChoice createListChoice(MarkupContainer markupContainer, String id, IModel<Map<String, Object>> model, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        NashornListChoice object = new NashornListChoice(id, model, choices, renderer);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornListChoice createListChoice(String id, IModel<Map<String, Object>> model, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer, int maxRows) {
        return createListChoice(container, id, model, choices, renderer, maxRows);
    }

    @Override
    public NashornListChoice createListChoice(MarkupContainer markupContainer, String id, IModel<Map<String, Object>> model, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer, int maxRows) {
        NashornListChoice object = new NashornListChoice(id, model, choices, renderer, maxRows);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornRadioChoice createRadioChoice(String id, IModel<Map<String, Object>> model, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        return createRadioChoice(container, id, model, choices, renderer);
    }

    @Override
    public NashornRadioChoice createRadioChoice(MarkupContainer container, String id, IModel<Map<String, Object>> model, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        NashornRadioChoice object = new NashornRadioChoice(id, model, choices, renderer);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornCheckBox createCheckBox(String id, IModel<Boolean> model) {
        return createCheckBox(container, id, model);
    }

    @Override
    public NashornCheckBox createCheckBox(MarkupContainer container, String id, IModel<Boolean> model) {
        NashornCheckBox object = new NashornCheckBox(id, model);
        container.add(object);
        this.children.put(id, object);
        return object;
    }
}