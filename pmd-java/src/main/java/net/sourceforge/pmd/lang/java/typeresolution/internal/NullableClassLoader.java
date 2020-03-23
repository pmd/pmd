/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.internal;

public interface NullableClassLoader {

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
