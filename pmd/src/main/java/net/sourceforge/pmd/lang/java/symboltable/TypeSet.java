/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.java.typeresolution.PMDASMClassLoader;

/**
 * Keeps track of the types encountered in a ASTCompilationUnit
 */
public class TypeSet {

    private final PMDASMClassLoader pmdClassLoader;

    /**
     * The {@link TypeSet} provides type resolution for the symbol facade.
     */
    public TypeSet() {
        this(TypeSet.class.getClassLoader());
    }

    /**
     * The {@link TypeSet} provides type resolution for the symbol facade.
     * @param classLoader the class loader to use to search classes (could be an auxiliary class path)
     */
    public TypeSet(ClassLoader classLoader) {
        ClassLoader cl = classLoader;
        if (cl == null) {
            cl = TypeSet.class.getClassLoader();
        }
        pmdClassLoader = PMDASMClassLoader.getInstance(cl);
    }

    /**
     * A resolver that can resolve a class by name. The name can be a simple name or a fully qualified name.
     */
    // TODO should Resolver provide a canResolve() and a resolve()? Requiring 2
    // calls seems clunky... but so does this throwing an exception for flow
    // control...
    public interface Resolver {
        /**
         * Resolve the class by the given name
         *
         * @param name the name of the class, might be fully classified or not.
         * @return the class
         * @throws ClassNotFoundException if the class couldn't be found
         */
        Class<?> resolve(String name) throws ClassNotFoundException;
    }

    /**
     * Base Resolver class that support a {@link PMDASMClassLoader} class
     * loader.
     */
    public static abstract class AbstractResolver implements Resolver {
        /** the class loader. */
        protected final PMDASMClassLoader pmdClassLoader;
        /**
         * Creates a new AbstractResolver that uses the given class loader.
         * @param pmdClassLoader the class loader to use
         */
        public AbstractResolver(PMDASMClassLoader pmdClassLoader) {
            this.pmdClassLoader = pmdClassLoader;
        }
    }

    /**
     * Resolver that tries to resolve the given simple class name with the
     * explicit import statements.
     */
    public static class ExplicitImportResolver extends AbstractResolver {
        private Set<String> importStmts;
        /**
         * Creates a new {@link ExplicitImportResolver}.
         * @param pmdClassLoader the class loader to use.
         * @param importStmts the import statements
         */
        public ExplicitImportResolver(PMDASMClassLoader pmdClassLoader, Set<String> importStmts) {
            super(pmdClassLoader);
            this.importStmts = importStmts;
        }
        @Override
        public Class<?> resolve(String name) throws ClassNotFoundException {
            for (String importStmt : importStmts) {
                if (importStmt.endsWith(name)) {
                    return pmdClassLoader.loadClass(importStmt);
                }
            }
            throw new ClassNotFoundException("Type " + name + " not found");
        }
    }

    /**
     * Resolver that uses the current package to resolve a simple class name.
     */
    public static class CurrentPackageResolver extends AbstractResolver {
        private String pkg;
        /**
         * Creates a new {@link CurrentPackageResolver}
         * @param pmdClassLoader the class loader to use
         * @param pkg the package name
         */
        public CurrentPackageResolver(PMDASMClassLoader pmdClassLoader, String pkg) {
            super(pmdClassLoader);
            this.pkg = pkg;
        }
        @Override
        public Class<?> resolve(String name) throws ClassNotFoundException {
            return pmdClassLoader.loadClass(pkg + '.' + name);
        }
    }

    /**
     * Resolver that resolves simple class names from the implicit import of <code>java.lang.*</code>.
     */
    // TODO cite the JLS section on implicit imports
    public static class ImplicitImportResolver extends AbstractResolver {
        /**
         * Creates a {@link ImplicitImportResolver}
         * @param pmdClassLoader the class loader
         */
        public ImplicitImportResolver(PMDASMClassLoader pmdClassLoader) {
            super(pmdClassLoader);
        }
        @Override
        public Class<?> resolve(String name) throws ClassNotFoundException {
            return pmdClassLoader.loadClass("java.lang." + name);
        }
    }

    /**
     * Resolver that uses the "on demand" import statements.
     */
    public static class ImportOnDemandResolver extends AbstractResolver {
        private Set<String> importStmts;
        /**
         * Creates a {@link ImportOnDemandResolver}
         * @param pmdClassLoader the class loader to use
         * @param importStmts the import statements
         */
        public ImportOnDemandResolver(PMDASMClassLoader pmdClassLoader, Set<String> importStmts) {
            super(pmdClassLoader);
            this.importStmts = importStmts;
        }
        @Override
        public Class<?> resolve(String name) throws ClassNotFoundException {
            for (String importStmt : importStmts) {
                if (importStmt.endsWith("*")) {
                    try {
                        String importPkg = importStmt.substring(0, importStmt.indexOf('*') - 1);
                        return pmdClassLoader.loadClass(importPkg + '.' + name);
                    } catch (ClassNotFoundException cnfe) {
                    }
                }
            }
            throw new ClassNotFoundException("Type " + name + " not found");
        }
    }

    /**
     * Resolver that resolves primitive types such as int or double.
     */
    public static class PrimitiveTypeResolver implements Resolver {
        private Map<String, Class<?>> primitiveTypes = new HashMap<String, Class<?>>();
        /**
         * Creates a new {@link PrimitiveTypeResolver}.
         */
        @SuppressWarnings("PMD.AvoidUsingShortType")
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
        @Override
        public Class<?> resolve(String name) throws ClassNotFoundException {
            if (!primitiveTypes.containsKey(name)) {
                throw new ClassNotFoundException();
            }
            return primitiveTypes.get(name);
        }
    }

    /**
     * Resolver that resolves the "void" type.
     */
    public static class VoidResolver implements Resolver {
        @Override
        public Class<?> resolve(String name) throws ClassNotFoundException {
            if (name.equals("void")) {
                return void.class;
            }
            throw new ClassNotFoundException();
        }
    }

    /**
     * Resolver that simply loads the class by name. This only works if the class name
     * is given as a fully qualified name.
     */
    public static class FullyQualifiedNameResolver extends AbstractResolver {
        /**
         * Creates a {@link FullyQualifiedNameResolver}
         * @param pmdClassLoader the class loader to use
         */
        public FullyQualifiedNameResolver(PMDASMClassLoader pmdClassLoader) {
            super(pmdClassLoader);
        }
        @Override
        public Class<?> resolve(String name) throws ClassNotFoundException {
            return pmdClassLoader.loadClass(name);
        }
    }

    private String pkg;
    private Set<String> imports = new HashSet<String>();
    private List<Resolver> resolvers = new ArrayList<Resolver>();

    public void setASTCompilationUnitPackage(String pkg) {
        this.pkg = pkg;
    }

    public String getASTCompilationUnitPackage() {
        return pkg;
    }

    /**
     * Adds a import to the list of imports
     * @param importString the import to add
     */
    public void addImport(String importString) {
        imports.add(importString);
    }

    public int getImportsCount() {
        return imports.size();
    }

    /**
     * Resolves a class by its name using all known resolvers.
     * @param name the name of the class, can be a simple name or a fully qualified name.
     * @return the class
     * @throws ClassNotFoundException if there is no such class
     */
    public Class<?> findClass(String name) throws ClassNotFoundException {
        // we don't build the resolvers until now since we first want to get all
        // the imports
        if (resolvers.isEmpty()) {
            buildResolvers();
        }

        for (Resolver resolver : resolvers) {
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
        resolvers.add(new ExplicitImportResolver(pmdClassLoader, imports));
        resolvers.add(new CurrentPackageResolver(pmdClassLoader, pkg));
        resolvers.add(new ImplicitImportResolver(pmdClassLoader));
        resolvers.add(new ImportOnDemandResolver(pmdClassLoader, imports));
        resolvers.add(new FullyQualifiedNameResolver(pmdClassLoader));
    }
}
