package com.angkorteam.mbaas.server.ui;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.*;
import com.angkorteam.mbaas.model.entity.tables.pojos.MenuItemPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.PagePojo;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.GroovyClassLoader;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

import java.util.List;

/**
 * Created by socheat on 10/23/16.
 */
public class MenuItemWidget extends Panel {

    private String menuItemId;

    private String cssClass;

    private WebMarkupContainer menuItemContainer;

    private Label menuItemLabel;

    private boolean access = false;

    public MenuItemWidget(String id, String menuItemId) {
        super(id);
        this.menuItemId = menuItemId;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        GroovyClassLoader classLoader = Spring.getBean(GroovyClassLoader.class);
        DSLContext context = Spring.getBean(DSLContext.class);
        this.menuItemContainer = new WebMarkupContainer("menuItemContainer");
        this.menuItemContainer.add(AttributeModifier.replace("class", new PropertyModel<>(this, "cssClass")));
        add(this.menuItemContainer);

        MenuItemTable menuItemTable = Tables.MENU_ITEM.as("menuItemTable");

        MenuItemPojo menuItemPojo = context.select(menuItemTable.fields()).from(menuItemTable).where(menuItemTable.MENU_ITEM_ID.eq(this.menuItemId)).fetchOneInto(MenuItemPojo.class);

        PageTable pageTable = Tables.PAGE.as("pageTable");
        GroovyTable groovyTable = Tables.GROOVY.as("groovyTable");

        PagePojo pagePojo = context.select(pageTable.fields()).from(pageTable).where(pageTable.PAGE_ID.eq(menuItemPojo.getPageId())).fetchOneInto(PagePojo.class);
        Roles sessionRoles = getSession().getRoles();
        if (sessionRoles.hasRole("administrator")) {
            this.access = true;
            PropertyModel<Boolean> model = (PropertyModel<Boolean>) getParent().getDefaultModel();
            model.setObject(true);
        } else {
            PageRoleTable pageRoleTable = Tables.PAGE_ROLE.as("pageRoleTable");
            RoleTable roleTable = Tables.ROLE.as("roleTable");
            List<String> pageRoles = context.select(roleTable.NAME).from(roleTable).innerJoin(pageRoleTable).on(roleTable.ROLE_ID.eq(pageRoleTable.ROLE_ID)).and(pageRoleTable.PAGE_ID.eq(pagePojo.getPageId())).fetchInto(String.class);

            if (pageRoles != null && !pageRoles.isEmpty()) {
                for (String role : pageRoles) {
                    if (sessionRoles.hasRole(role)) {
                        this.access = true;
                        PropertyModel<Boolean> model = (PropertyModel<Boolean>) getParent().getDefaultModel();
                        model.setObject(true);
                        break;
                    }
                }
            }
        }

        Class<? extends WebPage> page = null;
        if (!pagePojo.getCmsPage()) {
            try {
                page = (Class<? extends WebPage>) classLoader.loadClass(pagePojo.getPageId());
            } catch (ClassNotFoundException e) {
            }
        } else {
            try {
                String javaClass = context.select(groovyTable.JAVA_CLASS).from(groovyTable).where(groovyTable.GROOVY_ID.eq(pagePojo.getGroovyId())).fetchOneInto(String.class);
                page = (Class<? extends WebPage>) classLoader.loadClass(javaClass);
            } catch (ClassNotFoundException e) {
            }
        }

        BookmarkablePageLink<Void> menuItemLink = new BookmarkablePageLink<>("menuItemLink", page);
        this.menuItemContainer.add(menuItemLink);

        WebMarkupContainer menuItemIcon = new WebMarkupContainer("menuItemIcon");
        menuItemLink.add(menuItemIcon);
        menuItemIcon.add(AttributeModifier.replace("class", "fa " + menuItemPojo.getIcon()));

        this.menuItemLabel = new Label("menuItemLabel", menuItemPojo.getTitle());
        menuItemLink.add(this.menuItemLabel);
        this.menuItemLabel.setRenderBodyOnly(menuItemPojo.getSectionId() == null);
    }

    @Override
    protected void onBeforeRender() {
        setVisible(this.access);
        MBaaSPage mBaaSPage = (MBaaSPage) getPage();
        this.cssClass = mBaaSPage.isMenuItemWidgetSelected(this.menuItemId) ? "active" : "";
        super.onBeforeRender();
    }

    @Override
    public com.angkorteam.mbaas.server.Session getSession() {
        return (com.angkorteam.mbaas.server.Session) super.getSession();
    }
}
