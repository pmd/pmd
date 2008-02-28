/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.typeresolution;

import net.sourceforge.pmd.typeresolution.visitors.PMDASMVisitor;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * I've refactored this class to not cache the results any more. This is a
 * tradeoff in testing I've found the CPU tradeoff is negligeable. With the
 * cache, large codebases consumed a lot of memory and slowed down greatly when
 * approaching 3,000 classes. I'm adding this comment in case someone is looking
 * at this code and thinks a cache may help.
 */
public class PMDASMClassLoader extends ClassLoader {

    public PMDASMClassLoader(ClassLoader parent) {
    	super(parent);
    }

    private Set<String> dontBother = new HashSet<String>();

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
                inner = new LinkedList<String>(inner); // to avoid ConcurrentModificationException
                for (String str: inner) {
                    reader = new ClassReader(getResourceAsStream(str.replace('.', '/') + ".class"));
                    reader.accept(asmVisitor, 0);
                }
            }
            return asmVisitor.getPackages();
        } catch (IOException e) {
            dontBother.add(name);
            throw new ClassNotFoundException(name);
        }
    }
}