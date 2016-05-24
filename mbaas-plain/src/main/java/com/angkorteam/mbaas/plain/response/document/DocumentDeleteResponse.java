package com.angkorteam.mbaas.plain.response.document;

import com.angkorteam.mbaas.plain.response.Response;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 2/21/16.
 */
public class DocumentDeleteResponse extends Response<DocumentDeleteResponse.Body> {

    public DocumentDeleteResponse() {
        this.data = new Body();
    }

    public static class Body {

        @Expose
        @SerializedName("documentId")
        private String documentId;

        public String getDocumentId() {
            return documentId;
        }

        public void setDocumentId(String documentId) {
            this.documentId = documentId;
        }
    }
}
