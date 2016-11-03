package com.angkorteam.mbaas.plain.request.collection;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khauv Socheat on 2/12/2016.
 */
public class CollectionCreateRequest {

    @Expose
    @SerializedName("collectionName")
    private String collectionName;

    @Expose
    @SerializedName("attributes")
    private List<Attribute> attributes = new ArrayList<>();

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public static class Attribute {

        @Expose
        @SerializedName("name")
        private String name;

        @Expose
        @SerializedName("nullable")
        private boolean nullable = true;

        @Expose
        @SerializedName("length")
        private int length;

        @Expose
        @SerializedName("precision")
        private int precision;

        @Expose
        @SerializedName("type")
        private String type;

        @Expose
        @SerializedName("order")
        private int order;

        @Expose
        @SerializedName("index")
        private String index;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isNullable() {
            return nullable;
        }

        public void setNullable(boolean nullable) {
            this.nullable = nullable;
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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }
    }
}
