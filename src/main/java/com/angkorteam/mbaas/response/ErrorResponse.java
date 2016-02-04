package com.angkorteam.mbaas.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by socheat on 2/4/16.
 */
public class ErrorResponse extends Response {

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("method")
    private String method;

    @Expose
    @SerializedName("request_header")
    private Map<String, String[]> requestHeader;

    @Expose
    @SerializedName("version")
    private String version;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String[]> getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(Map<String, String[]> requestHeader) {
        this.requestHeader = requestHeader;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
