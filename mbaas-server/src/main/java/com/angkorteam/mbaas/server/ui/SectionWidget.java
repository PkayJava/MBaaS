package com.angkorteam.mbaas.server.ui;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.MenuItemTable;
import com.angkorteam.mbaas.model.entity.tables.MenuTable;
import com.angkorteam.mbaas.model.entity.tables.SectionTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.MenuItemPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.MenuPojo;
import com.angkorteam.mbaas.server.Spring;
import com.google.common.collect.Lists;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

import java.util.Collections;
import java.util.List;

/**
 * Created by socheat on 10/23/16.
 */
public class SectionWidget extends Panel {

    private String sectionId;

    private boolean access = false;

    public SectionWidget(String id, String sectionId) {
        super(id);
        this.sectionId = sectionId;
        this.access = false;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = Spring.getBean(DSLContext.class);
        SectionTable sectionTable = Tables.SECTION.as("sectionTable");
        String sectionTitle = context.select(sectionTable.TITLE).from(sectionTable).where(sectionTable.SECTION_ID.eq(this.sectionId)).fetchOneInto(String.class);
        Label label = new Label("sectionTitle", sectionTitle);
        add(label);

        MenuTable menuTable = Tables.MENU.as("menuTable");
        MenuItemTable menuItemTable = Tables.MENU_ITEM.as("menuItemTable");
        List<MenuPojo> menuPojos = context.select(menuTable.fields()).from(menuTable).where(menuTable.SECTION_ID.eq(this.sectionId)).orderBy(menuTable.ORDER.asc()).fetchInto(MenuPojo.class);
        List<MenuItemPojo> menuItemPojos = context.select(menuItemTable.fields()).from(menuItemTable).where(menuItemTable.SECTION_ID.eq(this.sectionId)).orderBy(menuItemTable.ORDER.asc()).fetchInto(MenuItemPojo.class);
        List<Object> items = Lists.newArrayList();
        items.addAll(menuPojos);
        items.addAll(menuItemPojos);
        Collections.sort(items, new Comparator());

        RepeatingView itemWidgets = new RepeatingView("itemWidgets", new PropertyModel<Boolean>(this, "access"));
        for (Object item : items) {
            if (item instanceof MenuPojo) {
                MenuWidget itemWidget = new MenuWidget(itemWidgets.newChildId(), ((MenuPojo) item).getMenuId());
                itemWidgets.add(itemWidget);
            } else if (item instanceof MenuItemPojo) {
                MenuItemWidget itemWidget = new MenuItemWidget(itemWidgets.newChildId(), ((MenuItemPojo) item).getMenuItemId());
                itemWidgets.add(itemWidget);
            }
        }
        add(itemWidgets);
    }

    @Override
    protected void onBeforeRender() {
        setVisible(this.access);
        super.onBeforeRender();
    }

    public static class Comparator implements java.util.Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            Integer io1 = o1 instanceof MenuPojo ? ((MenuPojo) o1).getOrder() : ((MenuItemPojo) o1).getOrder();
            Integer io2 = o2 instanceof MenuPojo ? ((MenuPojo) o2).getOrder() : ((MenuItemPojo) o2).getOrder();
            return io1.compareTo(io2);
        }

    }


}
