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
    @SerializedName("resultMessage")
    private String resultMessage;

    @Expose
    @SerializedName("resultCode")
    private Integer resultCode;

    @Expose
    @SerializedName("requestHeader")
    private Map<String, List<String>> requestHeader = new LinkedHashMap<>();

    @Expose
    @SerializedName("requestQueryParameterErrors")
    private Map<String, String> requestQueryParameterErrors = null;

    @Expose
    @SerializedName("requestHeaderErrors")
    private Map<String, String> requestHeaderErrors = null;

    @Expose
    @SerializedName("requestBodyErrors")
    private Map<String, Object> requestBodyErrors = null;

    @Expose
    @SerializedName("businessErrors")
    private Map<String, Object> businessErrors = null;

    @Expose
    @SerializedName("responseHeaderErrors")
    private Map<String, String> responseHeaderErrors = null;

    @Expose
    @SerializedName("responseBodyErrors")
    private Map<String, Object> responseBodyErrors = null;

    @Expose
    @SerializedName("stackTrace")
    private List<String> stackTrace = null;

    @Expose
    @SerializedName("debugMessage")
    private String debugMessage;

    @Expose
    @SerializedName("method")
    private String method;

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

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public Map<String, String> getRequestQueryParameterErrors() {
        return requestQueryParameterErrors;
    }

    public void setRequestQueryParameterErrors(Map<String, String> requestQueryParameterErrors) {
        this.requestQueryParameterErrors = requestQueryParameterErrors;
    }

    public Map<String, Object> getRequestBodyErrors() {
        return requestBodyErrors;
    }

    public List<String> getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(List<String> stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public void setDebugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
    }

    public Map<String, Object> getResponseBodyErrors() {
        return responseBodyErrors;
    }

    public void setResponseBodyErrors(Map<String, Object> responseBodyErrors) {
        this.responseBodyErrors = responseBodyErrors;
    }

    public void setRequestBodyErrors(Map<String, Object> requestBodyErrors) {
        this.requestBodyErrors = requestBodyErrors;
    }

    public Map<String, String> getRequestHeaderErrors() {
        return requestHeaderErrors;
    }

    public void setRequestHeaderErrors(Map<String, String> requestHeaderErrors) {
        this.requestHeaderErrors = requestHeaderErrors;
    }

    public Map<String, String> getResponseHeaderErrors() {
        return responseHeaderErrors;
    }

    public void setResponseHeaderErrors(Map<String, String> responseHeaderErrors) {
        this.responseHeaderErrors = responseHeaderErrors;
    }

    public Map<String, Object> getBusinessErrors() {
        return businessErrors;
    }

    public void setBusinessErrors(Map<String, Object> businessErrors) {
        this.businessErrors = businessErrors;
    }
}
