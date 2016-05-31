package com.angkorteam.mbaas.server.nashorn;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.MultipleChoiceProvider;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2MultipleChoice;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Select2SingleChoice;
import com.angkorteam.mbaas.server.nashorn.factory.*;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornButton;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornForm;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.select2.NashornChoiceRenderer;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.select2.NashornMultipleChoiceProvider;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.select2.NashornSingleChoiceProvider;
import com.angkorteam.mbaas.server.page.flow.FlowPage;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 5/30/16.
 */
public class Factory implements Serializable,
        ITextFieldFactory,
        IFormFactory,
        IButtonFactory,
        IWebMarkupContainerFactory,
        ILabelFactory,
        IPropertyModelFactory,
        IChoiceRendererFactory,
        ISelect2MultipleChoiceFactory,
        IMultipleChoiceProviderFactory,
        ISelect2SingleChoiceFactory,
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
    public Label createLabel(String id, Serializable model) {
        return createLabel(container, id, model);
    }

    @Override
    public Label createLabel(MarkupContainer container, String id, Serializable model) {
        Label object = new Label(id, model);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public Label createLabel(String id, IModel<?> model) {
        return createLabel(container, id, model);
    }

    @Override
    public Label createLabel(MarkupContainer container, String id, IModel<?> model) {
        Label object = new Label(id, model);
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
    public WebMarkupContainer createWebMarkupContainer(String id, IModel<?> model) {
        return createWebMarkupContainer(container, id, model);
    }

    @Override
    public WebMarkupContainer createWebMarkupContainer(MarkupContainer container, String id, IModel<?> model) {
        WebMarkupContainer object = new WebMarkupContainer(id, model);
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
    public <T> NashornForm<T> createForm(String id, IModel<T> model) {
        return createForm(container, id, model);
    }

    @Override
    public <T> NashornForm<T> createForm(MarkupContainer container, String id, IModel<T> model) {
        NashornForm<T> object = new NashornForm<>(id, model);
        object.setUserModel(this.userModel);
        object.setScript(this.script);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public <T> TextField<T> createTextField(String id) {
        return createTextField(container, id);
    }

    @Override
    public <T> TextField<T> createTextField(MarkupContainer container, String id) {
        TextField<T> object = new TextField<>(id);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public <T> TextField<T> createTextField(String id, Class<T> type) {
        return createTextField(container, id, type);
    }

    @Override
    public <T> TextField<T> createTextField(MarkupContainer container, String id, Class<T> type) {
        TextField<T> object = new TextField<>(id, type);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public <T> TextField<T> createTextField(String id, IModel<T> model) {
        return createTextField(container, id, model);
    }

    @Override
    public <T> TextField<T> createTextField(MarkupContainer container, String id, IModel<T> model) {
        TextField<T> object = new TextField<>(id, model);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public <T> TextField<T> createTextField(String id, IModel<T> model, Class<T> type) {
        return createTextField(container, id, model, type);
    }

    @Override
    public <T> TextField<T> createTextField(MarkupContainer container, String id, IModel<T> model, Class<T> type) {
        TextField<T> object = new TextField<>(id, model, type);
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
    public NashornButton createButton(String id, IModel<String> model) {
        return createButton(container, id, model);
    }

    @Override
    public NashornButton createButton(MarkupContainer container, String id, IModel<String> model) {
        NashornButton object = new NashornButton(id, model);
        object.setScript(this.script);
        object.setUserModel(this.userModel);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public Select2MultipleChoice<Map<String, Object>> createSelect2MultipleChoice(String id, IModel<List<Map<String, Object>>> model, MultipleChoiceProvider<Map<String, Object>> provider, IChoiceRenderer<Map<String, Object>> renderer) {
        return createSelect2MultipleChoice(container, id, model, provider, renderer);
    }

    @Override
    public Select2MultipleChoice<Map<String, Object>> createSelect2MultipleChoice(MarkupContainer container, String id, IModel<List<Map<String, Object>>> model, MultipleChoiceProvider<Map<String, Object>> provider, IChoiceRenderer<Map<String, Object>> renderer) {
        Select2MultipleChoice<Map<String, Object>> object = new Select2MultipleChoice<>(id, model, provider, renderer);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public Select2SingleChoice<Map<String, Object>> createSelect2SingleChoice(String id, IModel<Map<String, Object>> model, NashornSingleChoiceProvider provider, NashornChoiceRenderer renderer) {
        return createSelect2SingleChoice(container, id, model, provider, renderer);
    }

    @Override
    public Select2SingleChoice<Map<String, Object>> createSelect2SingleChoice(MarkupContainer container, String id, IModel<Map<String, Object>> model, NashornSingleChoiceProvider provider, NashornChoiceRenderer renderer) {
        Select2SingleChoice<Map<String, Object>> object = new Select2SingleChoice<>(id, model, provider, renderer);
        container.add(object);
        renderer.setId(id);
        provider.setId(id);
        this.children.put(id, object);
        return object;
    }

    @Override
    public <T> PropertyModel<T> createPropertyModel(Object model, String expression) {
        PropertyModel<T> object = new PropertyModel<>(model, expression);
        return object;
    }

    @Override
    public NashornSingleChoiceProvider createSingleChoiceProvider() {
        NashornSingleChoiceProvider object = new NashornSingleChoiceProvider(this.applicationCode);
        object.setScript(this.script);
        return object;
    }

    @Override
    public NashornMultipleChoiceProvider createMultipleChoiceProvider() {
        NashornMultipleChoiceProvider object = new NashornMultipleChoiceProvider(this.applicationCode);
        return object;
    }

    @Override
    public NashornChoiceRenderer createChoiceRenderer(String id, String text) {
        NashornChoiceRenderer object = new NashornChoiceRenderer(id, text);
        return object;
    }
}