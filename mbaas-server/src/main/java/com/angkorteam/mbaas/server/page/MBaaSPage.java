package com.angkorteam.mbaas.server.page;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.MenuItemTable;
import com.angkorteam.mbaas.model.entity.tables.MenuTable;
import com.angkorteam.mbaas.model.entity.tables.PageTable;
import com.angkorteam.mbaas.model.entity.tables.SectionTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.MenuItemPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.MenuPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.PagePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.SectionPojo;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.function.HttpFunction;
import com.angkorteam.mbaas.server.ui.SectionWidget;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by socheat on 10/23/16.
 */
public abstract class MBaaSPage extends AdminLTEPage implements UUIDPage {

    private List<String> breadcrumb;

    private WebMarkupContainer headerContainer;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.breadcrumb = initBreadcrumb();
        this.headerContainer = new WebMarkupContainer("headerContainer");
        add(this.headerContainer);
        String htmlPageTitle = getHtmlPageTitle();
        String htmlPageDescription = getHtmlPageDescription();
        Label headerTitle = new Label("headerTitle", new PropertyModel<>(this, "htmlPageTitle"));
        this.headerContainer.add(headerTitle);
        Label headerDescription = new Label("headerDescription", new PropertyModel<>(this, "htmlPageDescription"));
        this.headerContainer.add(headerDescription);
        this.headerContainer.setVisible(!(Strings.isNullOrEmpty(htmlPageTitle) && Strings.isNullOrEmpty(htmlPageDescription)));

        DSLContext context = Spring.getBean(DSLContext.class);

        SectionTable sectionTable = Tables.SECTION.as("sectionTable");
        List<SectionPojo> sectionPojos = context.select(sectionTable.fields()).from(sectionTable).orderBy(sectionTable.ORDER.asc()).fetchInto(SectionPojo.class);
        ListView<SectionPojo> sectionWidgets = new ListView<SectionPojo>("sectionWidgets", sectionPojos) {

            @Override
            protected void populateItem(ListItem<SectionPojo> item) {
                SectionPojo sectionPojo = item.getModelObject();
                SectionWidget sectionWidget = new SectionWidget("sectionWidget", sectionPojo.getSectionId());
                item.add(sectionWidget);
            }

        };
        add(sectionWidgets);

        BookmarkablePageLink<Void> logoutPage = new BookmarkablePageLink<>("logoutPage", LogoutPage.class);
        add(logoutPage);

    }

    protected List<String> initBreadcrumb() {
        List<String> breadcrumb = Lists.newArrayList();
        MenuItemPojo menuItem = getMenuItem();
        if (menuItem == null) {
            return breadcrumb;
        }
        if (menuItem.getMenuItemId() != null) {
            breadcrumb.add("MenuItemId:" + menuItem.getMenuItemId());
        }
        if (menuItem.getSectionId() != null) {
            breadcrumb.add("SectionId:" + menuItem.getSectionId());
        }
        if (menuItem.getMenuId() != null) {
            breadcrumb.add("MenuId:" + menuItem.getMenuId());
        }

        String menuId = menuItem.getMenuId();
        DSLContext context = Spring.getBean(DSLContext.class);
        MenuTable menuTable = Tables.MENU.as("menuTable");
        while (true) {
            if (menuId == null) {
                break;
            }
            MenuPojo menuPojo = context.select(menuTable.fields()).from(menuTable).where(menuTable.MENU_ID.eq(menuId)).fetchOneInto(MenuPojo.class);
            menuId = menuPojo.getParentMenuId();
            if (menuPojo.getSectionId() != null) {
                breadcrumb.add("SectionId:" + menuPojo.getSectionId());
            }
            if (menuPojo.getParentMenuId() != null) {
                breadcrumb.add("MenuId:" + menuPojo.getParentMenuId());
            }

        }
        return breadcrumb;
    }

    public String getHtmlPageTitle() {
        String pageId = getPageUUID();
        DSLContext context = Spring.getBean(DSLContext.class);
        PageTable pageTable = Tables.PAGE.as("pageTable");
        PagePojo pagePojo = context.select(pageTable.fields()).from(pageTable).where(pageTable.PAGE_ID.eq(pageId)).fetchOneInto(PagePojo.class);
        return pagePojo != null ? pagePojo.getTitle() : "";
    }

    public String getHtmlPageDescription() {
        String pageId = getPageUUID();
        DSLContext context = Spring.getBean(DSLContext.class);
        PageTable pageTable = Tables.PAGE.as("pageTable");
        PagePojo pagePojo = context.select(pageTable.fields()).from(pageTable).where(pageTable.PAGE_ID.eq(pageId)).fetchOneInto(PagePojo.class);
        return pagePojo != null ? pagePojo.getDescription() : "";
    }

    public boolean isMenuWidgetSelected(String menuId) {
        if (this.breadcrumb == null) {
            this.breadcrumb = initBreadcrumb();
        }
        return this.breadcrumb.contains("MenuId:" + menuId);
    }

    public boolean isMenuItemWidgetSelected(String menuItemId) {
        if (this.breadcrumb == null) {
            this.breadcrumb = initBreadcrumb();
        }
        return this.breadcrumb.contains("MenuItemId:" + menuItemId);
    }

    public MenuItemPojo getMenuItem() {
        String pageId = getPageUUID();
        DSLContext context = Spring.getBean(DSLContext.class);
        MenuItemTable menuItemTable = Tables.MENU_ITEM.as("menuItemTable");
        MenuItemPojo menuItemPojo = context.select(menuItemTable.fields()).from(menuItemTable).where(menuItemTable.PAGE_ID.eq(pageId)).fetchOneInto(MenuItemPojo.class);
        return menuItemPojo;
    }

    public String getHttpAddress() {
        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
        return HttpFunction.getHttpAddress(request);
    }

}
