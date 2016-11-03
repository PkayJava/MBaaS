package com.angkorteam.mbaas.server.page;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.PageTable;
import com.angkorteam.mbaas.model.entity.tables.SectionTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.PagePojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.SectionPojo;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.ui.SectionWidget;
import com.google.common.base.Strings;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.jooq.DSLContext;

import java.util.List;

/**
 * Created by socheat on 11/3/16.
 */
public class MBaaSLayout extends Border implements UUIDLayout {

    public MBaaSLayout(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        WebMarkupContainer headerContainer = new WebMarkupContainer("headerContainer");
        addToBorder(headerContainer);
        String htmlPageTitle = getHtmlPageTitle();
        String htmlPageDescription = getHtmlPageDescription();
        Label headerTitle = new Label("headerTitle", new PropertyModel<>(this, "htmlPageTitle"));
        headerContainer.add(headerTitle);
        Label headerDescription = new Label("headerDescription", new PropertyModel<>(this, "htmlPageDescription"));
        headerContainer.add(headerDescription);
        headerContainer.setVisible(!(Strings.isNullOrEmpty(htmlPageTitle) && Strings.isNullOrEmpty(htmlPageDescription)));

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
        addToBorder(sectionWidgets);

        BookmarkablePageLink<Void> logoutPage = new BookmarkablePageLink<>("logoutPage", LogoutPage.class);
        addToBorder(logoutPage);

    }

    public String getHtmlPageTitle() {
        String pageId = ((MBaaSPage) getPage()).getPageUUID();
        DSLContext context = Spring.getBean(DSLContext.class);
        PageTable pageTable = Tables.PAGE.as("pageTable");
        PagePojo pagePojo = context.select(pageTable.fields()).from(pageTable).where(pageTable.PAGE_ID.eq(pageId)).fetchOneInto(PagePojo.class);
        return pagePojo != null ? pagePojo.getTitle() : "";
    }

    public String getHtmlPageDescription() {
        String pageId = ((MBaaSPage) getPage()).getPageUUID();
        DSLContext context = Spring.getBean(DSLContext.class);
        PageTable pageTable = Tables.PAGE.as("pageTable");
        PagePojo pagePojo = context.select(pageTable.fields()).from(pageTable).where(pageTable.PAGE_ID.eq(pageId)).fetchOneInto(PagePojo.class);
        return pagePojo != null ? pagePojo.getDescription() : "";
    }

    @Override
    public String getLayoutUUID() {
        return MBaaSLayout.class.getName();
    }
}
