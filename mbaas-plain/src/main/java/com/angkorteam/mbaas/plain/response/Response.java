package com.angkorteam.mbaas.plain.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 2/4/16.
 */
public abstract class Response<T> implements Serializable {

    @Expose
    @SerializedName("result")
    private String result;

    @Expose
    @SerializedName("http_code")
    private Integer httpCode;

    @Expose
    @SerializedName("error_messages")
    private Map<String, String> errorMessages = new LinkedHashMap<>();

    @Expose
    @SerializedName("method")
    private String method;

    @Expose
    @SerializedName("request_header")
    private Map<String, List<String>> requestHeader = new LinkedHashMap<>();

    @Expose
    @SerializedName("version")
    private String version;

    @Expose
    @SerializedName("data")
    protected T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setHttpCode(Integer httpCode) {
        this.httpCode = httpCode;
    }

    public Map<String, String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(Map<String, String> errorMessages) {
        this.errorMessages = errorMessages;
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

    public Integer getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
