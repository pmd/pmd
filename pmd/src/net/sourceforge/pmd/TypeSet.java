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


    /**
     * TODO should Resolver provide a canResolve() and a resolve()?
     * Requiring 2 calls seems clunky... but so does this
     * throwing an exception for flow control...
     */
    public interface Resolver {
        Class resolve(String name) throws ClassNotFoundException;
    }

    public static class ExplicitImportResolver implements Resolver {
        private Set importStmts;
        public ExplicitImportResolver(Set importStmts) {
            this.importStmts = importStmts;
        }
        public Class resolve(String name) throws ClassNotFoundException {
            for (Iterator i = importStmts.iterator(); i.hasNext();) {
                String importStmt = (String)i.next();
                if (importStmt.endsWith(name)) {
                    return Class.forName(importStmt);
                }
            }
            throw new ClassNotFoundException("Type " + name + " not found");
        }
    }

    public static class CurrentPackageResolver implements Resolver {
        private String pkg;
        public CurrentPackageResolver(String pkg) {
            this.pkg = pkg;
        }
        public Class resolve(String name) throws ClassNotFoundException {
            return Class.forName(pkg + name);
        }
    }

    // TODO cite the JLS section on implicit imports
    public static class ImplicitImportResolver implements Resolver {
        public Class resolve(String name) throws ClassNotFoundException {
            return Class.forName("java.lang." + name);
        }
    }

    public static class ImportOnDemandResolver implements Resolver {
        private Set importStmts;
        public ImportOnDemandResolver(Set importStmts) {
            this.importStmts = importStmts;
        }
        public Class resolve(String name) throws ClassNotFoundException {
            for (Iterator i = importStmts.iterator(); i.hasNext();) {
                String importStmt = (String)i.next();
                if (importStmt.endsWith("*")) {
                    try {
                        String importPkg = importStmt.substring(0, importStmt.indexOf("*")-1);
                        return Class.forName(importPkg + "." + name);
                    } catch (ClassNotFoundException cnfe) {}
                }
            }
            throw new ClassNotFoundException("Type " + name + " not found");
        }
    }

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

    public int getImportsCount() {
        return imports.size();
    }

    public Class findClass(String name) throws ClassNotFoundException {

        Resolver resolver = new ExplicitImportResolver(imports);
        try {
            return resolver.resolve(name);
        } catch (ClassNotFoundException cnfe) {}

        resolver = new CurrentPackageResolver(pkg);
        try {
            return resolver.resolve(name);
        } catch (ClassNotFoundException cnfe) {}

        resolver = new ImplicitImportResolver();
        try {
            return resolver.resolve(name);
        } catch (ClassNotFoundException cnfe) {}
        throw new ClassNotFoundException("Type " + name + " not found");
    }

}
