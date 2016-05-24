package com.angkorteam.mbaas.plain.request.document;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Khauv Socheat on 2/15/2016.
 */
public class DocumentQueryRequest {

    @Expose
    @SerializedName("query")
    private Query query = new Query();

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    /**
     * Created by socheat on 2/25/16.
     */
    public static class Query implements Serializable {

        @Expose
        @SerializedName("limit")
        private Integer limit = 10;

        @Expose
        @SerializedName("offset")
        private Integer offset = 0;

        @Expose
        @SerializedName("where")
        private List<String> where = new LinkedList<>();

        @Expose
        @SerializedName("params")
        private Map<String, Object> params = new LinkedHashMap<>();

        @Expose
        @SerializedName("orderBy")
        private List<String> orderBy = new LinkedList<>();

        @Expose
        @SerializedName("fields")
        private List<String> fields = new LinkedList<>();

        @Expose
        @SerializedName("groupBy")
        private List<String> groupBy = new LinkedList<>();

        public Integer getLimit() {
            return limit;
        }

        public void setLimit(Integer limit) {
            this.limit = limit;
        }

        public Integer getOffset() {
            return offset;
        }

        public void setOffset(Integer offset) {
            this.offset = offset;
        }

        public List<String> getWhere() {
            return where;
        }

        public void setWhere(List<String> where) {
            this.where = where;
        }

        public Map<String, Object> getParams() {
            return params;
        }

        public void setParams(Map<String, Object> params) {
            this.params = params;
        }

        public List<String> getOrderBy() {
            return orderBy;
        }

        public void setOrderBy(List<String> orderBy) {
            this.orderBy = orderBy;
        }

        public List<String> getFields() {
            return fields;
        }

        public void setFields(List<String> fields) {
            this.fields = fields;
        }

        public List<String> getGroupBy() {
            return groupBy;
        }

        public void setGroupBy(List<String> groupBy) {
            this.groupBy = groupBy;
        }
    }
}
