package com.angkorteam.mbaas.server.page.menu;

import com.angkorteam.framework.extension.wicket.markup.html.form.Button;
import com.angkorteam.framework.extension.wicket.markup.html.form.Form;
import com.angkorteam.framework.extension.wicket.markup.html.panel.TextFeedbackPanel;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.MenuTable;
import com.angkorteam.mbaas.model.entity.tables.SectionTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.MenuPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.SectionPojo;
import com.angkorteam.mbaas.model.entity.tables.records.MenuRecord;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.choice.MenuChoiceRenderer;
import com.angkorteam.mbaas.server.choice.SectionChoiceRenderer;
import com.angkorteam.mbaas.server.page.MBaaSPage;
import com.angkorteam.mbaas.server.validator.MenuFormValidator;
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
public class MenuModifyPage extends MBaaSPage {

    private String menuId;

    private String title;
    private TextField<String> titleField;
    private TextFeedbackPanel titleFeedback;

    private String icon;
    private TextField<String> iconField;
    private TextFeedbackPanel iconFeedback;

    private Integer order;
    private TextField<Integer> orderField;
    private TextFeedbackPanel orderFeedback;

    private List<MenuPojo> menuParents;
    private MenuPojo menuParent;
    private DropDownChoice<MenuPojo> parentField;
    private TextFeedbackPanel parentFeedback;

    private List<SectionPojo> sections;
    private SectionPojo section;
    private DropDownChoice<SectionPojo> sectionField;
    private TextFeedbackPanel sectionFeedback;

    private Form<Void> form;
    private Button saveButton;
    private BookmarkablePageLink<Void> closeButton;

    @Override
    protected void onInitialize() {
        super.onInitialize();
        DSLContext context = Spring.getBean(DSLContext.class);
        MenuTable menuTable = Tables.MENU.as("menuTable");
        SectionTable sectionTable = Tables.SECTION.as("sectionTable");

        PageParameters parameters = getPageParameters();
        this.menuId = parameters.get("menuId").toString("");

        MenuPojo menu = context.select(menuTable.fields()).from(menuTable).where(menuTable.MENU_ID.eq(this.menuId)).fetchOneInto(MenuPojo.class);
        this.title = menu.getTitle();
        this.icon = menu.getIcon();
        this.order = menu.getOrder();
        if (menu.getParentMenuId() != null) {
            this.menuParent = context.select(menuTable.fields()).from(menuTable).where(menuTable.MENU_ID.eq(menu.getParentMenuId())).fetchOneInto(MenuPojo.class);
        }
        if (menu.getSectionId() != null) {
            this.section = context.select(sectionTable.fields()).from(sectionTable).where(sectionTable.SECTION_ID.eq(menu.getSectionId())).fetchOneInto(SectionPojo.class);
        }

        this.form = new Form<>("form");
        add(this.form);

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

        this.menuParents = context.select(menuTable.fields()).from(menuTable).fetchInto(MenuPojo.class);
        this.parentField = new DropDownChoice<>("parentField", new PropertyModel<>(this, "menuParent"), new PropertyModel<>(this, "menuParents"), new MenuChoiceRenderer());
        this.parentField.setNullValid(true);
        this.form.add(this.parentField);
        this.parentFeedback = new TextFeedbackPanel("parentFeedback", this.parentField);
        this.form.add(this.parentFeedback);

        this.sections = context.select(sectionTable.fields()).from(sectionTable).fetchInto(SectionPojo.class);
        this.sectionField = new DropDownChoice<>("sectionField", new PropertyModel<>(this, "section"), new PropertyModel<>(this, "sections"), new SectionChoiceRenderer());
        this.sectionField.setNullValid(true);
        this.form.add(this.sectionField);
        this.sectionFeedback = new TextFeedbackPanel("sectionFeedback", this.sectionField);
        this.form.add(this.sectionFeedback);

        this.saveButton = new Button("saveButton");
        this.saveButton.setOnSubmit(this::saveButtonOnSubmit);
        this.form.add(this.saveButton);

        this.closeButton = new BookmarkablePageLink<>("closeButton", MenuBrowsePage.class);
        this.form.add(this.closeButton);

        this.form.add(new MenuFormValidator(this.parentField, this.sectionField));
    }

    private void saveButtonOnSubmit(Button button) {
        DSLContext context = Spring.getBean(DSLContext.class);
        MenuTable menuTable = Tables.MENU.as("menuTable");
        MenuRecord menuRecord = context.select(menuTable.fields()).from(menuTable).where(menuTable.MENU_ID.eq(this.menuId)).fetchOneInto(menuTable);
        menuRecord.setTitle(this.title);
        menuRecord.setIcon(this.icon);
        menuRecord.setOrder(this.order);
        if (this.menuParent != null) {
            menuRecord.setParentMenuId(this.menuParent.getMenuId());
            menuRecord.setPath(this.menuParent.getPath() + " > " + this.title);
        }
        if (this.section != null) {
            menuRecord.setPath(this.section.getTitle() + " > " + this.title);
            menuRecord.setSectionId(this.section.getSectionId());
        }
        menuRecord.update();
        setResponsePage(MenuBrowsePage.class);
    }

    @Override
    public String getPageUUID() {
        return MenuModifyPage.class.getName();
    }

}
