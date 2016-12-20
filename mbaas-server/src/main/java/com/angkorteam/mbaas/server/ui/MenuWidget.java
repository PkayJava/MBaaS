package com.angkorteam.mbaas.server.ui;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.MenuItemTable;
import com.angkorteam.mbaas.model.entity.tables.MenuTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.MenuItemPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.MenuPojo;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.google.common.collect.Lists;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
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
public class MenuWidget extends Panel {

    private String menuId;

    private List<Object> items;

    private String cssClass;

    private WebMarkupContainer menuContainer;

    private boolean access = false;

    public MenuWidget(String id, String menuId) {
        super(id);
        this.menuId = menuId;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.menuContainer = new WebMarkupContainer("menuContainer");
        this.menuContainer.add(AttributeModifier.replace("class", new PropertyModel<>(this, "cssClass")));
        add(this.menuContainer);
        DSLContext context = Spring.getBean(DSLContext.class);
        MenuTable menuTable = Tables.MENU.as("menuTable");
        MenuPojo menuPojo = context.select(menuTable.fields()).from(menuTable).where(menuTable.MENU_ID.eq(this.menuId)).fetchOneInto(MenuPojo.class);
        WebMarkupContainer menuIcon = new WebMarkupContainer("menuIcon");
        menuIcon.add(AttributeModifier.replace("class", "fa " + menuPojo.getIcon()));
        this.menuContainer.add(menuIcon);
        Label menuTitle = new Label("menuTitle", menuPojo.getTitle());
        this.menuContainer.add(menuTitle);

        MenuItemTable menuItemTable = Tables.MENU_ITEM.as("menuItemTable");

        List<MenuPojo> menuPojos = context.select(menuTable.fields()).from(menuTable).where(menuTable.PARENT_MENU_ID.eq(this.menuId)).orderBy(menuTable.ORDER.asc()).fetchInto(MenuPojo.class);
        List<MenuItemPojo> menuItemPojos = context.select(menuItemTable.fields()).from(menuItemTable).where(menuItemTable.MENU_ID.eq(this.menuId)).orderBy(menuItemTable.ORDER.asc()).fetchInto(MenuItemPojo.class);

        this.items = Lists.newArrayList();
        this.items.addAll(menuPojos);
        this.items.addAll(menuItemPojos);
        Collections.sort(items, new SectionWidget.Comparator());

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
        this.menuContainer.add(itemWidgets);

        setVisible(!this.items.isEmpty());
    }

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = this.access || access;
        PropertyModel<Boolean> model = (PropertyModel<Boolean>) getParent().getDefaultModel();
        model.setObject(access);
    }

    @Override
    protected void onBeforeRender() {
        setVisible(this.access);
        MBaaSPage mBaaSPage = (MBaaSPage) getPage();
        this.cssClass = mBaaSPage.isMenuWidgetSelected(this.menuId) ? "treeview active" : "treeview";
        super.onBeforeRender();
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
