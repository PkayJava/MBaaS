package com.angkorteam.mbaas.server.nashorn.factory;

import com.angkorteam.framework.extension.wicket.markup.html.FullCalendar;
import com.angkorteam.framework.extension.wicket.markup.html.FullCalendarItem;
import org.apache.wicket.MarkupContainer;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by socheat on 6/17/16.
 */
public interface IFullCalendarFactory extends Serializable {

    FullCalendar createFullCalendar(String id);

    FullCalendar createFullCalendar(MarkupContainer container, String id);

    FullCalendarItem createFullCalendarItem(String id, String title, Date start, Date end);

}
