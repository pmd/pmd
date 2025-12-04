/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.util.PmdClasspathWrapper;

/**
 * Base properties class for JVM languages that use a classpath to resolve
 * references. This contributes the "auxClasspath" property.
 *
 * @author Clément Fournier
 */
public class JvmLanguagePropertyBundle extends LanguagePropertyBundle {
    private static final Logger LOG = LoggerFactory.getLogger(JvmLanguagePropertyBundle.class);

    public static final PropertyDescriptor<String> AUX_CLASSPATH
        = PropertyFactory.stringProperty("auxClasspath")
                         .desc("A classpath to use to resolve references to external types in the analysed sources. "
                                   + "Individual paths are separated by ; on Windows and : on other platforms. "
                                   + "All classes of the analysed project should be found on this classpath, including "
                                   + "the compiled classes corresponding to the analyzed sources themselves, and the JDK classes.")
                         .defaultValue("")
                         .build();

    public static final PropertyDescriptor<Boolean> WARN_IF_NO_CLASSPATH
        = PropertyFactory.booleanProperty("warnIfNoClasspath")
                         .desc("Whether to warn about missing auxclasspath. This is enabled by default, but disabled in tests for instance.")
                         .defaultValue(true)
                         .build();

    private @Nullable PmdClasspathWrapper classpathWrapper;

    public JvmLanguagePropertyBundle(Language language) {
        super(language);
        definePropertyDescriptor(AUX_CLASSPATH);
        definePropertyDescriptor(WARN_IF_NO_CLASSPATH);
    }

    @Override
    public <T> void setProperty(PropertyDescriptor<T> propertyDescriptor, T value) {
        super.setProperty(propertyDescriptor, value);
        if (propertyDescriptor == AUX_CLASSPATH) {
            classpathWrapper = null; // reset it.
        }
    }

    /**
     * Set the classloader to use for analysis. This overrides the
     * setting of a classpath as a string via {@link #setProperty(PropertyDescriptor, Object)}.
     * If the parameter is null, the classloader returned by {@link #getClasspathWrapper()}
     * is constructed from the value of the {@link #AUX_CLASSPATH auxClasspath} property.
     */
    public void setClasspathWrapper(@Nullable PmdClasspathWrapper classpath) {
        this.classpathWrapper = classpath;
    }

    /**
     * Return the classpath wrapper. Note that a language property bundle
     * is not considered to own the classpath wrapper and will not close
     * it itself. It is only passing this data from pmd core to pmd java.
     * The Java language processor will close it.
     */
    public PmdClasspathWrapper getClasspathWrapper() {
        if (classpathWrapper == null) {
            classpathWrapper = PmdClasspathWrapper.bootClasspath();
            classpathWrapper.prependClasspath(getProperty(AUX_CLASSPATH));
        }
        return classpathWrapper;
    }


    /**
     * Set the classloader to use for analysis. This overrides the
     * setting of a classpath as a string via {@link #setProperty(PropertyDescriptor, Object)}.
     * If the parameter is null, the classloader returned by {@link #getAnalysisClassLoader()}
     * is constructed from the value of the {@link #AUX_CLASSPATH auxClasspath} property.
     *
     * @deprecated Use exclusively the string property {@link #AUX_CLASSPATH}.
     */
    @Deprecated
    public void setClassLoader(ClassLoader classLoader) {
        setClasspathWrapper(PmdClasspathWrapper.thisClassLoaderWillNotBeClosedByPmd(classLoader));
    }

    /**
     * Returns the classloader to use to resolve classes for this language.
     *
     * @deprecated Use exclusively the string property {@link #AUX_CLASSPATH}.
     */
    @Deprecated
    public @NonNull ClassLoader getAnalysisClassLoader() {
        return getClasspathWrapper().getClassLoader();
    }
}
