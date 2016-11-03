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
    @SerializedName("type")
    private String type;

    @Expose
    @SerializedName("length")
    private int length;

    @Expose
    @SerializedName("index")
    private String index;

    @Expose
    @SerializedName("order")
    private int order;

    @Expose
    @SerializedName("precision")
    private int precision;

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

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
