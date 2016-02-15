package com.angkorteam.mbaas.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Khauv Socheat on 2/15/2016.
 */
public class DocumentPermissionUsernameRequest extends Request {

    @Expose
    @SerializedName("collection")
    private String collection;

    @Expose
    @SerializedName("id")
    private Integer id;

    @Expose
    @SerializedName("action")
    private Integer action;

    @Expose
    @SerializedName("username")
    private String username;

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
