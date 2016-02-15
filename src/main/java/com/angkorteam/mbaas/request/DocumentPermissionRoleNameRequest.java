package com.angkorteam.mbaas.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Khauv Socheat on 2/15/2016.
 */
public class DocumentPermissionRoleNameRequest extends Request {

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
    @SerializedName("roleName")
    private String roleName;

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
