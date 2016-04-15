package com.angkorteam.mbaas.plain.response.javascript;

import com.angkorteam.mbaas.plain.response.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 2/27/16.
 */
public class JavaScriptExecuteResponse extends Response<Object> {

    public JavaScriptExecuteResponse() {
        this.data = new Object();
    }

}
