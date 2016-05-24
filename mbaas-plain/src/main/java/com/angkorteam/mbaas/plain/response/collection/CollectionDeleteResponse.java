package com.angkorteam.mbaas.plain.response.collection;

import com.angkorteam.mbaas.plain.response.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 2/17/16.
 */
public class CollectionDeleteResponse extends Response<CollectionDeleteResponse.Body> {

    public CollectionDeleteResponse() {
        this.data = new Body();
    }

    public static class Body {

        @Expose
        @SerializedName("collectionName")
        private String collectionName;

        public String getCollectionName() {
            return collectionName;
        }

        public void setCollectionName(String collectionName) {
            this.collectionName = collectionName;
        }
    }
}
