/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.nimpl;


import static net.sourceforge.pmd.internal.util.AssertionUtil.isValidJavaPackageName;
import static net.sourceforge.pmd.lang.java.symbols.table.nimpl.Resolvers.newMapBuilder;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameter;
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameters;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AstDisambiguationPass;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;
import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SemanticChecksLogger;
import net.sourceforge.pmd.lang.java.symbols.table.nimpl.MostlySingularMultimap.Builder;
import net.sourceforge.pmd.util.CollectionUtil;

final class SymTableFactory {


    private final String thisPackage;
    private final JavaAstProcessor processor;


    SymTableFactory(String thisPackage, JavaAstProcessor processor) {
        this.thisPackage = thisPackage;
        this.processor = processor;
    }

    // <editor-fold defaultstate="collapsed" desc="Utilities for classloading">


    public void disambig(NodeStream<? extends JavaNode> nodes) {
        AstDisambiguationPass.disambig(processor, nodes);
    }

    public void disambig(JavaNode node) {
        AstDisambiguationPass.disambig(processor, node);
    }

    SemanticChecksLogger getLogger() {
        return processor.getLogger();
    }

    final JClassSymbol loadClassReportFailure(JavaNode location, String fqcn) {
        JClassSymbol loaded = loadClassOrFail(fqcn);
        if (loaded == null) {
            getLogger().warning(location, SemanticChecksLogger.CANNOT_RESOLVE_SYMBOL, fqcn);
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

    protected boolean canBeImported(JAccessibleElementSymbol member) {
        return Resolvers.canBeImportedIn(thisPackage, member);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Shadowing essentials">

    private <N, S extends JElementSymbol> Builder<String, S> groupByName(Iterable<? extends N> tparams, Function<? super N, ? extends S> symbolFetcher) {
        return Resolvers.<S>newMapBuilder().groupBy(CollectionUtil.map(tparams, symbolFetcher), JElementSymbol::getSimpleName);

    }

    private <S extends JElementSymbol> Builder<String, S> groupByName(Iterable<? extends S> tparams) {
        return Resolvers.<S>newMapBuilder().groupBy(tparams, JElementSymbol::getSimpleName);
    }


    @NonNull
    private NSymbolTable buildTable(NSymbolTable parent,
                                    ShadowGroup<JVariableSymbol> vars,
                                    ShadowGroup<JMethodSymbol> methods,
                                    ShadowGroup<JTypeDeclSymbol> types) {
        if (vars == parent.variables() && methods == parent.methods() && types == parent.types()) {
            return parent;
        } else {
            return new NSymbolTableImpl(vars, types, methods);
        }
    }

    private static <S> ShadowGroup<S> augment(ShadowGroup<S> parent, boolean shadowBarrier, Builder<String, S> symbols) {
        if (symbols.isEmpty() && !shadowBarrier) {
            return parent;
        }
        return new SimpleShadowGroup<>(parent, shadowBarrier, Resolvers.mapResolver(symbols));
    }

    private static <S> ShadowGroup<S> augment(ShadowGroup<S> parent, boolean shadowBarrier, NameResolver<S> resolver) {
        return new SimpleShadowGroup<>(parent, shadowBarrier, resolver);
    }

    private static <S> ShadowGroup<S> augment(ShadowGroup<S> parent, String key, boolean shadowBarrier, S symbol) {
        return new SimpleShadowGroup<>(parent, shadowBarrier, Resolvers.singleton(key, symbol));
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Import tables">


    NSymbolTable importsOnDemand(NSymbolTable parent, Collection<ASTImportDeclaration> importsOnDemand) {
        if (importsOnDemand.isEmpty()) {
            return parent;
        }

        Builder<String, JTypeDeclSymbol> importedTypes = newMapBuilder();
        Builder<String, JVariableSymbol> importedFields = newMapBuilder();
        Builder<String, JMethodSymbol> importedMethods = newMapBuilder();

        Set<String> lazyImportedPackagesAndTypes = new LinkedHashSet<>();

        fillImportOnDemands(importsOnDemand, importedTypes, importedFields, importedMethods, lazyImportedPackagesAndTypes);

        ShadowGroup<JVariableSymbol> vars = augment(parent.variables(), true, importedFields);
        ShadowGroup<JMethodSymbol> methods = augment(parent.methods(), true, importedMethods);
        ShadowGroup<JTypeDeclSymbol> types;
        if (lazyImportedPackagesAndTypes.isEmpty()) {
            // then we don't need to use the lazy impl
            types = augment(parent.types(), true, importedTypes);
        } else {
            types = new CachedShadowGroup<>(
                parent.types(),
                importedTypes.getMutableMap(),
                Resolvers.importedOnDemand(lazyImportedPackagesAndTypes, processor.getSymResolver(), thisPackage),
                true
            );
        }

        return buildTable(parent, vars, methods, types);
    }

    NSymbolTable singleImportsSymbolTable(NSymbolTable parent, List<ASTImportDeclaration> singleImports) {
        if (singleImports.isEmpty()) {
            return parent;
        }

        Builder<String, JTypeDeclSymbol> importedTypes = newMapBuilder();
        Builder<String, JVariableSymbol> importedFields = newMapBuilder();
        Builder<String, JMethodSymbol> importedMethods = newMapBuilder();

        fillSingleImports(singleImports, importedTypes, importedFields, importedMethods);

        return buildTable(
            parent,
            augment(parent.variables(), true, importedFields),
            augment(parent.methods(), true, importedMethods),
            augment(parent.types(), true, importedTypes)
        );

    }

    private void fillImportOnDemands(Iterable<ASTImportDeclaration> importsOnDemand,
                                     Builder<String, JTypeDeclSymbol> importedTypes,
                                     Builder<String, JVariableSymbol> importedFields,
                                     Builder<String, JMethodSymbol> importedMethods,
                                     Set<String> importedPackagesAndTypes) {
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

    private void fillSingleImports(List<ASTImportDeclaration> singleImports,
                                   Builder<String, JTypeDeclSymbol> importedTypes,
                                   Builder<String, JVariableSymbol> importedFields,
                                   Builder<String, JMethodSymbol> importedMethods) {
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
        return typesInPackage(parent, "java.lang");
    }

    NSymbolTable samePackageSymTable(NSymbolTable parent) {
        return typesInPackage(parent, thisPackage);
    }

    @NonNull
    private NSymbolTable typesInPackage(NSymbolTable parent, String packageName) {
        assert isValidJavaPackageName(packageName) : "Not a package name: " + packageName;

        return NSymbolTableImpl.withTypes(
            parent,
            new CachedShadowGroup<>(
                parent.types(),
                Resolvers.packageResolver(processor.getSymResolver(), packageName),
                true
            )
        );
    }

    @NonNull
    private NSymbolTable typeSymTable(NSymbolTable parent, Builder<String, JTypeDeclSymbol> map) {
        return NSymbolTableImpl.withTypes(parent, augment(parent.types(), true, map));
    }

    NSymbolTable typeBody(NSymbolTable parent, @NonNull JClassSymbol sym) {

        Pair<NameResolver<JTypeDeclSymbol>, NameResolver<JVariableSymbol>> resolvers = Resolvers.classAndFieldResolvers(sym);

        ShadowGroup<JTypeDeclSymbol> types = parent.types();
        types = augment(types, true, resolvers.getLeft());
        types = augment(types, true, groupByName(sym.getTypeParameters()));

        ShadowGroup<JVariableSymbol> fields = augment(parent.variables(), true, resolvers.getRight());
        ShadowGroup<JMethodSymbol> methods = new CachedShadowGroup<>(parent.methods(), Resolvers.methodResolver(sym), true);

        return buildTable(parent, fields, methods, types);
    }

    // </editor-fold>


    NSymbolTable typeOnlySymTable(NSymbolTable parent, NodeStream<ASTAnyTypeDeclaration> decls) {
        return typeSymTable(parent, groupByName(decls, ASTAnyTypeDeclaration::getSymbol));
    }

    NSymbolTable typeOnlySymTable(NSymbolTable parent, JClassSymbol sym) {
        return NSymbolTableImpl.withTypes(parent, augment(parent.types(), true, Resolvers.singleton(sym.getSimpleName(), sym)));
    }

    NSymbolTable typeHeader(NSymbolTable parent, JClassSymbol sym) {
        return NSymbolTableImpl.withTypes(parent, augment(parent.types(), true, groupByName(sym.getTypeParameters())));
    }

    /**
     * Symbol table for a body declaration. This places a shadowing
     * group for variables, ie, nested variable shadowing groups will
     * be merged into it but not into the parent. This implements shadowing
     * of fields by local variables and formals.
     */
    NSymbolTable bodyDeclaration(NSymbolTable parent, @Nullable ASTFormalParameters formals, @Nullable ASTTypeParameters tparams) {
        return new NSymbolTableImpl(
            augment(parent.variables(), true, groupByName(ASTList.orEmptyStream(formals), fp -> fp.getVarId().getSymbol())),
            augment(parent.types(), true, groupByName(ASTList.orEmptyStream(tparams), ASTTypeParameter::getSymbol)),
            parent.methods()
        );
    }

    NSymbolTable recordCtor(NSymbolTable parent, JConstructorSymbol symbol) {
        return NSymbolTableImpl.withVars(parent, augment(parent.variables(), true, groupByName(symbol.getFormalParameters())));
    }

    /**
     * Local vars are merged into the parent shadowing group. They don't
     * shadow other local vars, they conflict with them.
     */
    NSymbolTable localVarSymTable(NSymbolTable parent, NodeStream<ASTVariableDeclaratorId> ids) {
        List<JVariableSymbol> list = ids.toList(ASTVariableDeclaratorId::getSymbol);
        if (list.size() == 1) {
            JVariableSymbol sym = list.get(0);
            return NSymbolTableImpl.withVars(parent, augment(parent.variables(), sym.getSimpleName(), false, sym));
        }
        return NSymbolTableImpl.withVars(parent, augment(parent.variables(), false, groupByName(list)));
    }

    NSymbolTable localTypeSymTable(NSymbolTable parent, JClassSymbol sym) {
        return NSymbolTableImpl.withTypes(parent, augment(parent.types(), sym.getSimpleName(), false, sym));
    }

    NSymbolTable localVarSymTable(NSymbolTable parent, ASTVariableDeclaratorId id) {
        JVariableSymbol symbol = id.getSymbol();
        return NSymbolTableImpl.withVars(parent, augment(parent.variables(), symbol.getSimpleName(), false, symbol));
    }

}
