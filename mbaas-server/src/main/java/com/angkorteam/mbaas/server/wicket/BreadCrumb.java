package com.angkorteam.mbaas.server.wicket;

import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.io.Serializable;

/**
 * Created by socheat on 3/10/16.
 */
public class BreadCrumb implements Serializable {

    private Class<? extends Page> page;

    private PageParameters parameters = new PageParameters();

    private String label;

    public Class<? extends Page> getPage() {
        return page;
    }

    public void setPage(Class<? extends Page> page) {
        this.page = page;
    }

    public PageParameters getParameters() {
        return parameters;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
