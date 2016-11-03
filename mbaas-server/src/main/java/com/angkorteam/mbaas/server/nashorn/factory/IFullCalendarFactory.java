//package com.angkorteam.mbaas.server.nashorn.factory;
//
//import com.angkorteam.framework.extension.wicket.markup.html.FullCalendar;
//import com.angkorteam.framework.extension.wicket.markup.html.FullCalendarItem;
//import jdk.nashorn.api.scripting.ScriptObjectMirror;
//import org.apache.wicket.MarkupContainer;
//
//import java.io.Serializable;
//import java.util.Date;
//import java.util.Map;
//
///**
// * Created by socheat on 6/17/16.
// */
//public interface IFullCalendarFactory extends Serializable {
//
//    FullCalendar createFullCalendar(String id);
//
//    FullCalendar createFullCalendar(MarkupContainer container, String id);
//
//    FullCalendarItem createFullCalendarItem(String id, String title, Date start, Date end);
//
//    String createAddressLink(String pageCode);
//
//    String createAddressLink(String pageCode, ScriptObjectMirror params);
//
//    String createAddressLink(String pageCode, Map<String, Object> params);
//
//}
