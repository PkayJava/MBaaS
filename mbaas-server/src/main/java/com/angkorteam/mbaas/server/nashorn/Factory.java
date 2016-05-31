package com.angkorteam.mbaas.server.nashorn;

import com.angkorteam.mbaas.server.nashorn.factory.*;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornButton;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.NashornForm;
import com.angkorteam.mbaas.server.page.flow.FlowPage;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 5/30/16.
 */
public class Factory implements Serializable,
        ITextFieldFactory,
        IFormFactory,
        IButtonFactory,
        IWebMarkupContainerFactory,
        IPropertyModelFactory,
        ILabelFactory {

    private FlowPage container;

    private Map<String, Object> children;

    private Map<String, Object> userModel;

    public Factory(FlowPage container, Map<String, Object> userModel) {
        this.container = container;
        this.userModel = userModel;
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
        object.setUserModel(this.userModel);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public <T> PropertyModel<T> createPropertyModel(Object model, String expression) {
        PropertyModel<T> object = new PropertyModel<>(model, expression);
        return object;
    }
}