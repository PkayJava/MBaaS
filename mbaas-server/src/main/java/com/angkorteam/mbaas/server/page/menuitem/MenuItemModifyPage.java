package com.angkorteam.mbaas.server.page.menuitem;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.MenuItemTable;
import com.angkorteam.mbaas.model.entity.tables.MenuTable;
import com.angkorteam.mbaas.model.entity.tables.PageTable;
import com.angkorteam.mbaas.model.entity.tables.SectionTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.MenuItemPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.MenuPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.PagePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.SectionPojo;
import com.angkorteam.mbaas.model.entity.tables.records.MenuItemRecord;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.choice.MenuChoiceRenderer;
import com.angkorteam.mbaas.server.choice.PageChoiceRenderer;
import com.angkorteam.mbaas.server.choice.SectionChoiceRenderer;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.validator.MenuFormValidator;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.jooq.DSLContext;

import java.util.List;

/**
 * Created by socheat on 10/24/16.
 */
public class MenuItemModifyPage extends MBaaSPage {

    private String menuItemId;

    private String title;
    private TextField<String> titleField;
    private TextFeedbackPanel titleFeedback;

    private String icon;
    private TextField<String> iconField;
    private TextFeedbackPanel iconFeedback;

    private Integer order;
    private TextField<Integer> orderField;
    private TextFeedbackPanel orderFeedback;

    private List<MenuPojo> menus;
    private MenuPojo menu;
    private DropDownChoice<MenuPojo> menuField;
    private TextFeedbackPanel menuFeedback;

    private List<SectionPojo> sections;
    private SectionPojo section;
    private DropDownChoice<SectionPojo> sectionField;
    private TextFeedbackPanel sectionFeedback;

    private List<PagePojo> cmsPages;
    private PagePojo cmsPage;
    private DropDownChoice<PagePojo> pageField;
    private TextFeedbackPanel pageFeedback;

    private Form<Void> form;
    private Button saveButton;
    private BookmarkablePageLink<Void> closeButton;

    @Override
    public String getPageUUID() {
        return MenuItemModifyPage.class.getName();
    }

    @Override
    protected void doInitialize(Border layout) {
        add(layout);

        DSLContext context = Spring.getBean(DSLContext.class);
        MenuTable menuTable = Tables.MENU.as("menuTable");
        SectionTable sectionTable = Tables.SECTION.as("sectionTable");
        PageTable pageTable = Tables.PAGE.as("pageTable");
        MenuItemTable menuItemTable = Tables.MENU_ITEM.as("menuItemTable");

        PageParameters parameters = getPageParameters();
        this.menuItemId = parameters.get("menuItemId").toString("");

        MenuItemPojo menuItem = context.select(menuItemTable.fields()).from(menuItemTable).where(menuItemTable.MENU_ITEM_ID.eq(this.menuItemId)).fetchOneInto(MenuItemPojo.class);
        this.title = menuItem.getTitle();
        this.icon = menuItem.getIcon();
        this.order = menuItem.getOrder();
        if (menuItem.getMenuId() != null) {
            this.menu = context.select(menuTable.fields()).from(menuTable).where(menuTable.MENU_ID.eq(menuItem.getMenuId())).fetchOneInto(MenuPojo.class);
        }
        if (menuItem.getSectionId() != null) {
            this.section = context.select(sectionTable.fields()).from(sectionTable).where(sectionTable.SECTION_ID.eq(menuItem.getSectionId())).fetchOneInto(SectionPojo.class);
        }
        if (menuItem.getPageId() != null) {
            this.cmsPage = context.select(pageTable.fields()).from(pageTable).where(pageTable.PAGE_ID.eq(menuItem.getPageId())).fetchOneInto(PagePojo.class);
        }

        this.form = new Form<>("form");
        layout.add(this.form);

        this.orderField = new TextField<>("orderField", new PropertyModel<>(this, "order"));
        this.orderField.setRequired(true);
        this.form.add(this.orderField);
        this.orderFeedback = new TextFeedbackPanel("orderFeedback", this.orderField);
        this.form.add(this.orderFeedback);

        this.titleField = new TextField<>("titleField", new PropertyModel<>(this, "title"));
        this.titleField.setRequired(true);
        this.form.add(this.titleField);
        this.titleFeedback = new TextFeedbackPanel("titleFeedback", this.titleField);
        this.form.add(this.titleFeedback);

        this.iconField = new TextField<>("iconField", new PropertyModel<>(this, "icon"));
        this.iconField.setRequired(true);
        this.form.add(this.iconField);
        this.iconFeedback = new TextFeedbackPanel("iconFeedback", this.iconField);
        this.form.add(this.iconFeedback);

        this.cmsPages = context.select(pageTable.fields()).from(pageTable).where(pageTable.SYSTEM.eq(false)).orderBy(pageTable.TITLE.asc()).fetchInto(PagePojo.class);
        this.pageField = new DropDownChoice<>("pageField", new PropertyModel<>(this, "cmsPage"), new PropertyModel<>(this, "cmsPages"), new PageChoiceRenderer());
        this.pageField.setRequired(true);
        this.form.add(this.pageField);
        this.pageFeedback = new TextFeedbackPanel("pageFeedback", this.pageField);
        this.form.add(this.pageFeedback);

        this.menus = context.select(menuTable.fields()).from(menuTable).orderBy(menuTable.TITLE.asc()).fetchInto(MenuPojo.class);
        this.menuField = new DropDownChoice<>("menuField", new PropertyModel<>(this, "menu"), new PropertyModel<>(this, "menus"), new MenuChoiceRenderer());
        this.menuField.setNullValid(true);
        this.form.add(this.menuField);
        this.menuFeedback = new TextFeedbackPanel("menuFeedback", this.menuField);
        this.form.add(this.menuFeedback);

        this.sections = context.select(sectionTable.fields()).from(sectionTable).orderBy(sectionTable.TITLE.asc()).fetchInto(SectionPojo.class);
        this.sectionField = new DropDownChoice<>("sectionField", new PropertyModel<>(this, "section"), new PropertyModel<>(this, "sections"), new SectionChoiceRenderer());
        this.sectionField.setNullValid(true);
        this.form.add(this.sectionField);
        this.sectionFeedback = new TextFeedbackPanel("sectionFeedback", this.sectionField);
        this.form.add(this.sectionFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);

        this.closeButton = new BookmarkablePageLink<>("closeButton", MenuItemBrowsePage.class);
        this.form.add(this.closeButton);

        this.form.add(new MenuFormValidator(this.menuField, this.sectionField));
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = Spring.getBean(DSLContext.class);
        MenuItemTable menuItemTable = Tables.MENU_ITEM.as("menuItemTable");
        MenuItemRecord menuItemRecord = context.select(menuItemTable.fields()).from(menuItemTable).where(menuItemTable.MENU_ITEM_ID.eq(this.menuItemId)).fetchOneInto(menuItemTable);
        menuItemRecord.setTitle(this.title);
        menuItemRecord.setOrder(this.order);
        menuItemRecord.setIcon(this.icon);
        if (this.cmsPage != null) {
            menuItemRecord.setPageId(this.cmsPage.getPageId());
        }
        if (this.menu != null) {
            menuItemRecord.setMenuId(this.menu.getMenuId());
        }
        if (this.section != null) {
            menuItemRecord.setSectionId(this.section.getSectionId());
        }
        menuItemRecord.update();
        setResponsePage(MenuItemBrowsePage.class);
    }

}
