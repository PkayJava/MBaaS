package com.angkorteam.mbaas.plain.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Khauv Socheat on 2/7/2016.
 */
public class CreateAssetRequest extends Request {

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("meta")
    private String meta;

    @Expose
    @SerializedName("fileContent")
    private byte[] fileContent;

    @Expose
    @SerializedName("fileName")
    private String fileName;

    @Expose
    @SerializedName("fileType")
    private String fileType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
