package com.angkorteam.mbaas.server.background;

/**
 * Created by socheat on 4/22/16.
 */
public class MemInfo {

    private Double total;

    private Double used;

    private Double free;

    private String device;

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getUsed() {
        return used;
    }

    public void setUsed(Double used) {
        this.used = used;
    }

    public Double getFree() {
        return free;
    }

    public void setFree(Double free) {
        this.free = free;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }
}
