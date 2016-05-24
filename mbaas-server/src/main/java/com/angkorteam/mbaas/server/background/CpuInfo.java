package com.angkorteam.mbaas.server.background;

/**
 * Created by Khauv Socheat on 4/21/2016.
 */
public class CpuInfo {

    private Double user;

    private Double system;

    private Double iowait;

    private Double nice;

    private Double steal;

    private Double idle;

    public Double getUser() {
        return user;
    }

    public void setUser(Double user) {
        this.user = user;
    }

    public Double getSystem() {
        return system;
    }

    public void setSystem(Double system) {
        this.system = system;
    }

    public Double getIowait() {
        return iowait;
    }

    public void setIowait(Double iowait) {
        this.iowait = iowait;
    }

    public Double getSteal() {
        return steal;
    }

    public void setSteal(Double steal) {
        this.steal = steal;
    }

    public Double getIdle() {
        return idle;
    }

    public void setIdle(Double idle) {
        this.idle = idle;
    }

    public Double getNice() {
        return nice;
    }

    public void setNice(Double nice) {
        this.nice = nice;
    }
}
