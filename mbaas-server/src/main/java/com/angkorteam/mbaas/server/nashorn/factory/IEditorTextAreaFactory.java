package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.framework.extension.wicket.markup.html.form.CKEditorTextArea;
import org.apache.wicket.MarkupContainer;

import java.io.Serializable;

/**
 * Created by socheat on 6/26/16.
 */
public interface IEditorTextAreaFactory extends Serializable {

    CKEditorTextArea createEditorTextArea(String id);

    CKEditorTextArea createEditorTextArea(MarkupContainer container, String id);
    
}
