package com.angkorteam.mbaas.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Khauv Socheat on 2/6/2016.
 */
public class UploadFileRequest extends Request {

    @Expose
    @SerializedName("file")
    private MultipartFile file;

    @Expose
    @SerializedName("attachedData")
    private String attachedData;

    @Expose
    @SerializedName("acl")
    private String acl;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
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
