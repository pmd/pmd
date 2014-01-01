/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.typeresolution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.java.typeresolution.visitors.PMDASMVisitor;

import org.objectweb.asm.ClassReader;

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
 */
public class PMDASMClassLoader extends ClassLoader {
    
    private static PMDASMClassLoader cachedPMDASMClassLoader;
    private static ClassLoader cachedClassLoader;
    
    /**
     * A new PMDASMClassLoader is created for each compilation unit, this method allows to reuse the same
     * PMDASMClassLoader across all the compilation units.
     */
    public static synchronized PMDASMClassLoader getInstance(ClassLoader parent) {
        if (parent == cachedClassLoader) return cachedPMDASMClassLoader;
        cachedClassLoader = parent;
        cachedPMDASMClassLoader = new PMDASMClassLoader(parent);
        return cachedPMDASMClassLoader;
    }
    
    //

    private PMDASMClassLoader(ClassLoader parent) {
    	super(parent);
    }

    /** Caches the names of the classes that we can't load or that don't exist. */
    private final Set<String> dontBother = new HashSet<String>();

    @Override
    public synchronized Class<?> loadClass(String name) throws ClassNotFoundException {
	if (dontBother.contains(name)) {
	    throw new ClassNotFoundException(name);
	}
	try {
	    return super.loadClass(name);
	} catch (ClassNotFoundException e) {
	    dontBother.add(name);
	    throw e;
	}
    }

    public synchronized Map<String, String> getImportedClasses(String name) throws ClassNotFoundException {

        if (dontBother.contains(name)) {
            throw new ClassNotFoundException(name);
        }
        try {
            ClassReader reader = new ClassReader(getResourceAsStream(name.replace('.', '/') + ".class"));
            PMDASMVisitor asmVisitor = new PMDASMVisitor();
            reader.accept(asmVisitor, 0);

            List<String> inner = asmVisitor.getInnerClasses();
            if (inner != null && !inner.isEmpty()) {
                inner = new ArrayList<String>(inner); // to avoid ConcurrentModificationException
                for (String str: inner) {
                    reader = new ClassReader(getResourceAsStream(str.replace('.', '/') + ".class"));
                    reader.accept(asmVisitor, 0);
                }
            }
            return asmVisitor.getPackages();
        } catch (IOException e) {
            dontBother.add(name);
            throw new ClassNotFoundException(name, e);
        }
    }
}