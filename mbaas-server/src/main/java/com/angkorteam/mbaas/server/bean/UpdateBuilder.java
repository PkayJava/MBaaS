package com.angkorteam.mbaas.server.bean;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by socheatkhauv on 12/22/16.
 */
public class UpdateBuilder {

    private final String table;

    private List<String> where = Lists.newArrayList();

    private List<String> field = Lists.newArrayList();

    public UpdateBuilder(String table) {
        this.table = table;
    }

    public void addField(String field) {
        this.field.add(field);
    }

    public void addWhere(String filter) {
        this.where.add(filter);
    }

    public String toSQL() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("UPDATE  ").append(this.table);
        buffer.append(" SET ").append(StringUtils.join(this.field, ", "));
        if (!this.where.isEmpty()) {
            buffer.append(" WHERE " + StringUtils.join(this.where, " AND "));
        }
        return buffer.toString();
    }
}
