package com.angkorteam.mbaas.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 2/4/16.
 */
public class Response implements Serializable {

    @Expose
    @SerializedName("result")
    private String result;

    @Expose
    @SerializedName("http_code")
    private int httpCode;

    @Expose
    @SerializedName("data")
    private Object data;

    @Expose
    @SerializedName("error_message")
    private String errorMessage;

    @Expose
    @SerializedName("error_fields")
    private Map<String, List<String>> errorFields = new LinkedHashMap<>();

    @Expose
    @SerializedName("method")
    private String method;

    @Expose
    @SerializedName("request_header")
    private Map<String, List<String>> requestHeader = new LinkedHashMap<>();

    @Expose
    @SerializedName("version")
    private String version;

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, List<String>> getErrorFields() {
        return errorFields;
    }

    public void setErrorFields(Map<String, List<String>> errorFields) {
        this.errorFields = errorFields;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, List<String>> getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(Map<String, List<String>> requestHeader) {
        this.requestHeader = requestHeader;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
