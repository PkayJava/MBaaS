package com.angkorteam.mbaas.plain.request.javascript;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 2/27/16.
 */
public class JavaScriptCreateRequest{

    @Expose
    @SerializedName("content")
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
}
