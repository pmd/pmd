/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.internal.util.ClasspathClassLoader;
import net.sourceforge.pmd.internal.util.IOUtil;
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

    private static boolean cacheClassLoader = false;

    @InternalApi
    public static void enabledCacheClassLoader() {
        cacheClassLoader = true;
    }

    private static ClassLoader cachedClassLoader;
    private static String cachedClassLoaderPath;

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
     * Set the classloader to use for analysis. This uses the given classloader in addition to the
     * setting of a classpath as a string via {@link #setProperty(PropertyDescriptor, Object)}.
     * If the parameter is null, the classloader returned by {@link #getAnalysisClassLoader()}
     * is constructed solely from the value of the {@link #AUX_CLASSPATH auxClasspath} property.
     *
     * <p>Note: setting the property {@link #AUX_CLASSPATH} will reset the classloader set previously.</p>
     *
     * @deprecated Since 7.21.0. Use the property {@link #AUX_CLASSPATH} instead.
     */
    @Deprecated
    public void setClassLoader(ClassLoader classLoader) {
        if (classLoader != null) {
            this.classLoader = new UncloseableClassLoader(classLoader);
        }
    }

    /**
     * Returns the classloader to use to resolve classes for this language.
     * @deprecated Since 7.21.0. This will be internalized as future version of PMD won't be based on ClassLoader anymore.
     *             Use the property {@link #AUX_CLASSPATH} instead.
     */
    @Deprecated
    public @NonNull ClassLoader getAnalysisClassLoader() {
        // custom, external classloader - just use this
        if (classLoader != null) {
            return classLoader;
        }

        String auxClasspath = getProperty(JvmLanguagePropertyBundle.AUX_CLASSPATH);
        if (cacheClassLoader) {
            // we use a cached classloader to help performance boost our rule tests. Otherwise a new ClasspathClassLoader
            // will be created for every single rule test. Note: the last cached classloader won't be closed and is leaking...
            if (cachedClassLoader == null || !cachedClassLoaderPath.equals(auxClasspath)) {
                if (cachedClassLoader instanceof UncloseableClassLoader) {
                    IOUtil.tryCloseClassLoader(((UncloseableClassLoader) cachedClassLoader).getDelegate());
                }
                cachedClassLoader = new UncloseableClassLoader(create(auxClasspath));
                cachedClassLoaderPath = auxClasspath;
            }
            classLoader = cachedClassLoader;
        } else {
            classLoader = create(auxClasspath);
        }

        return classLoader;
    }

    private ClassLoader create(String auxClasspath) {
        if (StringUtils.isNotBlank(auxClasspath)) {
            try {
                return new ClasspathClassLoader(auxClasspath, PMDConfiguration.class.getClassLoader());
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        }
        // default fallback, when no analysis classpath has been configured
        return new UncloseableClassLoader(PMDConfiguration.class.getClassLoader());
    }

    // not closeable anymore, so that this classloader can be cached and reused
    private static final class UncloseableClassLoader extends ClassLoader {
        private final ClassLoader delegate;

        private UncloseableClassLoader(ClassLoader delegate) {
            this.delegate = delegate;
        }

        @Override
        public InputStream getResourceAsStream(String name) {
            return delegate.getResourceAsStream(name);
        }

        @Override
        public URL getResource(String name) {
            return delegate.getResource(name);
        }

        @Override
        public String toString() {
            return UncloseableClassLoader.class.getSimpleName() + "[delegate=" + delegate.toString() + "]";
        }

        private ClassLoader getDelegate() {
            return delegate;
        }
    }
}
