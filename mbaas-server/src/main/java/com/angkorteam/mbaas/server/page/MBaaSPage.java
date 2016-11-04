package com.angkorteam.mbaas.server.page;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.GroovyTable;
import com.angkorteam.mbaas.model.entity.tables.LayoutTable;
import com.angkorteam.mbaas.model.entity.tables.MenuItemTable;
import com.angkorteam.mbaas.model.entity.tables.MenuTable;
import com.angkorteam.mbaas.model.entity.tables.PageTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.LayoutPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.MenuItemPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.MenuPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.PagePojo;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.GroovyClassLoader;
import com.angkorteam.mbaas.server.function.HttpFunction;
import com.google.common.collect.Lists;
import org.apache.wicket.markup.html.border.Border;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by socheat on 10/23/16.
 */
public abstract class MBaaSPage extends AdminLTEPage implements UUIDPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmsPage.class);

    private List<String> breadcrumb;

    private Border layout;

    @Override
    protected final void onInitialize() {
        super.onInitialize();
        this.breadcrumb = initBreadcrumb();

        DSLContext context = Spring.getBean(DSLContext.class);
        PageTable pageTable = Tables.PAGE.as("pageTable");
        GroovyClassLoader classLoader = Spring.getBean(GroovyClassLoader.class);

        PagePojo page = context.select(pageTable.fields()).from(pageTable).where(pageTable.PAGE_ID.eq(getPageUUID())).fetchOneInto(PagePojo.class);
        LayoutTable layoutTable = Tables.LAYOUT.as("layoutTable");
        LayoutPojo layout = context.select(layoutTable.fields()).from(layoutTable).where(layoutTable.LAYOUT_ID.eq(page.getLayoutId())).fetchOneInto(LayoutPojo.class);

        Class<? extends Border> layoutClass = null;
        try {
            if (layout.getSystem()) {
                layoutClass = (Class<? extends Border>) classLoader.loadClass(layout.getLayoutId());
            } else {
                GroovyTable groovyTable = Tables.GROOVY.as("groovyTable");
                String javaClass = context.select(groovyTable.JAVA_CLASS).from(groovyTable).where(groovyTable.GROOVY_ID.eq(layout.getGroovyId())).fetchOneInto(String.class);
                layoutClass = (Class<? extends Border>) classLoader.loadClass(javaClass);
            }
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
        }
        Constructor<? extends Border> constructor = null;
        try {
            constructor = layoutClass.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            LOGGER.error(e.getMessage());
        }
        Border cmsLayout = null;
        try {
            cmsLayout = constructor.newInstance("layout");
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            LOGGER.error(e.getMessage());
        }
        this.layout = cmsLayout;

        doInitialize(this.layout);
    }

    protected abstract void doInitialize(Border layout);

    protected final Border getLayout() {
        return this.layout;
    }

    public MenuItemPojo getMenuItem() {
        String pageId = getPageUUID();
        DSLContext context = Spring.getBean(DSLContext.class);
        MenuItemTable menuItemTable = Tables.MENU_ITEM.as("menuItemTable");
        MenuItemPojo menuItemPojo = context.select(menuItemTable.fields()).from(menuItemTable).where(menuItemTable.PAGE_ID.eq(pageId)).fetchOneInto(MenuItemPojo.class);
        return menuItemPojo;
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

    public String getHttpAddress() {
        HttpServletRequest request = (HttpServletRequest) getRequest().getContainerRequest();
        return HttpFunction.getHttpAddress(request);
    }

}
