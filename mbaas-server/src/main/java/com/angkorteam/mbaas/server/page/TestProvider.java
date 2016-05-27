package com.angkorteam.mbaas.server.page;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.SingleChoiceProvider;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

/**
 * Created by socheat on 5/24/16.
 */
public class TestProvider extends SingleChoiceProvider<String> {

    public TestProvider() {
    }

    @Override
    public List<Option> query(String term, int page) {
        Option option = new Option("1", "Cambodia");
        return Arrays.asList(option);
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
