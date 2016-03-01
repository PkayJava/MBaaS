package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.model.ItemModel;

/**
 * Created by socheat on 3/2/16.
 */
public class CollectionItemModel implements ItemModel {

    private String collectionId;

    private String name;

    private Integer count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }
}
