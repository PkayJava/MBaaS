package com.angkorteam.mbaas.server.bean;

import com.google.common.collect.Lists;
import groovy.lang.GroovyCodeSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;

import java.io.File;
import java.io.IOException;
import java.security.CodeSource;
import java.util.List;
import java.util.Map;

/**
 * Created by socheatkhauv on 10/26/16.
 */
public class GroovyClassLoader extends groovy.lang.GroovyClassLoader {

    public void removeClassCache(String key) {
        if (this.classCache != null) {
            List<String> removes = Lists.newArrayList();
            for (Map.Entry<String, Class> cache : this.classCache.entrySet()) {
                if (StringUtils.startsWith(cache.getKey(), key)) {
                    removes.add(cache.getKey());
                }
            }
            for (String remove : removes) {
                this.classCache.remove(remove);
            }
        }
    }

    @Override
    protected CompilationUnit createCompilationUnit(CompilerConfiguration config, CodeSource source) {
        CompilationUnit compilationUnit = super.createCompilationUnit(config, source);
        if ("/groovy/script".equals(source.getLocation().getPath())) {
            return compilationUnit;
        }
        for (File file : FileUtils.listFiles(new File(FileUtils.getTempDirectory(), Configuration.GROOVY), new String[]{"groovy"}, true)) {
            String srcPath = source.getLocation().getPath();
            String flnPath = null;
            try {
                flnPath = file.getCanonicalPath();
            } catch (IOException e) {
                flnPath = file.getAbsolutePath();
            }
            if (!srcPath.equals(flnPath)) {
                compilationUnit.addSource(file);
            }
        }
        return compilationUnit;
    }

    public void removeSourceCache(String key) {
        if (this.sourceCache != null) {
            try {
                String newKey = "file:" + new File(FileUtils.getTempDirectory().getCanonicalFile(), Configuration.GROOVY).getAbsolutePath() + "/" + StringUtils.replaceAll(key, "\\.", "/") + ".groovy";
                this.sourceCache.remove(newKey);
            } catch (IOException e) {
            }
        }
    }

    public File writeGroovy(String fullJavaClass, String script) {
        File temp = new File(FileUtils.getTempDirectory(), Configuration.GROOVY);
        temp.mkdirs();
        String javaClass = fullJavaClass.substring(fullJavaClass.lastIndexOf(".") + 1);
        String javaDomain = fullJavaClass.substring(0, fullJavaClass.lastIndexOf("."));
        File groovyFolder = new File(temp, StringUtils.replaceAll(javaDomain, "\\.", "/"));
        groovyFolder.mkdirs();
        try {
            File groovy = new File(groovyFolder, javaClass + ".groovy");
            FileUtils.writeStringToFile(groovy, script, "UTF-8");
            return groovy;
        } catch (IOException e) {
            return null;
        }
    }

    public Class<?> compileGroovy(File groovyFile) {
        try {
            GroovyCodeSource source = new GroovyCodeSource(groovyFile, "UTF-8");
            source.setCachable(true);
            Class<?> clazz = null;
            try {
                clazz = parseClass(source, true);
            } catch (MultipleCompilationErrorsException e) {
                e.printStackTrace();
            }
            return clazz;
        } catch (IOException e) {
            return null;
        }
    }

    public Class<?> compileGroovy(String fullJavaClass) {
        File temp = new File(FileUtils.getTempDirectory(), Configuration.GROOVY);
        String sourceFile = StringUtils.replaceAll(fullJavaClass, "\\.", "/") + ".groovy";
        File groovy = new File(temp, sourceFile);
        return compileGroovy(groovy);
    }

}
