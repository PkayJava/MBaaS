package com.angkorteam.mbaas.server.select2;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.IChoiceProvider;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.SingleChoiceProvider;
import com.angkorteam.mbaas.server.MBaaS;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by socheat on 5/25/16.
 */
public class TableProvider extends SingleChoiceProvider<String> {

    @Override
    public List<Option> query(String term, int page) {
        List<Option> options = new ArrayList<>();
        int limit = 0;
        for (String item : MBaaS.COUNTRIES) {
            if (item.toUpperCase().startsWith(term.toUpperCase())) {
                limit++;
                options.add(new Option(item, item));
                if (limit >= IChoiceProvider.LIMIT) {
                    return options;
                }
            }
        }
        return options;
    }

    @Override
    public String toChoice(String id) {
        return id;
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
