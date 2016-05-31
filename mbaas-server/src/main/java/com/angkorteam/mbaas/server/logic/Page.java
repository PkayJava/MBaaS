package com.angkorteam.mbaas.server.logic;

import com.angkorteam.framework.extension.wicket.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
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
public class Page implements Serializable,
        ITextFieldFactory,
        IFormFactory,
        IButtonFactory,
        IWebMarkupContainerFactory,
        IPropertyModelFactory,
        ILabelFactory {

    private final FlowPage container;

    private final Map<String, Object> children;

    public Page(FlowPage container) {
        this.container = container;
        this.children = new HashMap<>();
    }

    @Override
    public Label createLabel(MarkupContainer container, String id) {
        Label object = new Label(id);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public Label createLabel(MarkupContainer container, String id, Serializable model) {
        Label object = new Label(id, model);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public Label createLabel(MarkupContainer container, String id, IModel<?> model) {
        Label object = new Label(id, model);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public Label createLabel(String id) {
        Label object = new Label(id);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public Label createLabel(String id, Serializable model) {
        Label object = new Label(id, model);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public Label createLabel(String id, IModel<?> model) {
        Label object = new Label(id, model);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public WebMarkupContainer createWebMarkupContainer(String id) {
        WebMarkupContainer object = new WebMarkupContainer(id);
        container.add(object);
        this.children.put(id, object);
        return object;
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
        WebMarkupContainer object = new WebMarkupContainer(id, model);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public WebMarkupContainer createWebMarkupContainer(MarkupContainer container, String id, IModel<?> model) {
        WebMarkupContainer object = new WebMarkupContainer(id, model);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public <T> Form<T> createForm(String id) {
        Form<T> object = new Form<>(id);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public <T> Form<T> createForm(MarkupContainer container, String id) {
        Form<T> object = new Form<>(id);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public <T> Form<T> createForm(String id, IModel<T> model) {
        Form<T> object = new Form<>(id, model);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public <T> Form<T> createForm(MarkupContainer container, String id, IModel<T> model) {
        Form<T> object = new Form<>(id, model);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public <T> TextField<T> createTextField(String id) {
        TextField<T> object = new TextField<>(id);
        container.add(object);
        this.children.put(id, object);
        return object;
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
        TextField<T> object = new TextField<>(id, type);
        container.add(object);
        this.children.put(id, object);
        return object;
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
        TextField<T> object = new TextField<>(id, model);
        container.add(object);
        this.children.put(id, object);
        return object;
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
        TextField<T> object = new TextField<>(id, model, type);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public <T> TextField<T> createTextField(MarkupContainer container, String id, IModel<T> model, Class<T> type) {
        TextField<T> object = new TextField<>(id, model, type);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public Button createButton(String id) {
        Button object = new Button(id);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public Button createButton(String id, IModel<String> model) {
        Button object = new Button(id, model);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public Button createButton(MarkupContainer container, String id) {
        Button object = new Button(id);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public Button createButton(MarkupContainer container, String id, IModel<String> model) {
        Button object = new Button(id, model);
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