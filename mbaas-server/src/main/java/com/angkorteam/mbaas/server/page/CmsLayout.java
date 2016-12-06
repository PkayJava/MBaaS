package com.angkorteam.mbaas.server.page;

import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.model.entity.tables.LayoutTable;
import com.angkorteam.mbaas.model.entity.tables.pojos.LayoutPojo;
import com.angkorteam.mbaas.server.Spring;
import com.angkorteam.mbaas.server.wicket.StringResourceStream;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.DefaultMarkupResourceStreamProvider;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.util.resource.IResourceStream;
import org.jooq.DSLContext;

/**
 * Created by socheatkhauv on 10/26/16.
 */
public abstract class CmsLayout extends Border implements IMarkupResourceStreamProvider, UUIDLayout {

    protected CmsLayout(String id) {
        super(id);
    }

    @Override
    protected final void onInitialize() {
        super.onInitialize();
        doInitialize();
    }

    @Override
    public final String getVariation() {
        return getLayoutUUID();
    }

    protected abstract void doInitialize();

    @Override
    public final IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        if (CmsLayout.class.isAssignableFrom(containerClass) && containerClass != CmsLayout.class) {
            DSLContext context = Spring.getBean(DSLContext.class);
            LayoutTable layoutTable = Tables.LAYOUT.as("layoutTable");
            LayoutPojo layout = context.select(layoutTable.fields()).from(layoutTable).where(layoutTable.LAYOUT_ID.eq(getLayoutUUID())).fetchOneInto(LayoutPojo.class);
            String html = layout.getHtml();
            StringResourceStream stream = new StringResourceStream("layout_" + layout.getLayoutId(), html);
            return stream;
        } else {
            DefaultMarkupResourceStreamProvider streamProvider = new DefaultMarkupResourceStreamProvider();
            return streamProvider.getMarkupResourceStream(container, containerClass);
        }
    }

}
