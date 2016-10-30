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
import java.util.concurrent.ConcurrentHashMap;

import net.sourceforge.pmd.lang.java.typeresolution.PMDASMClassLoader;
import net.sourceforge.pmd.util.ClasspathClassLoader;

/**
 * Keeps track of the types encountered in a ASTCompilationUnit
 */
public class TypeSet {

    private final PMDASMClassLoader pmdClassLoader;
    private boolean hasAuxclasspath;
    private String pkg;
    private Set<String> imports = new HashSet<>();
    private List<Resolver> resolvers = new ArrayList<>();

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
        hasAuxclasspath = cl instanceof ClasspathClassLoader;
        pmdClassLoader = PMDASMClassLoader.getInstance(cl);
    }

    /**
     * Whether the classloader is using the auxclasspath or not.
     * @return <code>true</code> if the classloader is using the auxclasspath feature
     */
    public boolean hasAuxclasspath() {
        return hasAuxclasspath;
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

        /**
         * Checks if the given class could be resolved by this resolver.
         * Notice, that a resolver's ability to resolve a class does not imply
         * that the class will actually be found and resolved.
         * @param name the name of the class, might be fully classified or not.
         * @return whether the class can be resolved
         */
        boolean couldResolve(String name);
    }

    /**
     * Base Resolver class that support a {@link PMDASMClassLoader} class
     * loader.
     */
    public abstract static class AbstractResolver implements Resolver {
        /** the class loader. */
        protected final PMDASMClassLoader pmdClassLoader;

        /**
         * Creates a new AbstractResolver that uses the given class loader.
         * @param pmdClassLoader the class loader to use
         */
        public AbstractResolver(final PMDASMClassLoader pmdClassLoader) {
            this.pmdClassLoader = pmdClassLoader;
        }

        public boolean couldResolve(final String name) {
            /*
             * Resolvers based on this one, will attempt to load the class from
             * the class loader, so ask him
             */
            return pmdClassLoader.couldResolve(name);
        }
    }

    /**
     * Resolver that tries to resolve the given simple class name with the
     * explicit import statements.
     */
    public static class ExplicitImportResolver extends AbstractResolver {
        private Map<String, String> importStmts;

        /**
         * Creates a new {@link ExplicitImportResolver}.
         * @param pmdClassLoader the class loader to use.
         * @param importStmts the import statements
         */
        public ExplicitImportResolver(PMDASMClassLoader pmdClassLoader, Set<String> importStmts) {
            super(pmdClassLoader);
            
            // unfold imports, to store both FQ and unqualified names mapped to the FQ name
            this.importStmts = new HashMap<>();
            for (final String stmt : importStmts) {
                if (stmt.endsWith("*")) {
                    continue;
                }

                this.importStmts.put(stmt, stmt);
                final int lastDotIdx = stmt.lastIndexOf('.');
                if (lastDotIdx != -1) {
                    this.importStmts.put(stmt.substring(lastDotIdx + 1), stmt);
                }
            }
        }

        @Override
        public Class<?> resolve(String name) throws ClassNotFoundException {
            final String fqName = importStmts.get(name);
            if (fqName != null) {
                return pmdClassLoader.loadClass(fqName);
            }

            throw new ClassNotFoundException("Type " + name + " not found");
        }

        @Override
        public boolean couldResolve(String name) {
            return importStmts.containsKey(name);
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

        @Override
        public boolean couldResolve(String name) {
        	return super.couldResolve(pkg + '.' + name);
        }
    }

    /**
     * Resolver that resolves simple class names from the implicit import of <code>java.lang.*</code>.
     */
    // TODO cite the JLS section on implicit imports
    public static class ImplicitImportResolver extends AbstractResolver {
        /*
         * They aren't so many to bother about memory, but are used all the time,
         * so we worry about performance. On average, you can expect this cache to have ~90% hit ratio
         * unless abusing star imports (import on demand)
         */
        private static final ConcurrentHashMap<String, Class<?>> CLASS_CACHE = new ConcurrentHashMap<>();

        /**
         * Creates a {@link ImplicitImportResolver}
         * @param pmdClassLoader the class loader
         */
        public ImplicitImportResolver(PMDASMClassLoader pmdClassLoader) {
            super(pmdClassLoader);
        }

        @Override
        public Class<?> resolve(String name) throws ClassNotFoundException {
            if (name == null) {
                throw new ClassNotFoundException();
            }

            Class<?> clazz = CLASS_CACHE.get(name);
            if (clazz != null) {
                return clazz;
            }

            clazz = pmdClassLoader.loadClass("java.lang." + name);
            CLASS_CACHE.putIfAbsent(name, clazz);

            return clazz;
        }

        @Override
        public boolean couldResolve(String name) {
        	return super.couldResolve("java.lang." + name);
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
            if (name == null) {
                throw new ClassNotFoundException();
            }

            for (String importStmt : importStmts) {
                if (importStmt.endsWith("*")) {
                    try {
                        String fqClassName = new StringBuilder(importStmt.length() + name.length())
                            .append(importStmt)
                            .replace(importStmt.length() - 1, importStmt.length(), name)
                            .toString();
                        return pmdClassLoader.loadClass(fqClassName);
                    } catch (ClassNotFoundException cnfe) {
                        // ignored as the class could be imported with the next on demand import...
                    }
                }
            }
            throw new ClassNotFoundException("Type " + name + " not found");
        }

        @Override
        public boolean couldResolve(String name) {
        	for (String importStmt : importStmts) {
                if (importStmt.endsWith("*")) {
                    String fqClassName = new StringBuilder(importStmt.length() + name.length())
                        .append(importStmt)
                        .replace(importStmt.length() - 1, importStmt.length(), name)
                        .toString();
                    // can any class be resolved / was never attempted?
                    if (super.couldResolve(fqClassName)) {
                    	return true;
                    }
                }
        	}

        	return false;
        }
    }

    /**
     * Resolver that resolves primitive types such as int or double.
     */
    public static class PrimitiveTypeResolver implements Resolver {
        private Map<String, Class<?>> primitiveTypes = new HashMap<>();
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
                throw new ClassNotFoundException(name);
            }
            return primitiveTypes.get(name);
        }

        @Override
        public boolean couldResolve(String name) {
            return primitiveTypes.containsKey(name);
        }
    }

    /**
     * Resolver that resolves the "void" type.
     */
    public static class VoidResolver implements Resolver {
        @Override
        public Class<?> resolve(String name) throws ClassNotFoundException {
            if ("void".equals(name)) {
                return void.class;
            }
            throw new ClassNotFoundException(name);
        }

        @Override
        public boolean couldResolve(String name) {
            return "void".equals(name);
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
            if (name == null) {
                throw new ClassNotFoundException();
            }
            return pmdClassLoader.loadClass(name);
        }
    }

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

    public Set<String> getExplicitImports() {
        return imports;
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

        for (final Resolver resolver : resolvers) {
            if (resolver.couldResolve(name)) {
                try {
                    return resolver.resolve(name);
                } catch (ClassNotFoundException cnfe) {
                    // ignored, maybe another resolver will find the class
                }
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
