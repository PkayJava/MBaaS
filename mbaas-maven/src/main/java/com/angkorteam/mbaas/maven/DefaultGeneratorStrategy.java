package com.angkorteam.mbaas.maven;

import org.jooq.tools.StringUtils;
import org.jooq.util.Definition;
import org.jooq.util.GeneratorStrategy;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by socheat on 2/20/16.
 */
public class DefaultGeneratorStrategy extends org.jooq.util.DefaultGeneratorStrategy {

    @Override
    public String getJavaClassName(Definition definition, Mode mode) {
        StringBuilder result = new StringBuilder();

        // [#4562] Some characters should be treated like underscore
        result.append(StringUtils.toCamelCase(
                definition.getOutputName()
                        .replace(' ', '_')
                        .replace('-', '_')
                        .replace('.', '_')
        ));

        if (mode == Mode.RECORD) {
            result.append("Record");
        } else if (mode == Mode.DAO) {
            result.append("Dao");
        } else if (mode == Mode.INTERFACE) {
            result.insert(0, "I");
        } else if (mode == Mode.ENUM) {
            result.append("Enum");
        } else if (mode == Mode.POJO) {
            result.append("Pojo");
        } else {
            result.append("Table");
        }

        return result.toString();
    }

    @Override
    public String getJavaClassExtends(Definition definition, GeneratorStrategy.Mode mode) {
        return Object.class.getName();
    }

    @Override
    public List<String> getJavaClassImplements(Definition definition, GeneratorStrategy.Mode mode) {
        return Arrays.asList(Serializable.class.getName(), Cloneable.class.getName());
    }

}
