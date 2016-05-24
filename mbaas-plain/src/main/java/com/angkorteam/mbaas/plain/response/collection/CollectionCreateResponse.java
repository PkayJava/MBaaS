package com.angkorteam.mbaas.plain.response.collection;

import com.angkorteam.mbaas.plain.response.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 2/16/16.
 */
public class CollectionCreateResponse extends Response<CollectionCreateResponse.Data> {

    public CollectionCreateResponse() {
        this.data = new Data();
    }

    public static class Data {

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
