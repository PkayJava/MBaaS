package com.angkorteam.mbaas.plain.request.file;

import com.angkorteam.mbaas.plain.request.Request;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by socheat on 4/9/16.
 */
public class FileCreateRequest extends Request {

    @Expose
    @SerializedName("contentType")
    private String contentType;

    @Expose
    @SerializedName("content")
    private byte[] content;

    @Expose
    @SerializedName("attributes")
    private Map<String, Object> attributes = new HashMap<>();

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
