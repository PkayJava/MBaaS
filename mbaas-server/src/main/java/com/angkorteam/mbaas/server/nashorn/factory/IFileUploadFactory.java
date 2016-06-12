package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.upload.NashornFileUpload;
import org.apache.wicket.MarkupContainer;

import java.io.Serializable;

/**
 * Created by socheat on 6/12/16.
 */
public interface IFileUploadFactory extends Serializable {

    NashornFileUpload createFileUpload(String id);

    NashornFileUpload createFileUpload(MarkupContainer container, String id);

}
