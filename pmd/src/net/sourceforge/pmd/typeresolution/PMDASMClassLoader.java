/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.typeresolution;

import net.sourceforge.pmd.typeresolution.visitors.PMDASMVisitor;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
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

    public PMDASMClassLoader() {
    }

    private Set dontBother = new HashSet();

    public Map getImportedClasses(String name) throws ClassNotFoundException {

        if (dontBother.contains(name)) {
            throw new ClassNotFoundException(name);
        }
        try {
            ClassReader reader = new ClassReader(getResourceAsStream(name.replace('.', '/') + ".class"));
            PMDASMVisitor asmVisitor = new PMDASMVisitor();
            reader.accept(asmVisitor, 0);

            List inner = asmVisitor.getInnerClasses();
            if (inner != null && !inner.isEmpty()) {
                for (int ix = 0; ix < inner.size(); ix++) {
                    String str = (String) inner.get(ix);
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