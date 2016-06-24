package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.link.NashornLink;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by socheat on 6/12/16.
 */
public interface ILinkFactory extends Serializable {

    NashornLink createLink(String id);

    NashornLink createLink(String id, IModel<Map<String, Object>> model);

    NashornLink createLink(MarkupContainer container, String id);

    NashornLink createLink(MarkupContainer container, String id, IModel<Map<String, Object>> model);

    BookmarkablePageLink<Void> createPageLink(String id, String pageCode);

    BookmarkablePageLink<Void> createPageLink(String id, String pageCode, Map<String, Object> params);

    BookmarkablePageLink<Void> createPageLink(String id, String pageCode, ScriptObjectMirror params);

    BookmarkablePageLink<Void> createPageLink(MarkupContainer container, String id, String pageCode);

    BookmarkablePageLink<Void> createPageLink(MarkupContainer container, String id, String pageCode, Map<String, Object> params);

    BookmarkablePageLink<Void> createPageLink(MarkupContainer container, String id, String pageCode, ScriptObjectMirror params);

}
