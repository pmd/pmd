/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolFactory;

/**
 * Resolves symbols by asking a classloader.
 */
public class ClasspathSymbolResolver implements SymbolResolver {

    private final NonThrowingClassLoader classLoader;
    private final SymbolFactory factory;

    public ClasspathSymbolResolver(ClassLoader classLoader, SymbolFactory factory) {
        this.classLoader = NonThrowingClassLoader.getInstance(classLoader);
        this.factory = factory;
    }


    @Override
    public @Nullable JClassSymbol resolveClassFromBinaryName(@NonNull String binaryName) {
        return ReflectedSymbols.getClassSymbol(factory, classLoader.loadClassOrNull(binaryName));
    }


    /*
     * I've refactored this class to not cache the results any more. This is a
     * tradeoff in testing I've found the CPU tradeoff is negligeable. With the
     * cache, large codebases consumed a lot of memory and slowed down greatly when
     * approaching 3,000 classes. I'm adding this comment in case someone is looking
     * at this code and thinks a cache may help.
     *
     * see: git show 9e7deee88f63870a1de2cd86458278a027deb6d6
     *
     * However, there seems to be a big performance improvement by caching
     * the negative cases only. The cache is shared between loadClass and getImportedClasses,
     * as they are using the same (parent) class loader, e.g. if the class foo.Bar cannot be loaded,
     * then the resource foo/Bar.class will not exist, too.
     *
     * Note: since git show 46ad3a4700b7a233a177fa77d08110127a85604c the cache is using
     * a concurrent hash map to avoid synchronizing on the class loader instance.
     */
    private static final class NonThrowingClassLoader extends ClassLoader {

        private static final Object CACHE_LOCK = new Object();
        private static NonThrowingClassLoader cachedPMDASMClassLoader;
        private static ClassLoader cachedClassLoader;
        /**
         * Caches the names of the classes that we can't load or that don't exist.
         */
        private final ConcurrentMap<String, Boolean> dontBother = new ConcurrentHashMap<>();


        static {
            registerAsParallelCapable();
        }


        private NonThrowingClassLoader(ClassLoader parent) {
            super(parent);
        }

        /**
         * Returns null instead of throwing a CNFE. Constructor of the exception
         * is a *very* significant performance bottleneck (more than half of the
         * runtime of this method, given that this method is very often called).
         */
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            Class<?> aClass = loadClassOrNull(name);
            if (aClass == null) {
                throw new ClassNotFoundException(name);
            }
            return aClass;
        }

        /**
         * Not throwing CNFEs to represent failure makes a huge performance
         * difference. Typeres as a whole is 2x faster.
         */
        @Nullable
        public Class<?> loadClassOrNull(String name) {
            if (dontBother.containsKey(name)) {
                return null;
            }

            try {
                return super.loadClass(name);
            } catch (ClassNotFoundException | LinkageError e) {
                dontBother.put(name, Boolean.TRUE);
                return null;
            }
        }

        /**
         * A new PMDASMClassLoader is created for each compilation unit, this method
         * allows to reuse the same PMDASMClassLoader across all the compilation
         * units.
         */
        static NonThrowingClassLoader getInstance(ClassLoader parent) {
            if (parent instanceof NonThrowingClassLoader) {
                return (NonThrowingClassLoader) parent;
            }
            synchronized (CACHE_LOCK) {
                if (parent.equals(cachedClassLoader)) {
                    return cachedPMDASMClassLoader;
                }
                cachedClassLoader = parent;
                cachedPMDASMClassLoader = new NonThrowingClassLoader(parent);

                return cachedPMDASMClassLoader;
            }
        }
    }
}
