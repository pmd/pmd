/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package net.sourceforge.pmd.symboltable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
                String importStmt = (String) i.next();
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
                String importStmt = (String) i.next();
                if (importStmt.endsWith("*")) {
                    try {
                        String importPkg = importStmt.substring(0, importStmt.indexOf("*") - 1);
                        return Class.forName(importPkg + "." + name);
                    } catch (ClassNotFoundException cnfe) {
                    }
                }
            }
            throw new ClassNotFoundException("Type " + name + " not found");
        }
    }

    public static class PrimitiveTypeResolver implements Resolver {
        private Map primitiveTypes = new HashMap();

        public PrimitiveTypeResolver() {
            primitiveTypes.put("int", int.class);
            primitiveTypes.put("float", float.class);
            primitiveTypes.put("double", double.class);
            primitiveTypes.put("long", long.class);
            primitiveTypes.put("boolean", boolean.class);
            primitiveTypes.put("byte", byte.class);
            primitiveTypes.put("short", short.class);
            primitiveTypes.put("char", char.class);
        }

        public Class resolve(String name) throws ClassNotFoundException {
            if (!primitiveTypes.containsKey(name)) {
                throw new ClassNotFoundException();
            }
            return (Class) primitiveTypes.get(name);
        }
    }

    public static class VoidResolver implements Resolver {
        public Class resolve(String name) throws ClassNotFoundException {
            if (name.equals("void")) {
                return void.class;
            }
            throw new ClassNotFoundException();
        }
    }

    public static class FullyQualifiedNameResolver implements Resolver {
        public Class resolve(String name) throws ClassNotFoundException {
            return Class.forName(name);
        }
    }

    private String pkg;
    private Set imports = new HashSet();
    private List resolvers = new ArrayList();

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
        // we don't build the resolvers until now since we first want to get all the imports
        if (resolvers.isEmpty()) {
            buildResolvers();
        }

        for (Iterator i = resolvers.iterator(); i.hasNext();) {
            Resolver resolver = (Resolver) i.next();
            try {
                return resolver.resolve(name);
            } catch (ClassNotFoundException cnfe) {
            }
        }

        throw new ClassNotFoundException("Type " + name + " not found");
    }

    private void buildResolvers() {
        resolvers.add(new PrimitiveTypeResolver());
        resolvers.add(new VoidResolver());
        resolvers.add(new ExplicitImportResolver(imports));
        resolvers.add(new CurrentPackageResolver(pkg));
        resolvers.add(new ImplicitImportResolver());
        resolvers.add(new ImportOnDemandResolver(imports));
        resolvers.add(new FullyQualifiedNameResolver());
    }

}
