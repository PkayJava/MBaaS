package com.angkorteam.mbaas.server.nashorn.wicket.markup.html.form.select2;

import com.angkorteam.framework.extension.wicket.markup.html.form.select2.MultipleChoiceProvider;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
import com.angkorteam.mbaas.server.wicket.Application;
import com.angkorteam.mbaas.server.wicket.ApplicationUtils;
import com.google.gson.Gson;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by socheat on 6/1/16.
 */
public class NashornMultipleChoiceProvider extends MultipleChoiceProvider<Map<String, Object>> {

    private final String applicationCode;

    private ToChoices toChoices;

    private Query query;

    private HasMore hasMore;

    public NashornMultipleChoiceProvider(String applicationCode) {
        this.applicationCode = applicationCode;
    }

    @Override
    public List<Map<String, Object>> toChoices(List<String> ids) {
        if (this.toChoices != null) {
            Application application = ApplicationUtils.getApplication();
            JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
            return this.toChoices.toChoices(jdbcTemplate, ids);
        }
        return null;
    }

    @Override
    public List<Option> query(String term, int page) {
        if (this.query != null) {
            Application application = ApplicationUtils.getApplication();
            JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
            return this.query.query(jdbcTemplate, term, page);
        }
        return null;
    }

    @Override
    public boolean hasMore(String term, int page) {
        if (this.hasMore != null) {
            Application application = ApplicationUtils.getApplication();
            JdbcTemplate jdbcTemplate = application.getJdbcTemplate(this.applicationCode);
            return this.hasMore.hasMore(jdbcTemplate, term, page);
        }
        return false;
    }

    @Override
    public Gson getGson() {
        return ApplicationUtils.getApplication().getGson();
    }

    public ToChoices getToChoices() {
        return toChoices;
    }

    public void setToChoices(ToChoices toChoices) {
        this.toChoices = toChoices;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public HasMore getHasMore() {
        return hasMore;
    }

    public void setHasMore(HasMore hasMore) {
        this.hasMore = hasMore;
    }

    public interface ToChoices extends Serializable {
        List<Map<String, Object>> toChoices(JdbcTemplate jdbcTemplate, List<String> ids);
    }

    public interface Query extends Serializable {
        List<Option> query(JdbcTemplate jdbcTemplate, String term, int page);
    }

    public interface HasMore extends Serializable {
        boolean hasMore(JdbcTemplate jdbcTemplate, String term, int page);
    }
}
