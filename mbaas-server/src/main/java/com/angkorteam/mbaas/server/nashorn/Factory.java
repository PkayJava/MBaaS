package com.angkorteam.mbaas.server.nashorn;

import com.angkorteam.framework.extension.spring.SimpleJdbcUpdate;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.tabs.TabbedPanel;
import com.angkorteam.framework.extension.wicket.markup.html.FullCalendar;
import com.angkorteam.framework.extension.wicket.markup.html.FullCalendarItem;
import com.angkorteam.framework.extension.wicket.markup.html.form.CKEditorTextArea;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.server.Jdbc;
import com.angkorteam.mbaas.server.nashorn.factory.*;
import com.angkorteam.mbaas.server.nashorn.wicket.ajax.markup.html.NashornAjaxLink;
import com.angkorteam.mbaas.server.nashorn.wicket.ajax.markup.html.form.NashornAjaxButton;
import com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.repeater.data.table.*;
import com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.repeater.data.table.filter.NashornFilterForm;
import com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.tabs.NashornTab;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.basic.NashornLabel;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.*;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.upload.NashornFileUpload;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.upload.NashornMultiFileUpload;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.image.NashornImage;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.link.NashornLink;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.panel.BlockPanel;
import com.angkorteam.mbaas.server.nashorn.wicket.protocol.ws.api.NashornWebSocketBehavior;
import com.angkorteam.mbaas.server.nashorn.wicket.provider.FilterStateLocator;
import com.angkorteam.mbaas.server.nashorn.wicket.provider.NashornFullCalendarProvider;
import com.angkorteam.mbaas.server.nashorn.wicket.provider.NashornTableProvider;
import com.angkorteam.mbaas.server.nashorn.wicket.provider.select2.NashornChoiceRenderer;
import com.angkorteam.mbaas.server.nashorn.wicket.provider.select2.NashornMultipleChoiceProvider;
import com.angkorteam.mbaas.server.nashorn.wicket.provider.select2.NashornSingleChoiceProvider;
import com.angkorteam.mbaas.server.page.PagePage;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.model.util.MapModel;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.registry.IKey;
import org.apache.wicket.protocol.ws.api.registry.SimpleWebSocketConnectionRegistry;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.Condition;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Created by socheat on 5/30/16.
 */
public class Factory implements Serializable,
        IListFactory,
        IUrlTextFieldFactory,
        IMapFactory,
        ITextFieldFactory,
        IFormFactory,
        IButtonFactory,
        IBlockFactory,
        IWebMarkupContainerFactory,
        IEditorTextAreaFactory,
        ILabelFactory,
        IJdbcTemplateFactory,
        IPropertyModelFactory,
        IFeedback,
        IFullCalendarFactory,
        IChoiceRendererFactory,
        IPasswordTextFieldFactory,
        IRangeTextFieldFactory,
        INumberTextFieldFactory,
        ISelect2MultipleChoiceFactory,
        IDateTextFieldFactory,
        IDropDownChoiceFactory,
        ITableFactory,
        IListMultipleChoiceFactory,
        IOptionFactory,
        IHiddenFieldFactory,
        IRequiredTextFieldFactory,
        IColorTextFieldFactory,
        ITabbedPanelFactory,
        IEmailTextFieldFactory,
        ITimeTextFieldFactory,
        IListChoiceFactory,
        ILinkFactory,
        IRadioChoiceFactory,
        IImageFactory,
        IWindowModalFactory,
        IAjaxButtonFactory,
        IMultiFileUploadFactory,
        ICheckBoxMultipleChoiceFactory,
        ISelect2SingleChoiceFactory,
        IFileUploadFactory,
        IRepeatingViewFactory,
        ITextAreaFactory,
        IWebSocketFactory,
        ICheckBoxFactory {

    private MarkupContainer container;

    private Map<String, Component> children;

    private Map<String, Object> pageModel;

    private String script;

    private String applicationCode;

    private Disk disk;

    private PageParameters pageParameters;

    private boolean stage;

    private String applicationUserId;

    private NashornWebSocketBehavior webSocketBehavior;

    public Factory(String applicationUserId, PageParameters pageParameters, MarkupContainer container, Disk disk, String applicationCode, String script, boolean stage, Map<String, Object> pageModel) {
        this.applicationCode = applicationCode;
        this.pageParameters = pageParameters;
        this.container = container;
        this.pageModel = pageModel;
        this.stage = stage;
        this.script = script;
        this.disk = disk;
        this.applicationUserId = applicationUserId;
        this.children = new HashMap<>();
    }

    public PageParameters getPageParameters() {
        return this.pageParameters;
    }

    public Component getChildren(String id) {
        return this.children.get(id);
    }

    public String getApplicationUserId() {
        return this.applicationUserId;
    }

    public Map<String, Object> getPageModel() {
        return this.pageModel;
    }

    @Override
    public JdbcTemplate createJdbcTemplate() {
        return ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
    }

    public Condition[] arrayCondition(Condition... conditions) {
        return conditions;
    }

    public void navigateTo(String pageCode) {
        navigateTo(pageCode, new HashMap<>());
    }

    public void navigateTo(String pageCode, ScriptObjectMirror js) {
        Map<String, Object> params = new HashMap<>();
        if (js != null && !js.isEmpty()) {
            for (Map.Entry<String, Object> param : js.entrySet()) {
                params.put(param.getKey(), param.getValue());
            }
        }
        navigateTo(pageCode, params);
    }

    public StringBuilder createStringBuilder() {
        return new StringBuilder();
    }

    public StringBuffer createStringBuffer() {
        return new StringBuffer();
    }

    public void navigateTo(String pageCode, Map<String, Object> params) {
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
        String pageId = jdbcTemplate.queryForObject("SELECT " + Jdbc.Page.PAGE_ID + " FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.CODE + " = ?", String.class, pageCode);
        PageParameters parameters = new PageParameters();
        parameters.add("pageId", pageId);
        if (this.stage) {
            parameters.add("stage", this.stage);
        }
        for (Map.Entry<String, Object> param : params.entrySet()) {
            if (param.getKey().equals("pageId") || param.getKey().equals("stage")) {
                throw new WicketRuntimeException("pageId or stage not allow, it is reserved for system");
            }
            parameters.add(param.getKey(), param.getValue());
        }
        RequestCycle.get().setResponsePage(PagePage.class, parameters);
    }

    @Override
    public SimpleJdbcInsert createSimpleJdbcInsert(String tableName) {
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        jdbcInsert.withTableName(tableName);
        return jdbcInsert;
    }

    @Override
    public SimpleJdbcUpdate createSimpleJdbcUpdate(String tableName) {
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
        SimpleJdbcUpdate jdbcUpdate = new SimpleJdbcUpdate(jdbcTemplate);
        jdbcUpdate.withTableName(tableName);
        return jdbcUpdate;
    }

    @Override
    public Option createOption(String id, String text) {
        return new Option(id, text);
    }

    @Override
    public <E> List<E> createList() {
        return new LinkedList<>();
    }

    @Override
    public <E> ListModel<E> createListModel(List<E> object) {
        return new ListModel<>(object);
    }

    @Override
    public <K, V> Map<K, V> createMap() {
        return new LinkedHashMap<>();
    }

    @Override
    public <K, V> MapModel<K, V> createMapModel(Map<K, V> object) {
        return new MapModel<>(object);
    }

    @Override
    public <T> PropertyModel<T> createPropertyModel(Object model, String expression) {
        PropertyModel<T> object = new PropertyModel<>(model, expression);
        return object;
    }

    @Override
    public NashornChoiceRenderer createChoiceRenderer(String id, String text) {
        NashornChoiceRenderer object = new NashornChoiceRenderer(id, text);
        return object;
    }

    @Override
    public NashornLabel createLabel(String id) {
        return createLabel(container, id);
    }

    @Override
    public NashornLabel createLabel(MarkupContainer container, String id) {
        NashornLabel object = new NashornLabel(id, createPropertyModel(this.pageModel, id));
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornWebSocketBehavior registerWebSocket() {
        if (this.webSocketBehavior == null) {
            this.webSocketBehavior = new NashornWebSocketBehavior(this, this.disk, this.script, this.pageModel, this.applicationCode);
            this.container.add(this.webSocketBehavior);
        }
        return this.webSocketBehavior;
    }

    public NashornWebSocketBehavior getWebSocketBehavior() {
        return this.webSocketBehavior;
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
        object.setPageModel(this.pageModel);
        object.setDisk(this.disk);
        object.setFactory(this);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public <T> NashornTextField<T> createTextField(String id, Class<T> type) {
        return createTextField(container, id, type);
    }

    @Override
    public <T> NashornTextField<T> createTextField(MarkupContainer container, String id, Class<T> type) {
        NashornTextField<T> object = new NashornTextField<>(id, createPropertyModel(this.pageModel, id), type);
        object.setScript(this.script);
        object.setPageModel(this.pageModel);
        object.setFactory(this);
        object.setDisk(this.disk);
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
        object.setDisk(this.disk);
        object.setFactory(this);
        object.setPageModel(this.pageModel);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornSelect2MultipleChoice createSelect2MultipleChoice(String id, IChoiceRenderer<Map<String, Object>> renderer) {
        return createSelect2MultipleChoice(container, id, renderer);
    }

    @Override
    public NashornSelect2MultipleChoice createSelect2MultipleChoice(MarkupContainer container, String id, IChoiceRenderer<Map<String, Object>> renderer) {
        NashornMultipleChoiceProvider provider = new NashornMultipleChoiceProvider(this, id, this.script);
        NashornSelect2MultipleChoice object = new NashornSelect2MultipleChoice(id, createPropertyModel(this.pageModel, id), provider, renderer);
        object.setScript(this.script);
        object.setDisk(this.disk);
        object.setFactory(this);
        object.setPageModel(this.pageModel);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornSelect2SingleChoice createSelect2SingleChoice(String id, IChoiceRenderer<Map<String, Object>> renderer) {
        return createSelect2SingleChoice(container, id, renderer);
    }

    @Override
    public NashornSelect2SingleChoice createSelect2SingleChoice(MarkupContainer container, String id, IChoiceRenderer<Map<String, Object>> renderer) {
        NashornSingleChoiceProvider provider = new NashornSingleChoiceProvider(this, id, this.script);
        NashornSelect2SingleChoice object = new NashornSelect2SingleChoice(id, createPropertyModel(this.pageModel, id), provider, renderer);
        object.setPageModel(this.pageModel);
        object.setDisk(this.disk);
        object.setFactory(this);
        object.setScript(this.script);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornDateTextField createDateTextField(String id) {
        return createDateTextField(container, id);
    }

    @Override
    public NashornDateTextField createDateTextField(MarkupContainer container, String id) {
        NashornDateTextField object = new NashornDateTextField(id, createPropertyModel(this.pageModel, id));
        object.setScript(this.script);
        object.setDisk(this.disk);
        object.setFactory(this);
        object.setPageModel(this.pageModel);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornColorTextField createColorTextField(String id) {
        return createColorTextField(container, id);
    }

    @Override
    public NashornColorTextField createColorTextField(MarkupContainer container, String id) {
        NashornColorTextField object = new NashornColorTextField(id, createPropertyModel(this.pageModel, id));
        object.setPageModel(this.pageModel);
        object.setDisk(this.disk);
        object.setFactory(this);
        object.setScript(this.script);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornTimeTextField createTimeTextField(String id) {
        return createTimeTextField(container, id);
    }

    @Override
    public NashornTimeTextField createTimeTextField(MarkupContainer container, String id) {
        NashornTimeTextField object = new NashornTimeTextField(id, createPropertyModel(this.pageModel, id));
        object.setPageModel(this.pageModel);
        object.setFactory(this);
        object.setDisk(this.disk);
        object.setScript(this.script);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornDropDownChoice createDropDownChoice(String id, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        return createDropDownChoice(container, id, choices, renderer);
    }

    @Override
    public NashornDropDownChoice createDropDownChoice(MarkupContainer container, String id, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        NashornDropDownChoice object = new NashornDropDownChoice(id, createPropertyModel(this.pageModel, id), choices, renderer);
        object.setScript(this.script);
        object.setDisk(this.disk);
        object.setPageModel(this.pageModel);
        object.setFactory(this);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornListMultipleChoice createListMultipleChoice(String id, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        return createListMultipleChoice(container, id, choices, renderer);
    }

    @Override
    public NashornListMultipleChoice createListMultipleChoice(MarkupContainer container, String id, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        NashornListMultipleChoice object = new NashornListMultipleChoice(id, createPropertyModel(this.pageModel, id), choices, renderer);
        object.setScript(this.script);
        object.setDisk(this.disk);
        object.setPageModel(this.pageModel);
        object.setFactory(this);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornListMultipleChoice createListMultipleChoice(String id, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer, int maxRows) {
        return createListMultipleChoice(container, id, choices, renderer, maxRows);
    }

    @Override
    public NashornListMultipleChoice createListMultipleChoice(MarkupContainer container, String id, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer, int maxRows) {
        NashornListMultipleChoice object = new NashornListMultipleChoice(id, createPropertyModel(this.pageModel, id), choices, renderer, maxRows);
        object.setScript(this.script);
        object.setDisk(this.disk);
        object.setPageModel(this.pageModel);
        object.setFactory(this);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornCheckBoxMultipleChoice createCheckBoxMultipleChoice(String id, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        return createCheckBoxMultipleChoice(container, id, choices, renderer);
    }

    @Override
    public NashornCheckBoxMultipleChoice createCheckBoxMultipleChoice(MarkupContainer container, String id, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        NashornCheckBoxMultipleChoice object = new NashornCheckBoxMultipleChoice(id, createPropertyModel(this.pageModel, id), choices, renderer);
        object.setScript(this.script);
        object.setDisk(this.disk);
        object.setPageModel(this.pageModel);
        object.setFactory(this);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornListChoice createListChoice(String id, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        return createListChoice(container, id, choices, renderer);
    }

    @Override
    public NashornListChoice createListChoice(MarkupContainer container, String id, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        NashornListChoice object = new NashornListChoice(id, createPropertyModel(this.pageModel, id), choices, renderer);
        object.setScript(this.script);
        object.setDisk(this.disk);
        object.setPageModel(this.pageModel);
        object.setFactory(this);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornListChoice createListChoice(String id, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer, int maxRows) {
        return createListChoice(container, id, choices, renderer, maxRows);
    }

    @Override
    public NashornListChoice createListChoice(MarkupContainer container, String id, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer, int maxRows) {
        NashornListChoice object = new NashornListChoice(id, createPropertyModel(this.pageModel, id), choices, renderer, maxRows);
        object.setScript(this.script);
        object.setDisk(this.disk);
        object.setPageModel(this.pageModel);
        object.setFactory(this);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornRadioChoice createRadioChoice(String id, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        return createRadioChoice(container, id, choices, renderer);
    }

    @Override
    public NashornRadioChoice createRadioChoice(MarkupContainer container, String id, IModel<List<Map<String, Object>>> choices, IChoiceRenderer<Map<String, Object>> renderer) {
        NashornRadioChoice object = new NashornRadioChoice(id, createPropertyModel(this.pageModel, id), choices, renderer);
        object.setDisk(this.disk);
        object.setFactory(this);
        object.setPageModel(this.pageModel);
        object.setScript(this.script);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornCheckBox createCheckBox(String id) {
        return createCheckBox(container, id);
    }

    @Override
    public NashornCheckBox createCheckBox(MarkupContainer container, String id) {
        NashornCheckBox object = new NashornCheckBox(id, createPropertyModel(this.pageModel, id));
        object.setDisk(this.disk);
        object.setFactory(this);
        object.setPageModel(this.pageModel);
        object.setScript(this.script);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornFilterForm createFilterForm(String id) {
        return createFilterForm(container, id);
    }

    @Override
    public NashornFilterForm createFilterForm(MarkupContainer container, String id) {
        FilterStateLocator stateLocator = new FilterStateLocator();
        NashornFilterForm object = new NashornFilterForm(id, stateLocator);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornTable createTable(String id, JSObject columns, int rowsPerPage) {
        return createTable(container, id, columns, rowsPerPage);
    }

    @Override
    public NashornTable createTable(MarkupContainer container, String id, JSObject columns, int rowsPerPage) {
        if (!columns.isArray()) {
            throw new WicketRuntimeException("columns is not right");
        }
        if (!(container instanceof NashornFilterForm)) {
            throw new WicketRuntimeException("filter form is required, factory.createFilterForm('id)");
        }
        NashornFilterForm form = (NashornFilterForm) container;
        NashornTableProvider tableProvider = new NashornTableProvider(form.getStateLocator(), this, id, this.script, this.applicationCode, this.pageModel);
        List<IColumn<Map<String, Object>, String>> tableFields = new ArrayList<>();
        if (columns instanceof ScriptObjectMirror) {
            if (((ScriptObjectMirror) columns).size() > 0) {
                for (int i = 0; i < ((ScriptObjectMirror) columns).size(); i++) {
                    Object column = columns.getSlot(i);
                    if (column instanceof ScriptObjectMirror) {
                        Class<?> clazz = (Class<?>) ((ScriptObjectMirror) column).get("classColumn");
                        String tableColumn = (String) ((ScriptObjectMirror) column).get("tableColumn");
                        String queryColumn = (String) ((ScriptObjectMirror) column).get("queryColumn");
                        String htmlColumn = (String) ((ScriptObjectMirror) column).get("htmlColumn");
                        if (htmlColumn == null || "".equals(htmlColumn)) {
                            htmlColumn = "Label";
                        }
                        if ("Label".equals(htmlColumn)) {
                            if (clazz == java.time.LocalTime.class) {
                                NashornTimeColumn tableField = new NashornTimeColumn(Model.of(tableColumn), tableColumn);
                                tableFields.add(tableField);
                                tableProvider.selectField(tableColumn, queryColumn, java.time.LocalTime.class);
                            } else if (clazz == java.time.LocalDate.class) {
                                NashornDateColumn tableField = new NashornDateColumn(Model.of(tableColumn), tableColumn);
                                tableFields.add(tableField);
                                tableProvider.selectField(tableColumn, queryColumn, java.time.LocalDate.class);
                            } else if (clazz == java.time.LocalDateTime.class) {
                                NashornDateTimeColumn tableField = new NashornDateTimeColumn(Model.of(tableColumn), tableColumn);
                                tableFields.add(tableField);
                                tableProvider.selectField(tableColumn, queryColumn, java.time.LocalDateTime.class);
                            } else if (clazz == Boolean.class
                                    || clazz == Byte.class
                                    || clazz == Short.class
                                    || clazz == Integer.class
                                    || clazz == Long.class
                                    || clazz == Float.class
                                    || clazz == Double.class
                                    || clazz == BigInteger.class
                                    || clazz == BigDecimal.class
                                    || clazz == Character.class
                                    || clazz == String.class
                                    ) {
                                NashornTextColumn tableField = new NashornTextColumn(Model.of(tableColumn), tableColumn);
                                tableProvider.selectField(tableColumn, queryColumn, clazz);
                                tableFields.add(tableField);
                            }
                        } else if ("Hidden".equals(htmlColumn)) {
                            if (clazz == java.time.LocalTime.class) {
                                tableProvider.selectField(tableColumn, queryColumn, java.time.LocalTime.class);
                            } else if (clazz == java.time.LocalDate.class) {
                                tableProvider.selectField(tableColumn, queryColumn, java.time.LocalDate.class);
                            } else if (clazz == java.time.LocalDateTime.class) {
                                tableProvider.selectField(tableColumn, queryColumn, java.time.LocalDateTime.class);
                            } else if (clazz == Boolean.class
                                    || clazz == Byte.class
                                    || clazz == Short.class
                                    || clazz == Integer.class
                                    || clazz == Long.class
                                    || clazz == Float.class
                                    || clazz == Double.class
                                    || clazz == BigInteger.class
                                    || clazz == BigDecimal.class
                                    || clazz == Character.class
                                    || clazz == String.class
                                    ) {
                                tableProvider.selectField(tableColumn, queryColumn, clazz);
                            }
                        } else if ("TextLink".equals(htmlColumn)) {
                            NashornTextLinkColumn tableField = new NashornTextLinkColumn(Model.of(tableColumn), tableColumn, id);
                            tableField.setPageModel(this.pageModel);
                            tableField.setDisk(this.disk);
                            tableField.setFactory(this);
                            tableField.setScript(this.script);
                            tableProvider.selectField(tableColumn, queryColumn, clazz);
                            tableFields.add(tableField);
                        } else if ("CheckBox".equals(htmlColumn)) {
                            ScriptObjectMirror actionColumn = (ScriptObjectMirror) ((ScriptObjectMirror) column).get("actionColumn");
                            Map<String, String> actions = new HashMap<>();
                            if (actionColumn != null) {
                                for (Map.Entry<String, Object> action : actionColumn.entrySet()) {
                                    actions.put(action.getKey(), (String) action.getValue());
                                }
                            }
                            String objectColumn = (String) ((ScriptObjectMirror) column).get("objectColumn");
                            NashornCheckBoxColumn tableField = new NashornCheckBoxColumn(Model.of(tableColumn), objectColumn, actions, id);
                            tableField.setPageModel(this.pageModel);
                            tableField.setDisk(this.disk);
                            tableField.setFactory(this);
                            tableField.setScript(this.script);
                            tableProvider.selectField(tableColumn, queryColumn, clazz);
                            tableFields.add(tableField);
                        } else if ("Action".equals(htmlColumn)) {
                            ScriptObjectMirror actionColumn = (ScriptObjectMirror) ((ScriptObjectMirror) column).get("actionColumn");
                            Map<String, String> actions = new HashMap<>();
                            if (actionColumn != null) {
                                for (Map.Entry<String, Object> action : actionColumn.entrySet()) {
                                    actions.put(action.getKey(), (String) action.getValue());
                                }
                            }
                            ScriptObjectMirror linkColumn = (ScriptObjectMirror) ((ScriptObjectMirror) column).get("linkColumn");
                            Map<String, String> links = new HashMap<>();
                            if (linkColumn != null) {
                                for (Map.Entry<String, Object> link : linkColumn.entrySet()) {
                                    links.put(link.getKey(), (String) link.getValue());
                                }
                            }
                            NashornActionColumn tableField = new NashornActionColumn(Model.of(tableColumn), links, actions, id, this.pageModel);
                            tableField.setDisk(this.disk);
                            tableField.setFactory(this);
                            tableField.setScript(this.script);
                            tableProvider.selectField(tableColumn, queryColumn, clazz);
                            tableFields.add(tableField);
                        }
                    }
                }
            }
        }
        NashornTable object = new NashornTable(id, tableFields, tableProvider, rowsPerPage);
        object.addTopToolbar(new FilterToolbar(object, form));
        container.add(object);
        this.children.put(id, object);
        this.children.put(object.getId(), object);
        return object;
    }

    @Override
    public NashornEmailTextField createEmailTextField(String id) {
        return createEmailTextField(container, id);
    }

    @Override
    public NashornEmailTextField createEmailTextField(MarkupContainer container, String id) {
        NashornEmailTextField object = new NashornEmailTextField(id, createPropertyModel(this.pageModel, id));
        object.setScript(this.script);
        object.setDisk(this.disk);
        object.setFactory(this);
        object.setPageModel(this.pageModel);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public <T> NashornHiddenField<T> createHiddenField(String id, Class<T> type) {
        return createHiddenField(container, id, type);
    }

    @Override
    public <T> NashornHiddenField<T> createHiddenField(MarkupContainer container, String id, Class<T> type) {
        NashornHiddenField<T> object = new NashornHiddenField<>(id, createPropertyModel(this.pageModel, id), type);
        object.setScript(this.script);
        object.setFactory(this);
        object.setDisk(this.disk);
        object.setPageModel(this.pageModel);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public <T extends Number & Comparable<T>> NashornNumberTextField<T> createNumberTextField(String id, Class<T> type) {
        return createNumberTextField(container, id, type);
    }

    @Override
    public <T extends Number & Comparable<T>> NashornNumberTextField<T> createNumberTextField(MarkupContainer container, String id, Class<T> type) {
        NashornNumberTextField<T> object = new NashornNumberTextField<>(id, createPropertyModel(this.pageModel, id), type);
        object.setFactory(this);
        object.setPageModel(this.pageModel);
        object.setDisk(this.disk);
        object.setScript(this.script);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornPasswordTextField createPasswordTextField(String id) {
        return createPasswordTextField(container, id);
    }

    @Override
    public NashornPasswordTextField createPasswordTextField(MarkupContainer container, String id) {
        NashornPasswordTextField object = new NashornPasswordTextField(id, createPropertyModel(this.pageModel, id));
        object.setScript(this.script);
        object.setDisk(this.disk);
        object.setFactory(this);
        object.setPageModel(this.pageModel);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public <T extends Number & Comparable<T>> NashornRangeTextField<T> createRangeTextField(String id, Class<T> type) {
        return createRangeTextField(container, id, type);
    }

    @Override
    public <T extends Number & Comparable<T>> NashornRangeTextField<T> createRangeTextField(MarkupContainer container, String id, Class<T> type) {
        NashornRangeTextField<T> object = new NashornRangeTextField<>(id, createPropertyModel(this.pageModel, id), type);
        object.setScript(this.script);
        object.setDisk(this.disk);
        object.setFactory(this);
        object.setPageModel(this.pageModel);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public <T> NashornRequiredTextField<T> createRequiredTextField(String id, Class<T> type) {
        return createRequiredTextField(container, id, type);
    }

    @Override
    public <T> NashornRequiredTextField<T> createRequiredTextField(MarkupContainer container, String id, Class<T> type) {
        NashornRequiredTextField<T> object = new NashornRequiredTextField<>(id, createPropertyModel(this.pageModel, id), type);
        object.setScript(this.script);
        object.setDisk(this.disk);
        object.setPageModel(this.pageModel);
        object.setFactory(this);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornUrlTextField createUrlTextField(String id) {
        return createUrlTextField(container, id);
    }

    @Override
    public NashornUrlTextField createUrlTextField(MarkupContainer container, String id) {
        NashornUrlTextField object = new NashornUrlTextField(id, createPropertyModel(this.pageModel, id));
        object.setDisk(this.disk);
        object.setFactory(this);
        object.setPageModel(this.pageModel);
        object.setScript(this.script);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornFileUpload createFileUpload(String id) {
        return createFileUpload(container, id);
    }

    @Override
    public NashornFileUpload createFileUpload(MarkupContainer container, String id) {
        NashornFileUpload object = new NashornFileUpload(id, createPropertyModel(this.pageModel, id));
        object.setScript(this.script);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornMultiFileUpload createMultiFileUpload(String id) {
        return createMultiFileUpload(container, id);
    }

    @Override
    public NashornMultiFileUpload createMultiFileUpload(MarkupContainer container, String id) {
        NashornMultiFileUpload object = new NashornMultiFileUpload(id, createPropertyModel(this.pageModel, id));
        object.setScript(this.script);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornMultiFileUpload createMultiFileUpload(String id, int max) {
        return createMultiFileUpload(container, id, max);
    }

    @Override
    public NashornMultiFileUpload createMultiFileUpload(MarkupContainer container, String id, int max) {
        NashornMultiFileUpload object = new NashornMultiFileUpload(id, createPropertyModel(this.pageModel, id), max);
        object.setScript(this.script);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornImage createImage(String id) {
        return createImage(container, id);
    }

    @Override
    public NashornImage createImage(MarkupContainer container, String id) {
        NashornImage object = new NashornImage(id, createPropertyModel(this.pageModel, id));
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornAjaxLink createAjaxLink(String id) {
        return createAjaxLink(container, id);
    }

    @Override
    public NashornAjaxLink createAjaxLink(MarkupContainer container, String id) {
        NashornAjaxLink object = new NashornAjaxLink(id);
        object.setScript(this.script);
        object.setPageModel(this.pageModel);
        object.setDisk(this.disk);
        object.setFactory(this);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornLink createLink(String id) {
        return createLink(container, id);
    }

    @Override
    public NashornLink createLink(MarkupContainer container, String id) {
        NashornLink object = new NashornLink(id);
        object.setScript(this.script);
        object.setPageModel(this.pageModel);
        object.setDisk(this.disk);
        object.setFactory(this);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public BlockPanel createBlock(String id, String blockCode) {
        return createBlock(container, id, blockCode);
    }

    @Override
    public BlockPanel createBlock(MarkupContainer container, String id, String blockCode) {
        Map<String, Object> blockModel = new HashMap<>();
        pageModel.put(id, blockModel);
        MapModel<String, Object> model = new MapModel<>(blockModel);
        BlockPanel object = new BlockPanel(id, blockCode, this.stage, this.pageModel, model);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public RepeatingView createRepeatingView(String id) {
        return createRepeatingView(container, id);
    }

    @Override
    public RepeatingView createRepeatingView(MarkupContainer container, String id) {
        RepeatingView object = new RepeatingView(id);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public TextFeedbackPanel createFeedback(String id, FormComponent<?> component) {
        return createFeedback(container, id, component);
    }

    @Override
    public TextFeedbackPanel createFeedback(MarkupContainer container, String id, FormComponent<?> component) {
        TextFeedbackPanel object = new TextFeedbackPanel(id, component);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornTextArea createTextArea(String id) {
        return createTextArea(container, id);
    }

    @Override
    public NashornTextArea createTextArea(MarkupContainer container, String id) {
        NashornTextArea object = new NashornTextArea(id, createPropertyModel(this.pageModel, id));
        object.setDisk(this.disk);
        object.setFactory(this);
        object.setPageModel(this.pageModel);
        object.setScript(this.script);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public BookmarkablePageLink<Void> createPageLink(String id, String pageCode) {
        return createPageLink(container, id, pageCode, new HashMap<>());
    }

    @Override
    public BookmarkablePageLink<Void> createPageLink(String id, String pageCode, ScriptObjectMirror params) {
        return createPageLink(container, id, pageCode, params);
    }

    @Override
    public BookmarkablePageLink<Void> createPageLink(String id, String pageCode, Map<String, Object> params) {
        return createPageLink(container, id, pageCode, params);
    }

    @Override
    public BookmarkablePageLink<Void> createPageLink(MarkupContainer container, String id, String pageCode) {
        return createPageLink(container, id, pageCode, new HashMap<>());
    }

    @Override
    public BookmarkablePageLink<Void> createPageLink(MarkupContainer container, String id, String pageCode, ScriptObjectMirror params) {
        Map<String, Object> temps = new HashMap<>();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                temps.put(param.getKey(), param.getValue());
            }
        }
        return createPageLink(container, id, pageCode, temps);
    }

    @Override
    public BookmarkablePageLink<Void> createPageLink(MarkupContainer container, String id, String pageCode, Map<String, Object> params) {
        PageParameters parameters = new PageParameters();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                parameters.add(param.getKey(), param.getValue());
            }
        }
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
        String pageId = jdbcTemplate.queryForObject("SELECT " + Jdbc.Page.PAGE_ID + " FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.CODE + " = ?", String.class, pageCode);
        parameters.add("pageId", pageId);
        if (this.stage) {
            parameters.add("stage", true);
        }
        BookmarkablePageLink<Void> object = new BookmarkablePageLink<>(id, PagePage.class, parameters);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public FullCalendar createFullCalendar(String id) {
        return createFullCalendar(container, id);
    }

    @Override
    public FullCalendar createFullCalendar(MarkupContainer container, String id) {
        NashornFullCalendarProvider provider = new NashornFullCalendarProvider(this, id, this.script, this.applicationCode);
        FullCalendar object = new FullCalendar(id, provider);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public FullCalendarItem createFullCalendarItem(String id, String title, Date start, Date end) {
        FullCalendarItem item = new FullCalendarItem();
        item.setId(id);
        item.setTitle(title);
        item.setStart(DateFormatUtils.ISO_DATETIME_FORMAT.format(start));
        item.setEnd(DateFormatUtils.ISO_DATETIME_FORMAT.format(end));
        return item;
    }

    @Override
    public CKEditorTextArea createEditorTextArea(String id) {
        return createEditorTextArea(container, id);
    }

    @Override
    public CKEditorTextArea createEditorTextArea(MarkupContainer container, String id) {
        CKEditorTextArea object = new CKEditorTextArea(id, createPropertyModel(this.pageModel, id));
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public String createAddressLink(String pageCode) {
        return createAddressLink(pageCode, new HashMap<>());
    }

    @Override
    public String createAddressLink(String pageCode, ScriptObjectMirror params) {
        Map<String, Object> temps = new HashMap<>();
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> param : params.entrySet()) {
                temps.put(param.getKey(), param.getValue());
            }
        }
        return createAddressLink(pageCode, temps);
    }

    @Override
    public String createAddressLink(String pageCode, Map<String, Object> params) {
        RequestCycle requestCycle = RequestCycle.get();
        PageParameters parameters = new PageParameters();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            parameters.add(param.getKey(), param.getValue());
        }
        JdbcTemplate jdbcTemplate = ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
        String pageId = jdbcTemplate.queryForObject("SELECT " + Jdbc.Page.PAGE_ID + " FROM " + Jdbc.PAGE + " WHERE " + Jdbc.Page.CODE + " = ?", String.class, pageCode);
        parameters.add("pageId", pageId);
        if (this.stage) {
            parameters.add("stage", true);
        }
        return requestCycle.urlFor(PagePage.class, parameters).toString();
    }

    @Override
    public NashornAjaxButton createAjaxButton(String id) {
        return createAjaxButton(container, id);
    }

    @Override
    public NashornAjaxButton createAjaxButton(MarkupContainer container, String id) {
        NashornAjaxButton object = new NashornAjaxButton(id);
        object.setScript(this.script);
        object.setDisk(this.disk);
        object.setFactory(this);
        object.setPageModel(this.pageModel);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public void pushMessage(String message) {
        // TODO : remove this trick
        if (message == null || "".equals(message)) {
            message = " ";
        }
        SimpleWebSocketConnectionRegistry registry = new SimpleWebSocketConnectionRegistry();
        Collection<IWebSocketConnection> connections = registry.getConnections(ApplicationUtils.getApplication());
        for (IWebSocketConnection connection : connections) {
            if (connection != null && connection.isOpen()) {
                try {
                    connection.sendMessage(message);
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public void pushMessage(String sessionId, String message) {
        // TODO : remove this trick
        if (message == null || "".equals(message)) {
            message = " ";
        }
        SimpleWebSocketConnectionRegistry registry = new SimpleWebSocketConnectionRegistry();
        Collection<IWebSocketConnection> connections = registry.getConnections(ApplicationUtils.getApplication(), sessionId);
        for (IWebSocketConnection connection : connections) {
            if (connection != null && connection.isOpen()) {
                try {
                    connection.sendMessage(message);
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public void pushMessage(String sessionId, IKey key, String message) {
        // TODO : remove this trick
        if (message == null || "".equals(message)) {
            message = " ";
        }
        SimpleWebSocketConnectionRegistry registry = new SimpleWebSocketConnectionRegistry();
        IWebSocketConnection connection = registry.getConnection(ApplicationUtils.getApplication(), sessionId, key);
        if (connection != null && connection.isOpen()) {
            try {
                connection.sendMessage(message);
            } catch (IOException e) {
            }
        }
    }

    @Override
    public TabbedPanel<? extends ITab> createTabbedPanel(String id, JSObject items) {
        return createTabbedPanel(container, id, items);
    }

    @Override
    public TabbedPanel<? extends ITab> createTabbedPanel(MarkupContainer container, String id, JSObject items) {
        if (!items.isArray()) {
            throw new WicketRuntimeException("items is not right");
        }
        List<ITab> tabs = new ArrayList<>();
        if (items instanceof ScriptObjectMirror) {
            if (((ScriptObjectMirror) items).size() > 0) {
                for (int i = 0; i < ((ScriptObjectMirror) items).size(); i++) {
                    Object column = items.getSlot(i);
                    if (column instanceof ScriptObjectMirror) {
                        String blockTitle = (String) ((ScriptObjectMirror) column).get("blockTitle");
                        String blockCode = (String) ((ScriptObjectMirror) column).get("blockCode");
                        Map<String, Object> blockModel = new HashMap<>();
                        this.pageModel.put(id + "_" + blockTitle, blockCode);
                        NashornTab nashornTab = new NashornTab(Model.of(blockTitle), blockCode, this.stage, this.pageModel, blockModel);
                        tabs.add(nashornTab);
                    }
                }
            }
        }
        TabbedPanel<ITab> object = new TabbedPanel<>(id, tabs);
        container.add(object);
        this.children.put(id, object);
        this.children.put(object.getId(), object);
        return object;
    }

    @Override
    public ModalWindow createModalWindow(String id, String blockCode) {
        return createModalWindow(container, id, blockCode);
    }

    @Override
    public ModalWindow createModalWindow(MarkupContainer container, String id, String blockCode) {
        Map<String, Object> blockModel = new HashMap<>();
        pageModel.put(id, blockModel);
        MapModel<String, Object> model = new MapModel<>(blockModel);
        BlockPanel content = new BlockPanel(ModalWindow.CONTENT_ID, blockCode, this.stage, this.pageModel, model);
        ModalWindow object = new ModalWindow(id);
        object.setContent(content);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

}