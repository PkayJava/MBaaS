package com.angkorteam.mbaas.elasticsearch;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 9/24/16.
 */
public class ElasticSearchRequest implements Serializable {

    @Expose
    @SerializedName("query")
    private Query query = new Query();

    @Expose
    @SerializedName("from")
    private Long from;

    @Expose
    @SerializedName("size")
    private Long size;

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public static class Query implements Serializable {
        @Expose
        @SerializedName("constant_score")
        private ConstantScore constantScore = new ConstantScore();

        public ConstantScore getConstantScore() {
            return constantScore;
        }

        public void setConstantScore(ConstantScore constantScore) {
            this.constantScore = constantScore;
        }
    }

    public static class ConstantScore implements Serializable {

        @Expose
        @SerializedName("filter")
        private Filter filter = new Filter();

        public Filter getFilter() {
            return filter;
        }

        public void setFilter(Filter filter) {
            this.filter = filter;
        }
    }

    public static class Filter implements Serializable {

        @Expose
        @SerializedName("term")
        private Map<String, Object> term = new HashMap<>();

        public void addTerm(String name, Object value) {
            this.term.put(name, value);
        }

    }
}
