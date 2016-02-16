package com.angkorteam.mbaas.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khauv Socheat on 2/12/2016.
 */
public class CollectionCreateRequest extends Request {

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("attributes")
    private List<Attribute> attributes = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        @SerializedName("javaType")
        private String javaType;

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

        public String getJavaType() {
            return javaType;
        }

        public void setJavaType(String javaType) {
            this.javaType = javaType;
        }
    }
}
