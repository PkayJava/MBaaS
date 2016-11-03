package com.angkorteam.mbaas.server.choice;

import com.angkorteam.mbaas.model.entity.tables.pojos.PagePojo;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.IModel;

import java.util.List;

/**
 * Created by socheat on 10/27/16.
 */
public class PageChoiceRenderer implements IChoiceRenderer<PagePojo> {

    @Override
    public Object getDisplayValue(PagePojo object) {
        return object.getTitle();
    }

    @Override
    public String getIdValue(PagePojo object, int index) {
        return object.getPageId();
    }

    @Override
    public PagePojo getObject(String id, IModel<? extends List<? extends PagePojo>> choices) {
        for (PagePojo page : choices.getObject()) {
            if (page.getPageId().equals(id)) {
                return page;
            }
        }
        return null;
    }
}
