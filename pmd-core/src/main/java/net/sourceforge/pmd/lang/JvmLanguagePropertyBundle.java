/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.util.PmdClasspathConfig;

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

    // TODO use this to warn about missing JDK sources on classpath for instance.
    public static final PropertyDescriptor<Boolean> ENABLE_CLASSPATH_DIAGNOSTICS
        = PropertyFactory.booleanProperty("enableClasspathDiagnostics")
                         .desc("Whether to warn about missing auxclasspath and other possible configuration mistakes."
                               + " This is enabled by default, but can be disabled in tests for instance.")
                         .defaultValue(true)
                         .build();

    private @Nullable PmdClasspathConfig classpathConfig;

    public JvmLanguagePropertyBundle(Language language) {
        super(language);
        definePropertyDescriptor(AUX_CLASSPATH);
        definePropertyDescriptor(ENABLE_CLASSPATH_DIAGNOSTICS);
    }

    @Override
    public <T> void setProperty(PropertyDescriptor<T> propertyDescriptor, T value) {
        super.setProperty(propertyDescriptor, value);
        if (propertyDescriptor == AUX_CLASSPATH) {
            classpathConfig = null; // reset it.
        }
    }

    /**
     * Return whether PMD should warn when the classpath is the default
     * used for PMD analysis. This hints at incomplete configuration.
     */
    public boolean shouldWarnIfImproperClasspath() {
        return getProperty(ENABLE_CLASSPATH_DIAGNOSTICS);
    }

    /**
     * Set the classpath to use for analysis. This overrides the
     * setting of a classpath as a string via {@link #setProperty(PropertyDescriptor, Object)}.
     * If the parameter is null, the classpath config returned by {@link #getClasspathConfig()}
     * is constructed from the value of the {@link #AUX_CLASSPATH auxClasspath} property.
     */
    public void setClasspathConfig(@Nullable PmdClasspathConfig classpath) {
        this.classpathConfig = classpath;
    }

    /**
     * Return the classpath config, as specified by {@link #setClasspathConfig(PmdClasspathConfig)}.
     */
    public PmdClasspathConfig getClasspathConfig() {
        if (classpathConfig == null) {
            classpathConfig = PmdClasspathConfig.defaultClasspath()
                                                .prependClasspath(getProperty(AUX_CLASSPATH));
        }
        return classpathConfig;
    }


    /**
     * Set the classloader to use for analysis. This overrides the
     * setting of a classpath as a string via {@link #setProperty(PropertyDescriptor, Object)}.
     * If the parameter is null, the classloader returned by {@link #getAnalysisClassLoader()}
     * is constructed from the value of the {@link #AUX_CLASSPATH auxClasspath} property.
     *
     * @deprecated Use {@link #setClasspathConfig(PmdClasspathConfig)}
     */
    @Deprecated
    public void setClassLoader(ClassLoader classLoader) {
        setClasspathConfig(PmdClasspathConfig.thisClassLoaderWillNotBeClosedByPmd(classLoader));
    }

    /**
     * Returns the classloader to use to resolve classes for this language.
     *
     * @deprecated Use {@link #getClasspathConfig()}
     */
    @Deprecated
    public @NonNull ClassLoader getAnalysisClassLoader() {
        return getClasspathConfig().leakClassLoader();
    }
}
