package com.angkorteam.mbaas.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 2/4/16.
 */
public class SuccessResponse<T> extends Response {

    @Expose
    @SerializedName("http_code")
    private int httpCode;

    @Expose
    @SerializedName("data")
    private T data;

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
