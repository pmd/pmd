/*
 * User: tom
 * Date: Jul 12, 2002
 * Time: 8:08:53 PM
 */
package net.sourceforge.pmd;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Keeps track of the types encountered in a ASTCompilationUnit
 */
public class TypeSet {

    private String pkg;
    private Set imports = new HashSet();

    public void setASTCompilationUnitPackage(String pkg) {
        this.pkg = pkg;
    }

    public String getASTCompilationUnitPackage() {
        return pkg;
    }

    public void addImport(String importString) {
        imports.add(importString);
    }

    public Class findClass(String name) throws ClassNotFoundException {
        // is it explicitly imported?
        for (Iterator i = imports.iterator(); i.hasNext();) {
            String importStmt = (String)i.next();
            if (importStmt.endsWith(name)) {
                return Class.forName(name);
            }
        }
        // is it in the current package?
        try {
            return Class.forName(pkg + name);
        } catch (ClassNotFoundException cnfe) {
        }
        // is it in an implicity imported package - i.e., java.lang?
        // TODO reference the relevant JLS section
        try {
            return Class.forName("java.lang." + name);
        } catch (ClassNotFoundException cnfe) {
        }
        throw new ClassNotFoundException("Type " + name + " not found");
    }

}
