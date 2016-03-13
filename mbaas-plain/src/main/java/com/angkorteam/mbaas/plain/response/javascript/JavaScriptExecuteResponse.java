package com.angkorteam.mbaas.plain.response.javascript;

import com.angkorteam.mbaas.plain.response.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 2/27/16.
 */
public class JavaScriptExecuteResponse extends Response<JavaScriptExecuteResponse.Body> {

    public JavaScriptExecuteResponse() {
        this.data = new Body();
    }

    public static class Body {

        @Expose
        @SerializedName("script")
        private String script;

        @Expose
        @SerializedName("body")
        private Object body;

        public Object getBody() {
            return body;
        }

        public void setBody(Object body) {
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
