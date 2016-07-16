package com.angkorteam.mbaas.plain.request.collection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Khauv Socheat on 2/13/2016.
 */
public class CollectionAttributeCreateRequest {

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
    @SerializedName("eav")
    private boolean eav = true;

    @Expose
    @SerializedName("attributeType")
    private String attributeType;

    @Expose
    @SerializedName("length")
    private String length;

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public boolean isEav() {
        return eav;
    }

    public void setEav(boolean eav) {
        this.eav = eav;
    }

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

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }
}
