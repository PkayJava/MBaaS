package com.angkorteam.mbaas.plain.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Khauv Socheat on 2/6/2016.
 */
public class UploadFileRequest extends Request {

    @Expose
    @SerializedName("fileContent")
    private byte[] fileContent;

    @Expose
    @SerializedName("fileName")
    private String fileName;

    @Expose
    @SerializedName("fileType")
    private String fileType;

    @Expose
    @SerializedName("attachedData")
    private String attachedData;

    @Expose
    @SerializedName("acl")
    private String acl;

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

    public String getAttachedData() {
        return attachedData;
    }

    public void setAttachedData(String attachedData) {
        this.attachedData = attachedData;
    }

    public String getAcl() {
        return acl;
    }

    public void setAcl(String acl) {
        this.acl = acl;
    }
}
