//package com.angkorteam.mbaas.server.nashorn.factory;
//
//import com.angkorteam.mbaas.server.nashorn.wicket.ajax.markup.html.NashornAjaxLink;
//import com.angkorteam.mbaas.server.nashorn.wicket.markup.html.link.NashornLink;
//import jdk.nashorn.api.scripting.ScriptObjectMirror;
//import org.apache.wicket.MarkupContainer;
//import org.apache.wicket.markup.html.link.BookmarkablePageLink;
//
//import java.io.Serializable;
//import java.util.Map;
//
///**
// * Created by socheat on 6/12/16.
// */
//public interface ILinkFactory extends Serializable {
//
//    NashornLink createLink(String id);
//
//    NashornLink createLink(MarkupContainer container, String id);
//
//    NashornAjaxLink createAjaxLink(String id);
//
//    NashornAjaxLink createAjaxLink(MarkupContainer container, String id);
//
//    BookmarkablePageLink<Void> createPageLink(String id, String pageCode);
//
//    BookmarkablePageLink<Void> createPageLink(String id, String pageCode, Map<String, Object> params);
//
//    BookmarkablePageLink<Void> createPageLink(String id, String pageCode, ScriptObjectMirror params);
//
//    BookmarkablePageLink<Void> createPageLink(MarkupContainer container, String id, String pageCode);
//
//    BookmarkablePageLink<Void> createPageLink(MarkupContainer container, String id, String pageCode, Map<String, Object> params);
//
//    BookmarkablePageLink<Void> createPageLink(MarkupContainer container, String id, String pageCode, ScriptObjectMirror params);
//
//}
