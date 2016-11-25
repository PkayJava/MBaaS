package com.angkorteam.mbaas.server.bean;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by socheat on 11/25/16.
 */
public class InsertBuilder {

    private String table;

    private List<String> values = Lists.newArrayList();

    private List<String> fields = Lists.newArrayList();

    private List<String> wheres = Lists.newArrayList();

    public void addField(String name) {
        this.fields.add(name);
        this.values.add(":" + name);
    }

    public void intoTable(String table) {
        this.table = table;
    }

    public String toSQL() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("INSERT INTO ").append(this.table);
        buffer.append("(").append(StringUtils.join(this.fields, ", ")).append(")");
        buffer.append(" ").append("VALUES").append("(").append(StringUtils.join(this.values, ", ")).append(")");
        return buffer.toString();
    }

}
