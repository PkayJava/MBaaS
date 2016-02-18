package com.angkorteam.mbaas.plain.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by socheat on 2/18/16.
 */
public class MonitorCpuResponse extends Response<List<MonitorCpuResponse.Body>> {

    public MonitorCpuResponse() {
        this.data = new LinkedList<>();
    }

    public static class Body {

        @Expose
        @SerializedName("vendor")
        private String vendor = null;

        @Expose
        @SerializedName("model")
        private String model = null;

        @Expose
        @SerializedName("mhz")
        private int mhz = 0;

        @Expose
        @SerializedName("mhzMax")
        private int mhzMax = 0;

        @Expose
        @SerializedName("mhzMin")
        private int mhzMin = 0;

        @Expose
        @SerializedName("cacheSize")
        private long cacheSize = 0L;

        @Expose
        @SerializedName("totalCores")
        private int totalCores = 0;

        @Expose
        @SerializedName("totalSockets")
        private int totalSockets = 0;

        @Expose
        @SerializedName("coresPerSocket")
        private int coresPerSocket = 0;

        public String getVendor() {
            return vendor;
        }

        public void setVendor(String vendor) {
            this.vendor = vendor;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public int getMhz() {
            return mhz;
        }

        public void setMhz(int mhz) {
            this.mhz = mhz;
        }

        public int getMhzMax() {
            return mhzMax;
        }

        public void setMhzMax(int mhzMax) {
            this.mhzMax = mhzMax;
        }

        public int getMhzMin() {
            return mhzMin;
        }

        public void setMhzMin(int mhzMin) {
            this.mhzMin = mhzMin;
        }

        public long getCacheSize() {
            return cacheSize;
        }

        public void setCacheSize(long cacheSize) {
            this.cacheSize = cacheSize;
        }

        public int getTotalCores() {
            return totalCores;
        }

        public void setTotalCores(int totalCores) {
            this.totalCores = totalCores;
        }

        public int getTotalSockets() {
            return totalSockets;
        }

        public void setTotalSockets(int totalSockets) {
            this.totalSockets = totalSockets;
        }

        public int getCoresPerSocket() {
            return coresPerSocket;
        }

        public void setCoresPerSocket(int coresPerSocket) {
            this.coresPerSocket = coresPerSocket;
        }
    }
}
