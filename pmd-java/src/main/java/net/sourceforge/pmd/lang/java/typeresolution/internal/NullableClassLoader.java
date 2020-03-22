/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.internal;

public interface NullableClassLoader {

    Class<?> loadClassOrNull(String binaryName);


    class ClassLoaderWrapper implements NullableClassLoader {

        private final ClassLoader classLoader;

        public ClassLoaderWrapper(ClassLoader classLoader) {
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
    }
}
