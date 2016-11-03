package com.angkorteam.mbaas.server.page;

import com.angkorteam.framework.extension.wicket.AdminLTEPage;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.LayoutTable;
import com.angkorteam.mbaas.model.entity.tables.PageTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.LayoutPojo;
import com.angkorteam.mbaas.model.entity.tables.pojos.PagePojo;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.bean.GroovyClassLoader;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.DefaultMarkupResourceStreamProvider;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by socheatkhauv on 10/25/16.
 */
public abstract class CmsPage extends AdminLTEPage implements UUIDPage, IMarkupResourceStreamProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmsPage.class);

    private CmsLayout layout;

    @Override
    protected final void onInitialize() {
        super.onInitialize();
        DSLContext context = Spring.getBean(DSLContext.class);
        PageTable pageTable = Tables.PAGE.as("pageTable");
        GroovyClassLoader classLoader = Spring.getBean(GroovyClassLoader.class);

        PagePojo page = context.select(pageTable.fields()).from(pageTable).where(pageTable.PAGE_ID.eq(getPageUUID())).fetchOneInto(PagePojo.class);
        LayoutTable layoutTable = Tables.LAYOUT.as("layoutTable");
        LayoutPojo layout = context.select(layoutTable.fields()).from(layoutTable).where(layoutTable.LAYOUT_ID.eq(page.getLayoutId())).fetchOneInto(LayoutPojo.class);

        Class<? extends CmsLayout> layoutClass = null;
        try {
            layoutClass = (Class<? extends CmsLayout>) classLoader.loadClass(layout.getJavaClass());
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
        }
        Constructor<? extends CmsLayout> constructor = null;
        try {
            constructor = layoutClass.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            LOGGER.error(e.getMessage());
        }
        CmsLayout cmsLayout = null;
        try {
            cmsLayout = constructor.newInstance("layout");
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            LOGGER.error(e.getMessage());
        }
        this.layout = cmsLayout;

        doInitialize();
    }

    @Override
    public String getVariation() {
        return getPageUUID();
    }

    public final Border getLayout() {
        return this.layout;
    }

    protected abstract void doInitialize();

    @Override
    public final IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        if (CmsPage.class.isAssignableFrom(containerClass) && containerClass != CmsPage.class) {
            DSLContext context = Spring.getBean(DSLContext.class);
            PageTable pageTable = Tables.PAGE.as("pageTable");
            PagePojo page = context.select(pageTable.fields()).from(pageTable).where(pageTable.PAGE_ID.eq(getPageUUID())).fetchOneInto(PagePojo.class);
            String html = page.getHtml();
            StringResourceStream stream = new StringResourceStream(html);
            return stream;
        } else {
            DefaultMarkupResourceStreamProvider streamProvider = new DefaultMarkupResourceStreamProvider();
            return streamProvider.getMarkupResourceStream(container, containerClass);
        }
    }

}