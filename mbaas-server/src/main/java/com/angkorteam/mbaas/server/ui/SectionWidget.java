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
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.jooq.DSLContext;

import java.util.Collections;
import java.util.List;

/**
 * Created by socheat on 10/23/16.
 */
public class SectionWidget extends Panel {

    private String sectionId;

    private List<Object> items;

    public SectionWidget(String id, String sectionId) {
        super(id);
        this.sectionId = sectionId;
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
        this.items = Lists.newArrayList();
        this.items.addAll(menuPojos);
        this.items.addAll(menuItemPojos);
        Collections.sort(this.items, new Comparator());

        ListView<Object> itemWidgets = new ListView<Object>("itemWidgets", items) {

            @Override
            protected void populateItem(ListItem<Object> item) {
                Object object = item.getModelObject();
                if (object instanceof MenuPojo) {
                    MenuWidget itemWidget = new MenuWidget("itemWidget", ((MenuPojo) object).getMenuId());
                    item.add(itemWidget);
                } else if (object instanceof MenuItemPojo) {
                    MenuItemWidget itemWidget = new MenuItemWidget("itemWidget", ((MenuItemPojo) object).getMenuItemId());
                    item.add(itemWidget);
                }
            }

        };
        add(itemWidgets);

        setVisible(!this.items.isEmpty());
    }

    protected static class Comparator implements java.util.Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            Integer io1 = o1 instanceof MenuPojo ? ((MenuPojo) o1).getOrder() : ((MenuItemPojo) o1).getOrder();
            Integer io2 = o2 instanceof MenuPojo ? ((MenuPojo) o2).getOrder() : ((MenuItemPojo) o2).getOrder();
            return io1.compareTo(io2);
        }

    }


}
