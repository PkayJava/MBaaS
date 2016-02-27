package com.angkorteam.mbaas.plain.response.script;

import com.angkorteam.mbaas.plain.response.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by socheat on 2/27/16.
 */
public class ScriptExecuteResponse extends Response<ScriptExecuteResponse.Body> {

    public ScriptExecuteResponse() {
        this.data = new Body();
    }

    public static class Body {

        @Expose
        @SerializedName("script")
        private String script;

        @Expose
        @SerializedName("body")
        private Map<String, Object> body = new LinkedHashMap<>();

        public Map<String, Object> getBody() {
            return body;
        }

        public void setBody(Map<String, Object> body) {
            this.body = body;
        }

        public String getScript() {
            return script;
        }

        public void setScript(String script) {
            this.script = script;
        }
    }

}
