package com.angkorteam.mbaas.plain.request.collection;

import com.angkorteam.mbaas.plain.request.Request;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Khauv Socheat on 2/15/2016.
 */
public class CollectionPermissionRoleNameRequest extends Request {

    @Expose
    @SerializedName("collectionName")
    private String collectionName;

    @Expose
    @SerializedName("actions")
    private List<Integer> actions = new LinkedList<>();

    @Expose
    @SerializedName("roleName")
    private String roleName;

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public List<Integer> getActions() {
        return actions;
    }

    public void setActions(List<Integer> actions) {
        this.actions = actions;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

}
