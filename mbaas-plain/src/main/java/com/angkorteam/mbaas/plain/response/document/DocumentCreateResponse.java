package com.angkorteam.mbaas.plain.response.document;

import com.angkorteam.mbaas.plain.response.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by socheat on 2/17/16.
 */
public class DocumentCreateResponse extends Response<DocumentCreateResponse.Body> {

    public DocumentCreateResponse() {
        this.data = new Body();
    }

    public static class Body {

        @Expose
        @SerializedName("collectionName")
        private String collectionName;

        @Expose
        @SerializedName("documentId")
        private String documentId;

        @Expose
        @SerializedName("attributes")
        private Map<String, Object> attributes = new LinkedHashMap<>();

        public String getCollectionName() {
            return collectionName;
        }

        public void setCollectionName(String collectionName) {
            this.collectionName = collectionName;
        }

        public Map<String, Object> getAttributes() {
            return attributes;
        }

        public void setAttributes(Map<String, Object> attributes) {
            this.attributes = attributes;
        }

        public String getDocumentId() {
            return documentId;
        }

        public void setDocumentId(String documentId) {
            this.documentId = documentId;
        }
    }
}
