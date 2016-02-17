package com.angkorteam.mbaas.plain.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 2/17/16.
 */
public class CollectionAttributeDeleteResponse extends Response<CollectionAttributeDeleteResponse.Body> {

    public CollectionAttributeDeleteResponse() {
        this.data = new Body();
    }

    public static class Body {

        @Expose
        @SerializedName("collectionName")
        private String collectionName;

        @Expose
        @SerializedName("attributeName")
        private String attributeName;

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
    }
}
