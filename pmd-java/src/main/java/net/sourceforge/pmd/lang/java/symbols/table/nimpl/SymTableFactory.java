/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.nimpl;


import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;
import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.reflect.ReflectSymInternals;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SemanticChecksLogger;
import net.sourceforge.pmd.lang.java.symbols.table.nimpl.PMultimap.Builder;

final class SymTableFactory {


    private final String thisPackage;
    private final JavaAstProcessor processor;


    SymTableFactory(String thisPackage, JavaAstProcessor processor) {
        this.thisPackage = thisPackage;
        this.processor = processor;
    }

    /** Prepend the package name, handling empty package. */
    String prependPackageName(String pack, String name) {
        return pack.isEmpty() ? name : pack + "." + name;
    }

    SemanticChecksLogger getLogger() {
        return processor.getLogger();
    }

    final JClassSymbol loadClassReportFailure(JavaNode location, String fqcn) {
        JClassSymbol loaded = loadClassOrFail(fqcn);
        if (loaded == null) {
            getLogger().warning(location, SemanticChecksLogger.CANNOT_FIND_CLASSPATH_SYMBOL, fqcn);
        }

        return loaded;
    }

    /** @see SymbolResolver#resolveClassFromCanonicalName(String) */
    @Nullable
    JClassSymbol loadClassOrFail(String fqcn) {
        return processor.getSymResolver().resolveClassFromCanonicalName(fqcn);
    }

    JClassSymbol findSymbolCannotFail(String name) {
        JClassSymbol found = processor.getSymResolver().resolveClassFromCanonicalName(name);
        return found == null ? processor.makeUnresolvedReference(name, 0)
                             : found;
    }

    /**
     * Returns true if the given element can be imported in the current file
     * (it's visible & accessible). This is not a general purpose accessibility
     * check and is only appropriate for imports.
     *
     *
     * <p>We consider protected members inaccessible outside of the package they were declared in,
     * which is an approximation but won't cause problems in practice.
     * In an ACU in another package, the name is accessible only inside classes that inherit
     * from the declaring class. But inheriting from a class makes its static members
     * accessible via simple name too. So this will actually be picked up by some other symbol table
     * when in the subclass. Usages outside of the subclass would have made the compilation fail.
     */
    protected boolean canBeImported(JAccessibleElementSymbol member) {
        int modifiers = member.getModifiers();
        if (Modifier.isPublic(modifiers)) {
            return true;
        } else if (Modifier.isPrivate(modifiers)) {
            return false;
        } else {
            // then it's package private, or protected
            return thisPackage.equals(member.getPackageName());
        }
    }

    private <S extends JElementSymbol> ShadowGroup<S> buildGroup(ShadowGroup<S> parent,
                                                                 PMultimap.Builder<String, S> names) {
        if (names.isEmpty()) {
            return parent;
        } else if (parent instanceof MapShadowGroup) {
            return ((MapShadowGroup<S>) parent).shadow(names.build());
        } else {
            return MapShadowGroup.root(parent, names.build());
        }
    }

    @NonNull
    private NSymbolTable buildTable(NSymbolTable parent, ShadowGroup<JVariableSymbol> vars, ShadowGroup<JMethodSymbol> methods, ShadowGroup<JTypeDeclSymbol> types) {
        if (vars == parent.variables() && methods == parent.methods() && types == parent.types()) {
            return parent;
        } else {
            return new NSymTableImpl(vars, types, methods);
        }
    }

    NSymbolTable importsOnDemand(NSymbolTable parent, Collection<ASTImportDeclaration> importsOnDemand) {
        if (importsOnDemand.isEmpty()) {
            return parent;
        }

        PMultimap.Builder<String, JTypeDeclSymbol> importedTypes = PMultimap.newBuilder();
        PMultimap.Builder<String, JVariableSymbol> importedFields = PMultimap.newBuilder();
        PMultimap.Builder<String, JMethodSymbol> importedMethods = PMultimap.newBuilder();

        Set<String> lazyImportedPackagesAndTypes = new LinkedHashSet<>();

        fillImportOnDemands(importsOnDemand, importedTypes, importedFields, importedMethods, lazyImportedPackagesAndTypes);

        ShadowGroup<JVariableSymbol> vars = buildGroup(parent.variables(), importedFields);
        ShadowGroup<JMethodSymbol> methods = buildGroup(parent.methods(), importedMethods);
        ShadowGroup<JTypeDeclSymbol> types;
        if (lazyImportedPackagesAndTypes.isEmpty()) {
            // then we don't need to use the lazy impl
            types = buildGroup(parent.types(), importedTypes);
        } else {
            types = new LazyShadowGroup<>(parent.types(), importedTypes.getMap(), importedOnDemandLazyResolver(lazyImportedPackagesAndTypes));
        }

        return buildTable(parent, vars, methods, types);
    }

    @NonNull
    private Function<String, List<JTypeDeclSymbol>> importedOnDemandLazyResolver(Set<String> lazyImportedPackagesAndTypes) {
        return simpleName -> {
            for (String pack : lazyImportedPackagesAndTypes) {
                // here 'pack' may be a package or a type name, so we must resolve by canonical name
                String name = prependPackageName(pack, simpleName);
                JClassSymbol sym = processor.getSymResolver().resolveClassFromCanonicalName(name);
                if (sym != null && canBeImported(sym)) {
                    return singletonList(sym);
                }
            }
            return null;
        };
    }

    private void fillImportOnDemands(Iterable<ASTImportDeclaration> importsOnDemand, Builder<String, JTypeDeclSymbol> importedTypes, Builder<String, JVariableSymbol> importedFields, Builder<String, JMethodSymbol> importedMethods, Set<String> importedPackagesAndTypes) {
        for (ASTImportDeclaration anImport : importsOnDemand) {
            assert anImport.isImportOnDemand() : "Expected import on demand: " + anImport;

            if (anImport.isStatic()) {
                // Static-Import-on-Demand Declaration
                // A static-import-on-demand declaration allows all accessible static members of a named type to be imported as needed.
                // includes types members, methods & fields

                @Nullable JClassSymbol containerClass = loadClassReportFailure(anImport, anImport.getImportedName());
                if (containerClass != null) {
                    // populate the inherited state

                    for (JMethodSymbol m : containerClass.getDeclaredMethods()) {
                        if (Modifier.isStatic(m.getModifiers()) && canBeImported(m)) {
                            importedMethods.appendValue(m.getSimpleName(), m);
                        }
                    }

                    for (JFieldSymbol f : containerClass.getDeclaredFields()) {
                        if (Modifier.isStatic(f.getModifiers()) && canBeImported(f)) {
                            importedFields.appendValue(f.getSimpleName(), f);
                        }
                    }

                    for (JClassSymbol t : containerClass.getDeclaredClasses()) {
                        if (Modifier.isStatic(t.getModifiers()) && canBeImported(t)) {
                            importedTypes.appendValue(t.getSimpleName(), t);
                        }
                    }
                }

                // can't be resolved sorry

            } else {
                // Type-Import-on-Demand Declaration
                // This is of the kind <packageName>.*;
                importedPackagesAndTypes.add(anImport.getPackageName());
            }
        }
    }

    NSymbolTable singleImportsSymbolTable(NSymbolTable parent, List<ASTImportDeclaration> singleImports) {
        if (singleImports.isEmpty()) {
            return parent;
        }

        PMultimap.Builder<String, JTypeDeclSymbol> importedTypes = PMultimap.newBuilder();
        PMultimap.Builder<String, JVariableSymbol> importedFields = PMultimap.newBuilder();
        PMultimap.Builder<String, JMethodSymbol> importedMethods = PMultimap.newBuilder();

        fillSingleImports(singleImports, importedTypes, importedFields, importedMethods);

        return buildTable(
            parent,
            buildGroup(parent.variables(), importedFields),
            buildGroup(parent.methods(), importedMethods),
            buildGroup(parent.types(), importedTypes)
        );

    }

    private void fillSingleImports(List<ASTImportDeclaration> singleImports, Builder<String, JTypeDeclSymbol> importedTypes, Builder<String, JVariableSymbol> importedFields, Builder<String, JMethodSymbol> importedMethods) {
        for (ASTImportDeclaration anImport : singleImports) {
            if (anImport.isImportOnDemand()) {
                throw new IllegalArgumentException(anImport.toString());
            }

            String simpleName = anImport.getImportedSimpleName();
            String name = anImport.getImportedName();

            if (anImport.isStatic()) {
                // Single-Static-Import Declaration
                // types, fields or methods having the same name

                int idx = name.lastIndexOf('.');
                assert idx > 0;
                String className = name.substring(0, idx);

                JClassSymbol containerClass = loadClassReportFailure(anImport, className);

                if (containerClass == null) {
                    // the auxclasspath is wrong
                    // bc static imports can't import toplevel types
                    // already reported
                    continue;
                }

                for (JMethodSymbol m : containerClass.getDeclaredMethods()) {
                    if (m.getSimpleName().equals(simpleName) && Modifier.isStatic(m.getModifiers())
                        && canBeImported(m)) {
                        importedMethods.appendValue(m.getSimpleName(), m);
                    }
                }

                JFieldSymbol f = containerClass.getDeclaredField(simpleName);
                if (f != null && Modifier.isStatic(f.getModifiers())) {
                    importedFields.appendValue(f.getSimpleName(), f);
                }

                JClassSymbol c = containerClass.getDeclaredClass(simpleName);
                if (c != null && Modifier.isStatic(c.getModifiers()) && canBeImported(c)) {
                    importedTypes.appendValue(c.getSimpleName(), c);
                }

            } else {
                // Single-Type-Import Declaration
                importedTypes.appendValue(simpleName, findSymbolCannotFail(name));
            }
        }
    }

    NSymbolTable javaLangSymTable(NSymbolTable parent) {
        return NSymTableImpl.withTypes(
            parent,
            new LazyShadowGroup<>(
                parent.types(),
                COMMON_JAVA_LANG,
                simpleName -> listOfNotNull(loadClassOrFail(prependPackageName("java.lang.", simpleName)))
            )
        );
    }

    NSymbolTable samePackageSymTable(NSymbolTable parent) {
        return NSymTableImpl.withTypes(
            parent,
            new LazyShadowGroup<>(
                parent.types(),
                new HashMap<>(),
                simpleName -> listOfNotNull(loadClassOrFail(prependPackageName(thisPackage, simpleName)))
            )
        );
    }

    NSymbolTable typeOnlySymTable(NSymbolTable parent, NodeStream<ASTAnyTypeDeclaration> decl) {
        Builder<String, JTypeDeclSymbol> builder = PMultimap.newBuilder();
        decl.forEach(t -> builder.appendValue(t.getSimpleName(), t.getSymbol()));
        return NSymTableImpl.withTypes(parent, buildGroup(parent.types(), builder));
    }

    NSymbolTable varOnlySymTable(NSymbolTable parent, NodeStream<ASTVariableDeclaratorId> decl) {
        Builder<String, JVariableSymbol> builder = PMultimap.newBuilder();
        decl.forEach(t -> builder.appendValue(t.getVariableName(), t.getSymbol()));
        return NSymTableImpl.withVars(parent, buildGroup(parent.variables(), builder));
    }

    private static <T> List<T> listOfNotNull(T t) {
        return t == null ? emptyList() : singletonList(t);
    }



    // this map is mutable, will be used as the cache of all javaLangSymTables
    private static final Map<String, List<JTypeDeclSymbol>> COMMON_JAVA_LANG;


    static {
        List<Class<?>> classes = Arrays.asList(
            // These are just those that seem the most common,
            // I didn't run any statistics or anything
            // If a type is not in there it will be queried like all
            // the others through the ClassLoader
            java.lang.AssertionError.class,
            java.lang.Boolean.class,
            java.lang.Byte.class,
            java.lang.Character.class,
            java.lang.CharSequence.class,
            java.lang.Class.class,
            java.lang.ClassCastException.class,
            java.lang.ClassLoader.class,
            java.lang.ClassNotFoundException.class,
            java.lang.Cloneable.class,
            java.lang.Comparable.class,
            java.lang.Deprecated.class,
            java.lang.Double.class,
            java.lang.Enum.class,
            java.lang.Error.class,
            java.lang.Exception.class,
            java.lang.Float.class,
            java.lang.FunctionalInterface.class,
            java.lang.IllegalAccessException.class,
            java.lang.IllegalArgumentException.class,
            java.lang.IllegalStateException.class,
            java.lang.IndexOutOfBoundsException.class,
            java.lang.Integer.class,
            java.lang.InternalError.class,
            java.lang.InterruptedException.class,
            java.lang.Iterable.class,
            java.lang.LinkageError.class,
            java.lang.Long.class,
            java.lang.Math.class,
            java.lang.NegativeArraySizeException.class,
            java.lang.NoClassDefFoundError.class,
            java.lang.NoSuchFieldError.class,
            java.lang.NoSuchFieldException.class,
            java.lang.NoSuchMethodError.class,
            java.lang.NoSuchMethodException.class,
            java.lang.NullPointerException.class,
            java.lang.Number.class,
            java.lang.NumberFormatException.class,
            java.lang.Object.class,
            java.lang.OutOfMemoryError.class,
            java.lang.Override.class,
            java.lang.Package.class,
            java.lang.Process.class,
            java.lang.ReflectiveOperationException.class,
            java.lang.Runnable.class,
            java.lang.Runtime.class,
            java.lang.RuntimeException.class,
            java.lang.SafeVarargs.class,
            java.lang.Short.class,
            java.lang.StackOverflowError.class,
            java.lang.String.class,
            java.lang.StringBuffer.class,
            java.lang.StringBuilder.class,
            java.lang.SuppressWarnings.class,
            java.lang.System.class,
            java.lang.Thread.class,
            java.lang.Throwable.class,
            java.lang.Void.class
        );

        Map<String, List<JTypeDeclSymbol>> theJavaLang = new ConcurrentHashMap<>();

        for (Class<?> aClass : classes) {
            JClassSymbol reference = ReflectSymInternals.createSharedSym(aClass);
            theJavaLang.put(aClass.getSimpleName(), listOfNotNull(reference));
        }
        COMMON_JAVA_LANG = theJavaLang;
    }
}
