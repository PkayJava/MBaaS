package com.angkorteam.mbaas.plain.request.javascript;

import com.angkorteam.mbaas.plain.request.Request;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 2/27/16.
 */
public class JavaScriptExecuteRequest extends Request {

    @Expose
    @SerializedName("body")
    private Map<String, Object> body = new HashMap<>();

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }
}
