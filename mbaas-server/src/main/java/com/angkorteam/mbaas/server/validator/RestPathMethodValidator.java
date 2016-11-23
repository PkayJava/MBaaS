package com.angkorteam.mbaas.server.validator;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.RestTable;
import com.angkorteam.mbaas.server.Application;
import com.angkorteam.mbaas.server.Spring;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.validation.ValidationError;
import org.jooq.DSLContext;

import java.util.List;

/**
 * Created by socheat on 11/3/16.
 */
public class RestPathMethodValidator extends AbstractFormValidator {

    public static final String PATH = "{s}";

    private String documentId;

    private TextField<String> pathField;

    private DropDownChoice<String> methodField;

    private FormComponent<?>[] components;

    public RestPathMethodValidator(TextField<String> pathField, DropDownChoice<String> methodField) {
        this.pathField = pathField;
        this.methodField = methodField;
        this.components = new FormComponent<?>[]{this.pathField, this.methodField};
    }

    public RestPathMethodValidator(String documentId, TextField<String> pathField, DropDownChoice<String> methodField) {
        this.documentId = documentId;
        this.pathField = pathField;
        this.methodField = methodField;
        this.components = new FormComponent<?>[]{this.pathField, this.methodField};
    }

    @Override
    public FormComponent<?>[] getDependentFormComponents() {
        return this.components;
    }

    @Override
    public void validate(Form<?> form) {
        String path = this.pathField.getConvertedInput();
        String method = this.methodField.getConvertedInput();
        String newPath = null;
        if (!Strings.isNullOrEmpty(path)) {
            List<String> segmentNames = Lists.newArrayList();
            if (!StringUtils.startsWithIgnoreCase(path, "/")
                    || StringUtils.equalsIgnoreCase(path, "/system")
                    || StringUtils.startsWithIgnoreCase(path, "/system/")
                    || StringUtils.equalsIgnoreCase(path, "/resource")
                    || StringUtils.startsWithIgnoreCase(path, "/resource/")
                    || StringUtils.endsWithIgnoreCase(path, "/")
                    || StringUtils.endsWithIgnoreCase(path, "//")) {
                this.pathField.error(new ValidationError("invalid"));
                return;
            }
            if (path.length() > 1) {
                for (int i = 1; i < path.length(); i++) {
                    char ch = path.charAt(i);
                    if (ch == '/' || Application.CURLLY_BRACES.contains(ch) || Application.CHARACTERS.contains(ch) || Application.NUMBERS.contains(ch)) {
                    } else {
                        this.pathField.error(new ValidationError("invalid"));
                        return;
                    }
                }
            }
            String[] segments = StringUtils.split(path, "/");
            List<String> newSegments = Lists.newLinkedList();
            for (String segment : segments) {
                if (!Strings.isNullOrEmpty(segment)) {
                    if (StringUtils.startsWithIgnoreCase(segment, "{") && StringUtils.endsWithIgnoreCase(segment, "}")) {
                        String name = segment.substring(1, segment.length() - 1);
                        if (StringUtils.containsIgnoreCase(name, "{") || StringUtils.containsIgnoreCase(name, "}")) {
                            this.pathField.error(new ValidationError("invalid"));
                            return;
                        } else {
                            if (segmentNames.contains(name)) {
                                this.pathField.error(new ValidationError("invalid"));
                                return;
                            } else {
                                segmentNames.add(name);
                            }
                        }
                        newSegments.add(PATH);
                    } else {
                        if (StringUtils.containsIgnoreCase(segment, "{") || StringUtils.containsIgnoreCase(segment, "}")) {
                            this.pathField.error(new ValidationError("invalid"));
                            return;
                        }
                        newSegments.add(segment);
                    }
                }
            }
            newPath = "/" + StringUtils.join(newSegments, "/");
        }
        if (!Strings.isNullOrEmpty(newPath) && !Strings.isNullOrEmpty(method)) {
            DSLContext context = Spring.getBean(DSLContext.class);
            RestTable table = Tables.REST.as("table");
            int count = 0;
            if (Strings.isNullOrEmpty(this.documentId)) {
                count = context.selectCount().from(table).where(table.PATH_VARIABLE.eq(newPath)).and(table.METHOD.eq(method)).fetchOneInto(int.class);
            } else {
                count = context.selectCount().from(table).where(table.PATH_VARIABLE.eq(newPath)).and(table.METHOD.eq(method)).and(table.REST_ID.notEqual(this.documentId)).fetchOneInto(int.class);
            }
            if (count > 0) {
                this.pathField.error(new ValidationError("duplicated"));
                this.methodField.error(new ValidationError("duplicated"));
            }
        }
    }
}
