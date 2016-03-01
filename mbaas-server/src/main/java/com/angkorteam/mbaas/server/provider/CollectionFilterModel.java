package com.angkorteam.mbaas.server.provider;

import com.angkorteam.framework.extension.share.model.FilterModel;

/**
 * Created by socheat on 3/2/16.
 */
public class CollectionFilterModel implements FilterModel {

    private String collectionId;

    private String name;

    private String count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }
}
