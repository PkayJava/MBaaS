package com.angkorteam.mbaas.server.ui;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.GroovyTable;
import com.angkorteam.mbaas.model.entity.tables.MenuItemTable;
import com.angkorteam.mbaas.model.entity.tables.PageTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.MenuItemPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.PagePojo;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

/**
 * Created by socheat on 10/23/16.
 */
public class MenuItemWidget extends Panel {

    private String menuItemId;

    private String cssClass;

    private WebMarkupContainer menuItemContainer;

    private Label menuItemLabel;

    public MenuItemWidget(String id, String menuItemId) {
        super(id);
        this.menuItemId = menuItemId;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = Spring.getBean(DSLContext.class);
        this.menuItemContainer = new WebMarkupContainer("menuItemContainer");
        this.menuItemContainer.add(AttributeModifier.replace("class", new PropertyModel<>(this, "cssClass")));
        add(this.menuItemContainer);

        MenuItemTable menuItemTable = Tables.MENU_ITEM.as("menuItemTable");

        MenuItemPojo menuItemPojo = context.select(menuItemTable.fields()).from(menuItemTable).where(menuItemTable.MENU_ITEM_ID.eq(this.menuItemId)).fetchOneInto(MenuItemPojo.class);

        PageTable pageTable = Tables.PAGE.as("pageTable");
        GroovyTable groovyTable = Tables.GROOVY.as("groovyTable");

        PagePojo pagePojo = context.select(pageTable.fields()).from(pageTable).where(pageTable.PAGE_ID.eq(menuItemPojo.getPageId())).fetchOneInto(PagePojo.class);

        Class<? extends WebPage> page = null;
        if (!pagePojo.getCmsPage()) {
            try {
                page = (Class<? extends WebPage>) Class.forName(pagePojo.getPageId());
            } catch (ClassNotFoundException e) {
            }
        } else {
            try {
                String javaClass = context.select(groovyTable.JAVA_CLASS).from(groovyTable).where(groovyTable.GROOVY_ID.eq(pagePojo.getGroovyId())).fetchOneInto(String.class);
                page = (Class<? extends WebPage>) Class.forName(javaClass);
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
        super.onBeforeRender();
        MBaaSPage mBaaSPage = (MBaaSPage) getPage();
        this.cssClass = mBaaSPage.isMenuItemWidgetSelected(this.menuItemId) ? "active" : "";
    }
}
