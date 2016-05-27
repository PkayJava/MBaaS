package com.angkorteam.mbaas.server.select2;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.MultipleChoiceProvider;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
import com.angkorteam.mbaas.server.MBaaS;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by socheat on 5/25/16.
 */
public class TableProvider1 extends MultipleChoiceProvider<String> {

    @Override
    public List<Option> query(String term, int page) {
        List<Option> options = new ArrayList<>();
        for (String item : MBaaS.COUNTRIES) {
            if (item.startsWith(term)) {
                options.add(new Option(item, item));
            }
        }
        return options;
    }

    @Override
    public List<String> toChoices(List<String> ids) {
        return ids;
    }

    @Override
    public Gson getGson() {
        return ApplicationUtils.getApplication().getGson();
    }

    @Override
    public boolean hasMore(String term, int page) {
        return true;
    }
}
