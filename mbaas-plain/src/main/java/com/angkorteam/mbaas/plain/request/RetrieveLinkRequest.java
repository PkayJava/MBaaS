package com.angkorteam.mbaas.plain.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by Khauv Socheat on 2/6/2016.
 */
public class RetrieveLinkRequest extends Request {

    @Expose
    @SerializedName("query")
    private Map<String, Object> query;

    public Map<String, Object> getQuery() {
        return query;
    }

    public void setQuery(Map<String, Object> query) {
        this.query = query;
    }
}
