/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.internal.util.ClasspathClassLoader;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * Base properties class for JVM languages that use a classpath to resolve
 * references. This contributes the "auxClasspath" property.
 *
 * @author Clément Fournier
 */
public class JvmLanguagePropertyBundle extends LanguagePropertyBundle {

    public static final PropertyDescriptor<String> AUX_CLASSPATH
        = PropertyFactory.stringProperty("auxClasspath")
                         .desc("A classpath to use to resolve references to external types in the analysed sources. "
                                   + "Individual paths are separated by ; on Windows and : on other platforms. "
                                   + "All classes of the analysed project should be found on this classpath, including "
                                   + "the compiled classes corresponding to the analyzed sources themselves, and the JDK classes.")
                         .defaultValue("")
                         .build();

    /**
     * @deprecated Since 7.27.0. Only used as fallback, if a ClassLoader is set externally.
     */
    @Deprecated
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

    /**
     * Set the classloader to use for analysis. This overrides the
     * setting of a classpath as a string via {@link #setProperty(PropertyDescriptor, Object)}.
     * If the parameter is null, the classloader returned by {@link #getAnalysisClassLoader()}
     * is constructed from the value of the {@link #AUX_CLASSPATH auxClasspath} property.
     *
     * @deprecated Since 7.27.0. Use {@code setProperty(JvmLanguagePropertyBundle.AUX_CLASSPATH, "file.jar")}
     * instead.
     */
    @Deprecated
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Returns exactly the same classloader set via {@link #setClassLoader(ClassLoader)}.
     * Unlike {@link #getAnalysisClassLoader()}, no modification is performed.
     *
     * @since 7.27.0
     * @see #setClassLoader(ClassLoader)
     * @deprecated Since 7.27.0. Only used to support backwards compatible configuration of classloaders.
     */
    @Deprecated
    protected ClassLoader getClassLoader() {
        return classLoader;
    }

    /**
     * Returns the classloader to use to resolve classes for this language.
     *
     * @deprecated Since 7.27.0. This is language specific and will be moved to the corresponding language.
     * Future versions might not even use a real classloader to resolve class files. Use
     * {@code getProperty(JvmLanguagePropertyBundle.AUX_CLASSPATH)} instead.
     */
    @Deprecated
    public @NonNull ClassLoader getAnalysisClassLoader() {
        if (classLoader != null) {
            return classLoader;
        }
        // load classloader using the property.
        classLoader = PMDConfiguration.class.getClassLoader();
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
