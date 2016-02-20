package com.angkorteam.mbaas.plain.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 2/20/16.
 */
public class DocumentCountResponse extends Response<DocumentCountResponse.Body> {

    public DocumentCountResponse() {
        this.data = new Body();
    }

    public static class Body {

        @Expose
        @SerializedName("total")
        private long total;

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }
    }
}
