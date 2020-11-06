/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.internal;

import net.sourceforge.pmd.lang.java.typeresolution.PMDASMClassLoader;

/**
 * A classloader that doesn't throw a {@link ClassNotFoundException}
 * to report unresolved classes. This is a big performance improvement
 * for {@link PMDASMClassLoader}, which caches negative cases.
 *
 * <p>See https://github.com/pmd/pmd/pull/2236
 */
public interface NullableClassLoader {

    /**
     * Load a class from its binary name. Returns null if not found.
     */
    Class<?> loadClassOrNull(String binaryName);


    class ClassLoaderWrapper implements NullableClassLoader {

        private final ClassLoader classLoader;

        private ClassLoaderWrapper(ClassLoader classLoader) {
            assert classLoader != null : "Null classloader";
            this.classLoader = classLoader;
        }

        @Override
        public Class<?> loadClassOrNull(String binaryName) {
            try {
                return classLoader.loadClass(binaryName);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }

        public static ClassLoaderWrapper wrapNullable(ClassLoader classLoader) {
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
            return new ClassLoaderWrapper(classLoader);
        }
    }
}
