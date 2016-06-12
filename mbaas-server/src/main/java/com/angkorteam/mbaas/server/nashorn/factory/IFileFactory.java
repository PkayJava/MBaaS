package com.angkorteam.mbaas.server.nashorn.factory;

import org.apache.wicket.markup.html.form.upload.FileUpload;

import java.io.File;
import java.io.Serializable;

/**
 * Created by socheat on 6/12/16.
 */
public interface IFileFactory extends Serializable {

    File createFile(FileUpload fileUpload);

}
