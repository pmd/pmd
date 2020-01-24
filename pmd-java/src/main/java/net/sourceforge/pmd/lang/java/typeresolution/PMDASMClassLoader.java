/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.objectweb.asm.ClassReader;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.typeresolution.visitors.PMDASMVisitor;

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
@InternalApi
@Deprecated
public final class PMDASMClassLoader extends ClassLoader {

    private static PMDASMClassLoader cachedPMDASMClassLoader;
    private static ClassLoader cachedClassLoader;

    /**
     * Caches the names of the classes that we can't load or that don't exist.
     */
    private final ConcurrentMap<String, Boolean> dontBother = new ConcurrentHashMap<>();

    static {
        registerAsParallelCapable();
    }

    private PMDASMClassLoader(ClassLoader parent) {
        super(parent);
    }

    /**
     * A new PMDASMClassLoader is created for each compilation unit, this method
     * allows to reuse the same PMDASMClassLoader across all the compilation
     * units.
     */
    public static synchronized PMDASMClassLoader getInstance(ClassLoader parent) {
        if (parent.equals(cachedClassLoader)) {
            return cachedPMDASMClassLoader;
        }
        cachedClassLoader = parent;
        cachedPMDASMClassLoader = new PMDASMClassLoader(parent);
        return cachedPMDASMClassLoader;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (dontBother.containsKey(name)) {
            throw new ClassNotFoundException(name);
        }

        try {
            return super.loadClass(name);
        } catch (ClassNotFoundException e) {
            dontBother.put(name, Boolean.TRUE);
            throw e;
        } catch (NoClassDefFoundError e) {
            dontBother.put(name, Boolean.TRUE);
            // rethrow as ClassNotFoundException, as the remaining part just
            // deals with that
            // see also: https://sourceforge.net/p/pmd/bugs/1319/
            throw new ClassNotFoundException(name, e);
        }
    }

    /**
     * Checks if the class loader could resolve a given class name (ie: it
     * doesn't know for sure it will fail). Notice, that the ability to resolve
     * a class does not imply that the class will actually be found and
     * resolved.
     *
     * @param name
     *            the name of the class
     * @return whether the class can be resolved
     */
    public boolean couldResolve(String name) {
        return !dontBother.containsKey(name);
    }

    public synchronized Map<String, String> getImportedClasses(String name) throws ClassNotFoundException {
        if (dontBother.containsKey(name)) {
            throw new ClassNotFoundException(name);
        }
        try (InputStream classResource = getResourceAsStream(name.replace('.', '/') + ".class")) {
            ClassReader reader = new ClassReader(classResource);
            PMDASMVisitor asmVisitor = new PMDASMVisitor(name);
            reader.accept(asmVisitor, 0);

            List<String> inner = asmVisitor.getInnerClasses();
            if (inner != null && !inner.isEmpty()) {
                // to avoid ConcurrentModificationException
                inner = new ArrayList<>(inner);
                for (String str : inner) {
                    try (InputStream innerClassStream = getResourceAsStream(str.replace('.', '/') + ".class")) {
                        if (innerClassStream != null) {
                            reader = new ClassReader(innerClassStream);
                            reader.accept(asmVisitor, 0);
                        }
                    }
                }
            }
            return asmVisitor.getPackages();
        } catch (IOException e) {
            dontBother.put(name, Boolean.TRUE);
            throw new ClassNotFoundException(name, e);
        }
    }
}
