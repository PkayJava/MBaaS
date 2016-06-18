package com.angkorteam.mbaas.server.nashorn;

import com.angkorteam.framework.extension.spring.SimpleJdbcUpdate;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.server.block.BlockPanel;
import com.angkorteam.mbaas.server.nashorn.factory.*;
import com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.repeater.data.table.*;
import com.angkorteam.mbaas.server.nashorn.wicket.extensions.markup.html.repeater.data.table.filter.NashornFilterForm;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.basic.NashornLabel;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.*;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.upload.NashornFileUpload;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.upload.NashornMultiFileUpload;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.image.NashornImage;
import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.link.NashornLink;
import com.angkorteam.mbaas.server.nashorn.wicket.provider.NashornTableProvider;
import com.angkorteam.mbaas.server.nashorn.wicket.provider.select2.NashornChoiceRenderer;
import com.angkorteam.mbaas.server.nashorn.wicket.provider.select2.NashornMultipleChoiceProvider;
import com.angkorteam.mbaas.server.nashorn.wicket.provider.select2.NashornSingleChoiceProvider;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.model.util.MapModel;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.UrlValidator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

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
        ILabelFactory,
        IJdbcTemplateFactory,
        IPropertyModelFactory,
        IFeedback,
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
        IEmailTextFieldFactory,
        ITimeTextFieldFactory,
        IListChoiceFactory,
        ILinkFactory,
        IRadioChoiceFactory,
        IImageFactory,
        IMultiFileUploadFactory,
        ICheckBoxMultipleChoiceFactory,
        ISelect2SingleChoiceFactory,
        IFileUploadFactory,
        IRepeatingViewFactory,
        ICheckBoxFactory {

    private MarkupContainer container;

    private Map<String, Component> children;

    private Map<String, Object> pageModel;

    private String script;

    private String applicationCode;

    private Disk disk;

    private boolean stage;

    public Factory(MarkupContainer container, Disk disk, String applicationCode, String script, boolean stage, Map<String, Object> pageModel) {
        this.container = container;
        this.pageModel = pageModel;
        this.stage = stage;
        this.script = script;
        this.disk = disk;
        this.applicationCode = applicationCode;
        this.children = new HashMap<>();
    }

    public Component getChildren(String id) {
        return this.children.get(id);
    }

    @Override
    public JdbcTemplate createJdbcTemplate() {
        return ApplicationUtils.getApplication().getJdbcTemplate(this.applicationCode);
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
        object.setUserModel(this.pageModel);
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
        object.setUserModel(this.pageModel);
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
        object.setScript(this.script);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornFilterForm createTable(String id, JSObject columns, int rowsPerPage) {
        return createTable(container, id, columns, rowsPerPage);
    }

    @Override
    public NashornFilterForm createTable(MarkupContainer container, String id, JSObject columns, int rowsPerPage) {
        if (!columns.isArray()) {
            throw new WicketRuntimeException("columns is not right");
        }
        NashornTableProvider tableProvider = new NashornTableProvider(this, id, this.script, this.applicationCode);
        List<IColumn<Map<String, Object>, String>> tableFields = new ArrayList<>();
        if (columns instanceof ScriptObjectMirror) {
            if (((ScriptObjectMirror) columns).size() > 0) {
                for (int i = 0; i < ((ScriptObjectMirror) columns).size(); i++) {
                    Object column = columns.getSlot(i);
                    if (column instanceof ScriptObjectMirror) {
                        Class<?> clazz = (Class<?>) ((ScriptObjectMirror) column).get("classColumn");
                        String tableColumn = (String) ((ScriptObjectMirror) column).get("tableColumn");
                        String queryColumn = (String) ((ScriptObjectMirror) column).get("queryColumn");
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
                            NashornTextColumn tableField = new NashornTextColumn(clazz, Model.of(tableColumn), tableColumn);
                            tableProvider.selectField(tableColumn, queryColumn, clazz);
                            tableFields.add(tableField);
                        }
                    }
                }
            }
        }
        NashornTable table = new NashornTable(id + "_table", tableFields, tableProvider, rowsPerPage);
        NashornFilterForm object = new NashornFilterForm(id, tableProvider);
        table.addTopToolbar(new FilterToolbar(table, object));
        object.add(table);
        container.add(object);
        this.children.put(id, object);
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
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornEmailTextField createEmailTextField(String id, IValidator<String> validator) {
        return createEmailTextField(container, id, validator);
    }

    @Override
    public NashornEmailTextField createEmailTextField(MarkupContainer container, String id, IValidator<String> validator) {
        NashornEmailTextField object = new NashornEmailTextField(id, createPropertyModel(this.pageModel, id), validator);
        object.setScript(this.script);
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
        object.setScript(this.script);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornUrlTextField createUrlTextField(String id, UrlValidator validator) {
        return createUrlTextField(container, id, validator);
    }

    @Override
    public NashornUrlTextField createUrlTextField(MarkupContainer container, String id, UrlValidator validator) {
        NashornUrlTextField object = new NashornUrlTextField(id, createPropertyModel(this.pageModel, id), validator);
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
    public NashornLink createLink(String id, IModel<Map<String, Object>> model) {
        return createLink(container, id, model);
    }

    @Override
    public NashornLink createLink(MarkupContainer container, String id, IModel<Map<String, Object>> model) {
        NashornLink object = new NashornLink(id, model);
        object.setScript(this.script);
        object.setDisk(this.disk);
        container.add(object);
        this.children.put(id, object);
        return object;
    }

    @Override
    public NashornLink createLink(String id) {
        return createLink(id, new MapModel<>(this.pageModel));
    }

    @Override
    public NashornLink createLink(MarkupContainer container, String id) {
        return createLink(container, id, new MapModel<>(this.pageModel));
    }

    @Override
    public BlockPanel createBlock(String id, String code) {
        return createBlock(container, id, code);
    }

    @Override
    public BlockPanel createBlock(MarkupContainer container, String id, String code) {
        Map<String, Object> blockModel = new HashMap<>();
        pageModel.put(id, blockModel);
        MapModel<String, Object> model = new MapModel<>(blockModel);
        BlockPanel object = new BlockPanel(id, code, this.stage, this.pageModel, model);
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
}