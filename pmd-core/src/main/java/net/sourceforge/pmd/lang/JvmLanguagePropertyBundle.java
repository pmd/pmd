/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.annotation.InternalApi;
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

    private ClassLoader classLoader;

    public JvmLanguagePropertyBundle(Language language) {
        super(language);
        definePropertyDescriptor(AUX_CLASSPATH);
        definePropertyDescriptor(WARN_IF_NO_CLASSPATH);
    }

    @Override
    public <T> void setProperty(PropertyDescriptor<T> propertyDescriptor, T value) {
        super.setProperty(propertyDescriptor, value);
        if (propertyDescriptor == AUX_CLASSPATH) {
            classLoader = null; // reset it.
        }
    }

    /**
     * Create a new {@link ClasspathClassLoader} from the provided auxClasspath.
     * Closing this classloader is the responsibility of the caller. Typically,
     * this will be called when a {@link LanguageProcessor} is created, and
     * closed when it is destroyed.
     */
    @InternalApi
    public ClasspathClassLoader newClasspathClassLoader() throws IOException {
        String auxclasspath = getProperty(AUX_CLASSPATH);
        if (StringUtils.isBlank(auxclasspath) && getProperty(WARN_IF_NO_CLASSPATH)) {
            LOG.warn("auxClasspath property was not set."
                     + " The analysis will use the PMD boot classpath to resolve symbols in analysed sources."
                     + " This may cause problems, including false positives.");
        }
        return new ClasspathClassLoader(auxclasspath, getClass().getClassLoader(), false);
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
        this.classLoader = classLoader;
    }

    /**
     * Returns the classloader to use to resolve classes for this language.
     *
     * @deprecated Use exclusively the string property {@link #AUX_CLASSPATH}.
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
