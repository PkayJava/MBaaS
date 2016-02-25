package com.angkorteam.mbaas.plain.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 2/22/16.
 */
public class DocumentQueryResponse extends Response<DocumentQueryResponse.Body> {

    public DocumentQueryResponse() {
        this.data = new Body();
    }

    public static class Body {

        @Expose
        @SerializedName("collectionName")
        private String collectionName;

        @Expose
        @SerializedName("documents")
        private List<Map<String, Object>> documents = new LinkedList<>();

        @Expose
        @SerializedName("total")
        private int total;

        public String getCollectionName() {
            return collectionName;
        }

        public void setCollectionName(String collectionName) {
            this.collectionName = collectionName;
        }

        public List<Map<String, Object>> getDocuments() {
            return documents;
        }

        public void setDocuments(List<Map<String, Object>> documents) {
            this.documents = documents;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
