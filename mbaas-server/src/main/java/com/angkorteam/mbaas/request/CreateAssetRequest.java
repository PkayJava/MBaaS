package com.angkorteam.mbaas.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.springframework.web.multipart.MultipartFile;

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
    @SerializedName("file")
    private MultipartFile file;

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

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
