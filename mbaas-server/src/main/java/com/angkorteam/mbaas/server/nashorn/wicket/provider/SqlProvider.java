package com.angkorteam.mbaas.server.nashorn.wicket.provider;

import com.angkorteam.framework.extension.jooq.IDSLContext;
import com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Expression;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Application;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.MapModel;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Created by socheat on 6/11/16.
 */
public abstract class SqlProvider extends SortableDataProvider<Map<String, Object>, String> implements IFilterStateLocator<Map<String, String>> {

    /**
     *
     */
    private static final long serialVersionUID = 2453015465083001162L;

    private static final char HIDDEN_SPACE = '\u200B';

    private Map<String, String> filterState;

    private Map<String, Class<?>> itemClass;
    private String groupBy;

    private Map<String, String> fields;
    private Map<String, String> reverseFields;

    private static final List<String> AGGREGATE_FUNCTION;

    static {
        AGGREGATE_FUNCTION = Arrays.asList("MAX", "MIN", "SUM", "AVG", "MEDIAN", "STDDEV_POP", "STDDEV_SAMP", "VAR_POP", "VAR_SAMP", "REGR_SLOPE", "REGR_INTERCEPT", "REGR_COUNT", "REGR_R2", "REGR_AVGX", "REGR_AVGY");
    }

    public SqlProvider() {
        this.fields = new HashMap<>();
        this.reverseFields = new HashMap<>();
        this.itemClass = new HashMap<>();
        this.filterState = new HashMap<>();
    }

    private Field<?> findField(Field<?> alias) {
        java.lang.reflect.Field aliasField = null;
        try {
            aliasField = alias.getClass().getDeclaredField("alias");
        } catch (NoSuchFieldException e) {
            return null;
        }
        aliasField.setAccessible(true);
        Object aliasObject = null;
        try {
            aliasObject = aliasField.get(alias);
        } catch (IllegalAccessException e) {
            return null;
        }

        Method wrappedMethod = null;
        try {
            wrappedMethod = aliasObject.getClass().getDeclaredMethod("wrapped");
        } catch (NoSuchMethodException e) {
            return null;
        }
        wrappedMethod.setAccessible(true);

        Field<Object> field = null;
        try {
            field = (Field<Object>) wrappedMethod.invoke(aliasObject);
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
        return field;
    }

    protected abstract TableLike<?> from();

    private List<Condition> buildWhere() {
        List<Condition> where = new ArrayList<>();

        for (Map.Entry<String, String> entry : this.filterState.entrySet()) {
            String filterText = entry.getValue();
            if (filterText != null && !"".equals(filterText)) {
                String tableColumn = entry.getKey();
                String queryColumn = this.fields.get(tableColumn).trim().toUpperCase();
                Class<?> clazz = this.itemClass.get(tableColumn);
                Field<?> field = parseField(tableColumn, clazz);
                if (!AGGREGATE_FUNCTION.contains(queryColumn)) {
                    Expression expression = Expression.parse(filterText.trim());
                    buildCondition(where, clazz, field, expression);
                }
            }
        }

        List<Condition> userWhere = where();
        if (userWhere != null && !userWhere.isEmpty()) {
            where.addAll(userWhere);
        }

        return where;
    }

    private List<Condition> buildHaving() {
        List<Condition> having = new ArrayList<>();

        for (Map.Entry<String, String> entry : this.filterState.entrySet()) {
            String filterText = entry.getValue();
            if (filterText != null && !"".equals(filterText)) {
                String tableColumn = entry.getKey();
                String queryColumn = this.fields.get(tableColumn).trim().toUpperCase();
                Class<?> clazz = this.itemClass.get(tableColumn);
                Field<?> field = parseField(tableColumn, clazz);
                if (AGGREGATE_FUNCTION.contains(queryColumn)) {
                    Expression expression = Expression.parse(filterText.trim());
                    buildCondition(having, clazz, field, expression);
                }
            }
        }

        List<Condition> userHaving = having();
        if (userHaving != null && !userHaving.isEmpty()) {
            having.addAll(userHaving);
        }

        return having;
    }

    private void buildCondition(List<Condition> conditions, Class<?> clazz, Field<?> field, Expression expression) {
        if (clazz == String.class
                || clazz == Character.class || clazz == char.class) {
            Condition condition = buildStringCondition((Field<String>) field, expression);
            conditions.add(condition);
        } else if (clazz == Long.class || clazz == long.class) {
            Condition condition = buildLongCondition((Field<Long>) field, expression);
            conditions.add(condition);
        } else if (clazz == Double.class || clazz == double.class) {
            Condition condition = buildDoubleCondition((Field<Double>) field, expression);
            conditions.add(condition);
        } else if (clazz == Byte.class || clazz == byte.class) {
            Condition condition = buildByteCondition((Field<Byte>) field, expression);
            conditions.add(condition);
        } else if (clazz == Integer.class || clazz == int.class) {
            Condition condition = buildIntegerCondition((Field<Integer>) field, expression);
            conditions.add(condition);
        } else if (clazz == Float.class || clazz == float.class) {
            Condition condition = buildFloatCondition((Field<Float>) field, expression);
            conditions.add(condition);
        } else if (clazz == Short.class || clazz == short.class) {
            Condition condition = buildShortCondition((Field<Short>) field, expression);
            conditions.add(condition);
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            Condition condition = buildBooleanCondition((Field<Boolean>) field, expression);
            conditions.add(condition);
        } else if (clazz == LocalDateTime.class) {
            List<Condition> condition = buildDateTimeCondition((Field<Date>) field, expression);
            if (condition != null && !condition.isEmpty()) {
                conditions.addAll(condition);
            }
        } else if (clazz == LocalDate.class) {
            List<Condition> condition = buildDateCondition((Field<Date>) field, expression);
            if (conditions != null && !conditions.isEmpty()) {
                conditions.addAll(condition);
            }
        } else if (clazz == LocalTime.class) {
            List<Condition> condition = buildTimeCondition((Field<Date>) field, expression);
            if (condition != null && !condition.isEmpty()) {
                conditions.addAll(condition);
            }
        }
    }

    protected SortField<?> buildOrderBy() {
        if (getSort() == null) {
            return null;
        }
        SortField<?> sortField = null;
        String property = getSort().getProperty();
        Class<?> clazz = this.itemClass.get(property);
        Field<?> field = parseField(this.fields.get(property), clazz);
        if (field == null) {
            return null;
        }
        if (getSort() != null) {
            if (getSort().isAscending()) {
                sortField = field.asc();
            } else {
                sortField = field.desc();
            }
        }
        return sortField;
    }

    private Field<?> parseField(String name, Class<?> clazz) {
        Field<?> field = null;
        if (clazz == Boolean.class || clazz == boolean.class) {
            return DSL.field(name, Boolean.class);
        } else if (clazz == Byte.class || clazz == byte.class) {
            return DSL.field(name, Byte.class);
        } else if (clazz == Short.class || clazz == short.class) {
            return DSL.field(name, Short.class);
        } else if (clazz == Integer.class || clazz == int.class
                || clazz == BigInteger.class) {
            return DSL.field(name, Integer.class);
        } else if (clazz == Long.class || clazz == long.class) {
            return DSL.field(name, Long.class);
        } else if (clazz == Float.class || clazz == float.class) {
            return DSL.field(name, Float.class);
        } else if (clazz == Double.class || clazz == double.class
                || clazz == BigDecimal.class) {
            return DSL.field(name, Double.class);
        } else if (clazz == String.class) {
            return DSL.field(name, String.class);
        } else if (clazz == Character.class || clazz == char.class) {
            return DSL.field(name, Character.class);
        } else if (clazz == LocalDateTime.class
                || clazz == LocalTime.class
                || clazz == LocalDate.class) {
            return DSL.field(name, Date.class);
        }
        return field;
    }

    public List<Field<?>> getFields() {
        List<Field<?>> fields = new ArrayList<>();
        for (Map.Entry<String, String> item : this.fields.entrySet()) {
            Class<?> clazz = this.itemClass.get(item.getKey());
            Field<?> field = parseField(item.getValue(), clazz);
            if (field != null) {
                fields.add(field);
            }
        }
        return fields;
    }

    public void selectField(String propertyColumn, String queryColumn, Class<?> clazz) {
        this.itemClass.put(propertyColumn, clazz);
        this.fields.put(propertyColumn, queryColumn);
        this.reverseFields.put(queryColumn, propertyColumn);
    }

    @Override
    public final IModel<Map<String, Object>> model(Map<String, Object> object) {
        return new MapModel<>(object);
    }

    @Override
    public final Map<String, String> getFilterState() {
        return this.filterState;
    }

    @Override
    public final void setFilterState(Map<String, String> state) {
        this.filterState = state;
    }

    protected SelectLimitStep<Record> buildQuery() {
        return buildQuery(null);
    }

    protected SelectLimitStep<Record> buildQuery(SortField<?> orderBy) {
        DSLContext context = getDSLContext();
        List<Condition> where = buildWhere();
        List<Condition> having = buildHaving();
        SelectLimitStep<Record> select = null;
        if (!where.isEmpty()) {
            if (this.groupBy != null) {
                Field<?> groupBy = parseField(getGroupBy(), this.itemClass.get(getGroupBy()));
                if (!having.isEmpty()) {
                    if (orderBy != null) {
                        if (getFields() != null && !getFields().isEmpty()) {
                            select = context.select(getFields()).from(from()).where(where).groupBy(groupBy).having(having).orderBy(orderBy);
                        } else {
                            select = context.select().from(from()).where(where).groupBy(groupBy).having(having).orderBy(orderBy);
                        }
                    } else {
                        if (getFields() != null && !getFields().isEmpty()) {
                            select = context.select(getFields()).from(from()).where(where).groupBy(groupBy).having(having);
                        } else {
                            select = context.select().from(from()).where(where).groupBy(groupBy).having(having);
                        }
                    }
                } else {
                    if (orderBy != null) {
                        if (getFields() != null && !getFields().isEmpty()) {
                            select = context.select(getFields()).from(from()).where(where).groupBy(groupBy).orderBy(orderBy);
                        } else {
                            select = context.select().from(from()).where(where).groupBy(groupBy).orderBy(orderBy);
                        }
                    } else {
                        if (getFields() != null && !getFields().isEmpty()) {
                            select = context.select(getFields()).from(from()).where(where).groupBy(groupBy);
                        } else {
                            select = context.select().from(from()).where(where).groupBy(groupBy);
                        }
                    }
                }
            } else {
                if (orderBy != null) {
                    if (getFields() != null && !getFields().isEmpty()) {
                        select = context.select(getFields()).from(from()).where(where).orderBy(orderBy);
                    } else {
                        select = context.select().from(from()).where(where).orderBy(orderBy);
                    }
                } else {
                    if (getFields() != null && !getFields().isEmpty()) {
                        select = context.select(getFields()).from(from()).where(where);
                    } else {
                        select = context.select().from(from()).where(where);
                    }
                }
            }
        } else {
            if (this.groupBy != null) {
                Field<?> groupBy = parseField(getGroupBy(), this.itemClass.get(getGroupBy()));
                if (!having.isEmpty()) {
                    if (orderBy != null) {
                        if (getFields() != null && !getFields().isEmpty()) {
                            select = context.select(getFields()).from(from()).groupBy(groupBy).having(having).orderBy(orderBy);
                        } else {
                            select = context.select().from(from()).groupBy(groupBy).having(having).orderBy(orderBy);
                        }
                    } else {
                        if (getFields() != null && !getFields().isEmpty()) {
                            select = context.select(getFields()).from(from()).groupBy(groupBy).having(having);
                        } else {
                            select = context.select().from(from()).groupBy(groupBy).having(having);
                        }
                    }
                } else {
                    if (orderBy != null) {
                        if (getFields() != null && !getFields().isEmpty()) {
                            select = context.select(getFields()).from(from()).groupBy(groupBy).orderBy(orderBy);
                        } else {
                            select = context.select().from(from()).groupBy(groupBy).orderBy(orderBy);
                        }
                    } else {
                        if (getFields() != null && !getFields().isEmpty()) {
                            select = context.select(getFields()).from(from()).groupBy(groupBy);
                        } else {
                            select = context.select().from(from()).groupBy(groupBy);
                        }
                    }
                }
            } else {
                if (orderBy != null) {
                    if (getFields() != null && !getFields().isEmpty()) {
                        select = context.select(getFields()).from(from()).orderBy(orderBy);
                    } else {
                        select = context.select().from(from()).orderBy(orderBy);
                    }
                } else {
                    if (getFields() != null && !getFields().isEmpty()) {
                        select = context.select(getFields()).from(from());
                    } else {
                        select = context.select().from(from());
                    }
                }
            }
        }
        return select;
    }

    protected abstract List<Condition> where();

    protected abstract List<Condition> having();

    @Override
    public Iterator<Map<String, Object>> iterator(long first, long count) {
        SortField<?> orderBy = buildOrderBy();
        SelectLimitStep<Record> select = buildQuery(orderBy);
        List<Map<String, Object>> entities = select.limit((int) first, (int) count).fetch(new RecordMapper());

        List<Map<String, Object>> chunks = new ArrayList<>();

        if (entities != null && !entities.isEmpty()) {
            for (Map<String, Object> entity : entities) {
                Map<String, Object> chunk = new HashMap<>();
                for (Map.Entry<String, Object> entry : entity.entrySet()) {
                    String key = this.reverseFields.get(entry.getKey());
                    chunk.put(key, entry.getValue());
                }
                chunks.add(chunk);
            }
        }
        return chunks.iterator();
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getGroupBy() {
        return groupBy;
    }

    @Override
    public long size() {
        DSLContext context = getDSLContext();
        SelectLimitStep<Record> select = buildQuery();
        return (long) context.fetchCount(select);
    }

    protected DSLContext getDSLContext() {
        if (Application.get() instanceof IDSLContext) {
            return ((IDSLContext) Application.get()).getDSLContext();
        } else {
            throw new WicketRuntimeException(Application.get().getClass().getName() + " is not instance of " + IDSLContext.class.getName());
        }
    }

    private List<Condition> buildDateTimeCondition(Field<Date> field, Expression expression) {
        List<Condition> conditions = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date v1 = null;
        try {
            v1 = dateFormat.parse(expression.getFirstOperand());
        } catch (ParseException e) {
        }

        if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Equal) {
            if (v1 != null) {
                conditions.add(DSL.timestamp(field).eq(new java.sql.Timestamp(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.NotEqual) {
            if (v1 != null) {
                conditions.add(DSL.timestamp(field).notEqual(new java.sql.Timestamp(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.GreaterThan) {
            if (v1 != null) {
                conditions.add(DSL.timestamp(field).greaterThan(new java.sql.Timestamp(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.GreaterThanAndEqual) {
            if (v1 != null) {
                conditions.add(DSL.timestamp(field).greaterOrEqual(new java.sql.Timestamp(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.LessThan) {
            if (v1 != null) {
                conditions.add(DSL.timestamp(field).lessThan(new java.sql.Timestamp(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.LessThanAndEqual) {
            if (v1 != null) {
                conditions.add(DSL.timestamp(field).lessOrEqual(new java.sql.Timestamp(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Like) {
            if (v1 != null) {
                conditions.add(DSL.timestamp(field).eq(new java.sql.Timestamp(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Between) {
            Date v2 = null;
            try {
                v2 = dateFormat.parse(expression.getSecondOperand());
            } catch (ParseException e) {
            }
            if (v1 != null && v2 != null) {
                java.sql.Timestamp dt1 = new java.sql.Timestamp(v1.getTime());
                java.sql.Timestamp dt2 = new java.sql.Timestamp(v2.getTime());
                if (dt1.before(dt2)) {
                    conditions.add(DSL.timestamp(field).between(dt1, dt2));
                } else {
                    conditions.add(DSL.timestamp(field).between(dt2, dt1));
                }
            }
        }
        return conditions;
    }

    private List<Condition> buildTimeCondition(Field<Date> field, Expression expression) {
        List<Condition> conditions = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date v1 = null;
        try {
            v1 = dateFormat.parse(expression.getFirstOperand());
        } catch (ParseException e) {
        }

        if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Equal) {
            if (v1 != null) {
                conditions.add(DSL.time(field).eq(new java.sql.Time(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.NotEqual) {
            if (v1 != null) {
                conditions.add(DSL.time(field).notEqual(new java.sql.Time(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.GreaterThan) {
            if (v1 != null) {
                conditions.add(DSL.time(field).greaterThan(new java.sql.Time(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.GreaterThanAndEqual) {
            if (v1 != null) {
                conditions.add(DSL.time(field).greaterOrEqual(new java.sql.Time(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.LessThan) {
            if (v1 != null) {
                conditions.add(DSL.time(field).lessThan(new java.sql.Time(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.LessThanAndEqual) {
            if (v1 != null) {
                conditions.add(DSL.time(field).lessOrEqual(new java.sql.Time(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Like) {
            if (v1 != null) {
                conditions.add(DSL.time(field).eq(new java.sql.Time(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Between) {
            Date v2 = null;
            try {
                v2 = dateFormat.parse(expression.getSecondOperand());
            } catch (ParseException e) {
            }
            if (v1 != null && v2 != null) {
                java.sql.Time t1 = new java.sql.Time(v1.getTime());
                java.sql.Time t2 = new java.sql.Time(v2.getTime());
                if (t1.before(t2)) {
                    conditions.add(DSL.time(field).between(t1, t2));
                } else {
                    conditions.add(DSL.time(field).between(t2, t1));
                }
            }
        }
        return conditions;
    }

    private List<Condition> buildDateCondition(Field<Date> field, Expression expression) {
        List<Condition> conditions = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date v1 = null;
        try {
            v1 = dateFormat.parse(expression.getFirstOperand());
        } catch (ParseException e) {
        }

        if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Equal) {
            if (v1 != null) {
                conditions.add(DSL.date(field).eq(new java.sql.Date(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.NotEqual) {
            if (v1 != null) {
                conditions.add(DSL.date(field).notEqual(new java.sql.Date(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.GreaterThan) {
            if (v1 != null) {
                conditions.add(DSL.date(field).greaterThan(new java.sql.Date(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.GreaterThanAndEqual) {
            if (v1 != null) {
                conditions.add(DSL.date(field).greaterOrEqual(new java.sql.Date(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.LessThan) {
            if (v1 != null) {
                conditions.add(DSL.date(field).lessThan(new java.sql.Date(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.LessThanAndEqual) {
            if (v1 != null) {
                conditions.add(DSL.date(field).lessOrEqual(new java.sql.Date(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Like) {
            if (v1 != null) {
                conditions.add(DSL.date(field).eq(new java.sql.Date(v1.getTime())));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Between) {
            Date v2 = null;
            try {
                v2 = dateFormat.parse(expression.getSecondOperand());
            } catch (ParseException e) {
            }
            if (v1 != null && v2 != null) {
                java.sql.Date d1 = new java.sql.Date(v1.getTime());
                java.sql.Date d2 = new java.sql.Date(v2.getTime());
                if (d1.before(d2)) {
                    conditions.add(DSL.date(field).between(d1, d2));
                } else {
                    conditions.add(DSL.date(field).between(d2, d1));
                }
            }
        }
        return conditions;
    }

    private Condition buildStringCondition(Field<String> field, Expression expression) {
        Condition condition = null;
        String firstValue = expression.getFirstOperand();

        if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Equal) {
            if (expression.getFilter().startsWith("=")) {
                condition = DSL.lower(field).eq(StringUtils.lowerCase(firstValue));
            } else {
                condition = DSL.lower(field).likeRegex(buildLikeRegxExpression(firstValue));
            }
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.NotEqual) {
            condition = field.notEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.GreaterThan) {
            condition = field.greaterThan(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.GreaterThanAndEqual) {
            condition = field.greaterOrEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.LessThan) {
            condition = field.lessThan(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.LessThanAndEqual) {
            condition = field.lessOrEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Like) {
            condition = DSL.lower(field).like(StringUtils.lowerCase(firstValue) + "%");
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Between) {
            String secondValue = expression.getSecondOperand();
            condition = field.between(firstValue, secondValue);
        }
        return condition;
    }

    private Condition buildLongCondition(Field<Long> field, Expression expression) {
        Condition condition = null;
        Long firstValue = Long.parseLong(expression.getFirstOperand());
        if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Equal) {
            condition = field.eq(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.NotEqual) {
            condition = field.notEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.GreaterThan) {
            condition = field.greaterThan(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.GreaterThanAndEqual) {
            condition = field.greaterOrEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.LessThan) {
            condition = field.lessThan(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.LessThanAndEqual) {
            condition = field.lessOrEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Like) {
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Between) {
            Long secondValue = Long.parseLong(expression.getSecondOperand());
            if (firstValue < secondValue) {
                condition = field.between(firstValue, secondValue);
            } else {
                condition = field.between(secondValue, firstValue);
            }
        }
        return condition;
    }

    private Condition buildBooleanCondition(Field<Boolean> field, Expression expression) {
        Condition condition = null;
        Boolean firstValue = Boolean.parseBoolean(expression.getFirstOperand());
        if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Equal) {
            condition = field.eq(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.NotEqual) {
            condition = field.notEqual(firstValue);
        }
        return condition;
    }

    private Condition buildByteCondition(Field<Byte> field, Expression expression) {
        Condition condition = null;
        Byte firstValue = Byte.parseByte(expression.getFirstOperand());
        if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Equal) {
            condition = field.eq(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.NotEqual) {
            condition = field.notEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.GreaterThan) {
            condition = field.greaterThan(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.GreaterThanAndEqual) {
            condition = field.greaterOrEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.LessThan) {
            condition = field.lessThan(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.LessThanAndEqual) {
            condition = field.lessOrEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Like) {
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Between) {
            Byte secondValue = Byte.parseByte(expression.getSecondOperand());
            if (firstValue < secondValue) {
                condition = field.between(firstValue, secondValue);
            } else {
                condition = field.between(secondValue, firstValue);
            }
        }
        return condition;
    }

    private Condition buildShortCondition(Field<Short> field, Expression expression) {
        Condition condition = null;
        Short firstValue = Short.parseShort(expression.getFirstOperand());
        if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Equal) {
            condition = field.eq(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.NotEqual) {
            condition = field.notEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.GreaterThan) {
            condition = field.greaterThan(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.GreaterThanAndEqual) {
            condition = field.greaterOrEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.LessThan) {
            condition = field.lessThan(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.LessThanAndEqual) {
            condition = field.lessOrEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Like) {
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Between) {
            Short secondValue = Short.parseShort(expression.getSecondOperand());
            if (firstValue < secondValue) {
                condition = field.between(firstValue, secondValue);
            } else {
                condition = field.between(secondValue, firstValue);
            }
        }
        return condition;
    }

    private Condition buildIntegerCondition(Field<Integer> field, Expression expression) {
        Condition condition = null;
        Integer firstValue = Integer.parseInt(expression.getFirstOperand());
        if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Equal) {
            condition = field.eq(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.NotEqual) {
            condition = field.notEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.GreaterThan) {
            condition = field.greaterThan(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.GreaterThanAndEqual) {
            condition = field.greaterOrEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.LessThan) {
            condition = field.lessThan(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.LessThanAndEqual) {
            condition = field.lessOrEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Like) {
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Between) {
            Integer secondValue = Integer.parseInt(expression.getSecondOperand());
            if (firstValue < secondValue) {
                condition = field.between(firstValue, secondValue);
            } else {
                condition = field.between(secondValue, firstValue);
            }
        }
        return condition;
    }

    private Condition buildFloatCondition(Field<Float> field, Expression expression) {
        Condition condition = null;
        Float firstValue = Float.parseFloat(expression.getFirstOperand());
        if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Equal) {
            condition = field.eq(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.NotEqual) {
            condition = field.notEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.GreaterThan) {
            condition = field.greaterThan(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.GreaterThanAndEqual) {
            condition = field.greaterOrEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.LessThan) {
            condition = field.lessThan(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.LessThanAndEqual) {
            condition = field.lessOrEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Like) {
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Between) {
            Float secondValue = Float.parseFloat(expression.getSecondOperand());
            if (firstValue < secondValue) {
                condition = field.between(firstValue, secondValue);
            } else {
                condition = field.between(secondValue, firstValue);
            }
        }
        return condition;
    }

    private Condition buildDoubleCondition(Field<Double> field, Expression expression) {
        Condition condition = null;
        Double firstValue = Double.parseDouble(expression.getFirstOperand());
        if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Equal) {
            condition = field.eq(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.NotEqual) {
            condition = field.notEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.GreaterThan) {
            condition = field.greaterThan(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.GreaterThanAndEqual) {
            condition = field.greaterOrEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.LessThan) {
            condition = field.lessThan(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.LessThanAndEqual) {
            condition = field.lessOrEqual(firstValue);
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Like) {
        } else if (expression.getOperator() == com.angkorteam.framework.extension.wicket.extensions.markup.html.repeater.data.table.filter.Operator.Between) {
            Double secondValue = Double.parseDouble(expression.getSecondOperand());
            if (firstValue < secondValue) {
                condition = field.between(firstValue, secondValue);
            } else {
                condition = field.between(secondValue, firstValue);
            }
        }
        return condition;
    }

    private String buildLikeRegxExpression(String searchText) {
        StringBuffer result = new StringBuffer(searchText.length());
        boolean space = false;
        for (char tmp : searchText.trim().toCharArray()) {
            if (tmp == ' ' || tmp == HIDDEN_SPACE) {
                if (!space) {
                    result.append('|');
                    space = true;
                }
            } else {
                space = false;
                result.append(Character.toLowerCase(tmp));
            }
        }
        return "^.*(" + result.toString() + ").*$";
    }

    private static class RecordMapper implements org.jooq.RecordMapper<Record, Map<String, Object>> {

        @Override
        public Map<String, Object> map(Record record) {
            Map<String, Object> item = new HashMap<>();
            for (int i = 0; i < record.fields().length; i++) {
                Field<?> field = record.field(i);
                if (field instanceof TableField) {
                    item.put(((TableField) field).getTable().getName() + "." + field.getName(), record.getValue(i));
                } else {
                    item.put(field.getName(), record.getValue(i));
                }
            }
            return item;
        }
    }

}