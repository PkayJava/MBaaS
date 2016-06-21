package com.angkorteam.mbaas.server.nashorn;

import com.angkorteam.framework.extension.spring.SimpleJdbcUpdate;
import com.angkorteam.framework.extension.wicket.markup.html.form.select2.Option;
import com.angkorteam.mbaas.model.entity.Tables;
import com.angkorteam.mbaas.server.Jdbc;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.validation.ValidationError;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by socheat on 4/24/16.
 */
public class JavascripUtils {

    public static void eval(ScriptEngine engine) {
        try {
            List<Class<?>> clazzes = new ArrayList<>();
            clazzes.add(Boolean.class);
            clazzes.add(Byte.class);
            clazzes.add(Short.class);
            clazzes.add(Integer.class);
            clazzes.add(Long.class);
            clazzes.add(Float.class);
            clazzes.add(Double.class);
            clazzes.add(Character.class);
            clazzes.add(String.class);
            clazzes.add(Date.class);
            clazzes.add(LocalDate.class);
            clazzes.add(LocalTime.class);
            clazzes.add(LocalDateTime.class);
            clazzes.add(BigDecimal.class);
            clazzes.add(BigInteger.class);
            clazzes.add(Arrays.class);
            clazzes.add(Collections.class);
            clazzes.add(LinkedHashMap.class);
            clazzes.add(LinkedHashSet.class);
            clazzes.add(Hashtable.class);
            clazzes.add(Vector.class);
            clazzes.add(LinkedList.class);
            clazzes.add(ArrayList.class);
            clazzes.add(HashMap.class);
            clazzes.add(ArrayBlockingQueue.class);
            clazzes.add(SynchronousQueue.class);
            clazzes.add(LinkedBlockingDeque.class);
            clazzes.add(DelayQueue.class);
            clazzes.add(LinkedTransferQueue.class);
            clazzes.add(ArrayDeque.class);
            clazzes.add(ConcurrentLinkedDeque.class);
            clazzes.add(Stack.class);
            clazzes.add(Tables.class);
            clazzes.add(DSL.class);
            clazzes.add(ValidationError.class);
            clazzes.add(StringUtils.class);
            clazzes.add(Option.class);
            clazzes.add(Jdbc.class);
            clazzes.add(UUID.class);
            clazzes.add(SimpleJdbcInsert.class);
            clazzes.add(SimpleJdbcUpdate.class);
            StringBuilder js = new StringBuilder();
            for (Class<?> clazz : clazzes) {
                js.append("var " + clazz.getSimpleName() + " = Java.type('" + clazz.getName() + "'); ");
            }
            engine.eval(js.toString());
        } catch (ScriptException e) {
        }
    }
}
