package com.angkorteam.mbaas.plain.response.document;

import com.angkorteam.mbaas.plain.response.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by socheat on 2/21/16.
 */
public class DocumentRetrieveResponse extends Response<DocumentRetrieveResponse.Body> {

    public DocumentRetrieveResponse() {
        this.data = new Body();
    }

    public static class Body {

        @Expose
        @SerializedName("optimistic")
        private Integer optimistic;

        @Expose
        @SerializedName("collectionName")
        private String collectionName;

        @Expose
        @SerializedName("documentId")
        private String documentId;

        @Expose
        @SerializedName("ownerUserId")
        private String ownerUserId;

        @Expose
        @SerializedName("document")
        private Map<String, Object> document = new LinkedHashMap<>();

        @Expose
        @SerializedName("deleted")
        private Boolean deleted;

        public Boolean getDeleted() {
            return deleted;
        }

        public void setDeleted(Boolean deleted) {
            this.deleted = deleted;
        }

        public Integer getOptimistic() {
            return optimistic;
        }

        public void setOptimistic(Integer optimistic) {
            this.optimistic = optimistic;
        }

        public String getCollectionName() {
            return collectionName;
        }

        public void setCollectionName(String collectionName) {
            this.collectionName = collectionName;
        }

        public String getDocumentId() {
            return documentId;
        }

        public void setDocumentId(String documentId) {
            this.documentId = documentId;
        }

        public String getOwnerUserId() {
            return ownerUserId;
        }

        public void setOwnerUserId(String ownerUserId) {
            this.ownerUserId = ownerUserId;
        }

        public Map<String, Object> getDocument() {
            return document;
        }

        public void setDocument(Map<String, Object> document) {
            this.document = document;
        }
    }
}
