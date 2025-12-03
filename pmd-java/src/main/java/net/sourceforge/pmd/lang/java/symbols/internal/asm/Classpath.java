/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

/**
 * Classpath abstraction. PMD's symbol resolver uses the classpath to
 * find class files.
 * @deprecated Use {@link net.sourceforge.pmd.lang.impl.Classpath}
 */
@FunctionalInterface
@Deprecated
public interface Classpath extends net.sourceforge.pmd.lang.impl.Classpath {

    /**
     * Returns a classpath instance that uses {@link ClassLoader#getResourceAsStream(String)}
     * to find resources.
     */
    static Classpath forClassLoader(ClassLoader classLoader) {
        return classLoader::getResourceAsStream;
    }

    static Classpath contextClasspath() {
        return forClassLoader(Thread.currentThread().getContextClassLoader());
    }

}
