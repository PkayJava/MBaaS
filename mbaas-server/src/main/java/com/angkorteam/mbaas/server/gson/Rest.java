package com.angkorteam.mbaas.server.gson;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by socheat on 11/17/16.
 */
public class Rest {

    @Expose
    @SerializedName("restId")
    private String restId;

    private String javaClass;

    @Expose
    @SerializedName("groovyPath")
    private String groovyPath;

    @Expose
    @SerializedName("clientGroovy")
    private String clientGroovy;

    @Expose
    @SerializedName("clientGroovyCrc32")
    private String clientGroovyCrc32;

    @Expose
    @SerializedName("serverGroovy")
    private String serverGroovy;

    @Expose
    @SerializedName("serverGroovyCrc32")
    private String serverGroovyCrc32;

    @Expose
    @SerializedName("groovyConflicted")
    private Boolean groovyConflicted;

    public String getRestId() {
        return restId;
    }

    public void setRestId(String restId) {
        this.restId = restId;
    }

    public boolean isGroovyConflicted() {
        return groovyConflicted != null && groovyConflicted;
    }

    public void setGroovyConflicted(boolean groovyConflicted) {
        this.groovyConflicted = groovyConflicted;
    }

    public String getClientGroovy() {
        return clientGroovy;
    }

    public void setClientGroovy(String clientGroovy) {
        this.clientGroovy = clientGroovy;
    }

    public String getClientGroovyCrc32() {
        return clientGroovyCrc32;
    }

    public void setClientGroovyCrc32(String clientGroovyCrc32) {
        this.clientGroovyCrc32 = clientGroovyCrc32;
    }

    public String getServerGroovy() {
        return serverGroovy;
    }

    public void setServerGroovy(String serverGroovy) {
        this.serverGroovy = serverGroovy;
    }

    public String getServerGroovyCrc32() {
        return serverGroovyCrc32;
    }

    public void setServerGroovyCrc32(String serverGroovyCrc32) {
        this.serverGroovyCrc32 = serverGroovyCrc32;
    }

    public String getGroovyPath() {
        return groovyPath;
    }

    public void setGroovyPath(String groovyPath) {
        this.groovyPath = groovyPath;
    }

    public String getJavaClass() {
        return javaClass;
    }

    public void setJavaClass(String javaClass) {
        this.javaClass = javaClass;
    }
}
