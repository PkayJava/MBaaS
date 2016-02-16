package com.angkorteam.mbaas.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Khauv Socheat on 2/13/2016.
 */
public class CollectionAttributeCreateRequest extends Request {

    @Expose
    @SerializedName("collection")
    private String collection;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("nullable")
    private boolean nullable = true;

    @Expose
    @SerializedName("javaType")
    private String javaType;

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJavaType() {
        return javaType;
    }

    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }
}
