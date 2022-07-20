/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.util.ClasspathClassLoader;

/**
 * @author Clément Fournier
 */
public class JvmLanguagePropertyBundle extends LanguagePropertyBundle {

    // TODO make that a PropertyDescriptor<ClassLoader>
    public static final PropertyDescriptor<String> AUX_CLASSPATH
        = PropertyFactory.stringProperty("auxClasspath")
                         .desc("TODO")
                         .defaultValue("")
                         .build();

    private ClassLoader classLoader;

    public JvmLanguagePropertyBundle(Language language) {
        super(language);
        definePropertyDescriptor(AUX_CLASSPATH);
    }

    @Override
    public <T> void setProperty(PropertyDescriptor<T> propertyDescriptor, T value) {
        super.setProperty(propertyDescriptor, value);
        if (propertyDescriptor == AUX_CLASSPATH) {
            classLoader = null; // reset it.
        }
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public @NonNull ClassLoader getAnalysisClassLoader() {
        if (classLoader == null) {
            classLoader = PMDConfiguration.class.getClassLoader();
        }
        String classpath = getProperty(AUX_CLASSPATH);
        if (StringUtils.isNotBlank(classpath)) {
            try {
                classLoader = new ClasspathClassLoader(classpath, classLoader);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return classLoader;
    }
}
