package com.angkorteam.mbaas.plain.request.collection;

import com.angkorteam.mbaas.plain.request.Request;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Khauv Socheat on 2/12/2016.
 */
public class CollectionDeleteRequest extends Request {

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
