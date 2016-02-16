package com.angkorteam.mbaas.plain.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Khauv Socheat on 2/15/2016.
 */
public class DocumentPermissionRoleNameRequest extends Request {

    @Expose
    @SerializedName("collection")
    private String collection;

    @Expose
    @SerializedName("documentId")
    private Integer documentId;

    @Expose
    @SerializedName("actions")
    private List<Integer> actions = new LinkedList<>();

    @Expose
    @SerializedName("roleName")
    private String roleName;

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public Integer getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Integer documentId) {
        this.documentId = documentId;
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
