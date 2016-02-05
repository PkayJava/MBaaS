package com.angkorteam.mbaas.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by Khauv Socheat on 2/5/2016.
 */
public class CreateDocumentRequest extends Request {

    @Expose
    @SerializedName("document")
    private Map<String, Object> document;

    public Map<String, Object> getDocument() {
        return document;
    }

    public void setDocument(Map<String, Object> document) {
        this.document = document;
    }
}
