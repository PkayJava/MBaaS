package com.angkorteam.mbaas.plain.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 2/18/16.
 */
public class MonitorMemResponse extends Response<MonitorMemResponse.Body> {

    public MonitorMemResponse() {
        this.data = new Body();
    }

    public static class Body {

        @Expose
        @SerializedName("total")
        private String total;
        @Expose
        @SerializedName("ram")
        private long ram = 0l;

        @Expose
        @SerializedName("used")
        private String used;

        @Expose
        @SerializedName("free")
        private String free;

        @Expose
        @SerializedName("actualUsed")
        private String actualUsed;

        @Expose
        @SerializedName("actualFree")
        private String actualFree;

        @Expose
        @SerializedName("usedPercent")
        private double usedPercent = 0.0d;

        @Expose
        @SerializedName("freePercent")
        private double freePercent = 0.0d;

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public long getRam() {
            return ram;
        }

        public void setRam(long ram) {
            this.ram = ram;
        }

        public String getUsed() {
            return used;
        }

        public void setUsed(String used) {
            this.used = used;
        }

        public String getFree() {
            return free;
        }

        public void setFree(String free) {
            this.free = free;
        }

        public String getActualUsed() {
            return actualUsed;
        }

        public void setActualUsed(String actualUsed) {
            this.actualUsed = actualUsed;
        }

        public String getActualFree() {
            return actualFree;
        }

        public void setActualFree(String actualFree) {
            this.actualFree = actualFree;
        }

        public double getUsedPercent() {
            return usedPercent;
        }

        public void setUsedPercent(double usedPercent) {
            this.usedPercent = usedPercent;
        }

        public double getFreePercent() {
            return freePercent;
        }

        public void setFreePercent(double freePercent) {
            this.freePercent = freePercent;
        }
    }
}
