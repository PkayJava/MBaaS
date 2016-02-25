package com.angkorteam.mbaas.plain.request.collection;

import com.angkorteam.mbaas.plain.request.Request;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Khauv Socheat on 2/13/2016.
 */
public class CollectionAttributeCreateRequest extends Request {

    @Expose
    @SerializedName("collectionName")
    private String collectionName;

    @Expose
    @SerializedName("attributeName")
    private String attributeName;

    @Expose
    @SerializedName("nullable")
    private boolean nullable = true;

    @Expose
    @SerializedName("javaType")
    private String javaType;

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
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
