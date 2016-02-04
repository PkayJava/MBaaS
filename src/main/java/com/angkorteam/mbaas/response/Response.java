package com.angkorteam.mbaas.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by socheat on 2/4/16.
 */
public class Response implements Serializable {

    @Expose
    @SerializedName("result")
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
