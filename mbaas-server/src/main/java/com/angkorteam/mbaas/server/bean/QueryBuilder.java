package com.angkorteam.mbaas.server.bean;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by socheat on 11/24/16.
 */
public class QueryBuilder {

    private List<String> select = Lists.newArrayList();
    private String from;
    private List<String> join = Lists.newArrayList();
    private List<String> where = Lists.newArrayList();
    private List<String> orderBy = Lists.newArrayList();
    private List<String> having = Lists.newArrayList();
    private List<String> groupBy = Lists.newArrayList();
    private Long offset;
    private Long number;

    public void addSelect(String field) {
        this.select.add(field);
    }

    public void addJoin(String table) {
        this.join.add(table);
    }

    public void addWhere(String field) {
        this.where.add(field);
    }

    public void addOrderBy(String field) {
        this.orderBy.add(field);
    }

    public void addGroupBy(String field) {
        this.groupBy.add(field);
    }

    public void addHaving(String field) {
        this.having.add(field);
    }

    public void setLimit(long offset, long number) {
        this.offset = offset;
        this.number = number;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String toSQL() {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        if (this.select.isEmpty()) {
            this.select.add("*");
        }
        builder.append(StringUtils.join(this.select, ", "));
        builder.append(" FROM ");
        builder.append(this.from).append(" ");
        builder.append(StringUtils.join(this.join, " "));
        if (!this.where.isEmpty()) {
            builder.append(" WHERE ").append(StringUtils.join(this.where, " AND "));
        }
        if (!this.groupBy.isEmpty()) {
            builder.append(" GROUP BY ").append(StringUtils.join(this.groupBy, ", "));
        }
        if (!this.having.isEmpty()) {
            builder.append(" HAVING ").append(StringUtils.join(this.having, " AND "));
        }
        if (!this.orderBy.isEmpty()) {
            builder.append(" ORDER BY ").append(StringUtils.join(this.orderBy, ", "));
        }
        if (this.offset != null && this.number != null) {
            builder.append(" LIMIT " + this.offset + "," + this.number);
        }
        return builder.toString();
    }
}