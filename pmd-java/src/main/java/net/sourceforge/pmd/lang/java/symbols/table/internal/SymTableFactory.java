/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;


import static net.sourceforge.pmd.internal.util.AssertionUtil.isValidJavaPackageName;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.Resolvers.newMapBuilder;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.internal.MostlySingularMultimap.Builder;

final class SymTableFactory {


    private final String thisPackage;
    private final JavaAstProcessor processor;

    private static final ShadowGroupBuilder<JTypeDeclSymbol> TYPES = new ShadowGroupBuilder<>(JTypeDeclSymbol::getSimpleName);
    private static final ShadowGroupBuilder<JVariableSymbol> VARS = new ShadowGroupBuilder<>(JElementSymbol::getSimpleName);
    private static final ShadowGroupBuilder<JMethodSymbol> METHODS = new ShadowGroupBuilder<>(JElementSymbol::getSimpleName);


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

    @NonNull
    private JSymbolTable buildTable(JSymbolTable parent,
                                    ShadowGroup<JVariableSymbol> vars,
                                    ShadowGroup<JMethodSymbol> methods,
                                    ShadowGroup<JTypeDeclSymbol> types) {
        if (vars == parent.variables() && methods == parent.methods() && types == parent.types()) {
            return parent;
        } else {
            return new SymbolTableImpl(vars, types, methods);
        }
    }

    JSymbolTable importsOnDemand(JSymbolTable parent, Collection<ASTImportDeclaration> importsOnDemand) {
        if (importsOnDemand.isEmpty()) {
            return parent;
        }

        Builder<String, JTypeDeclSymbol> importedTypes = newMapBuilder();
        Builder<String, JVariableSymbol> importedFields = newMapBuilder();
        Builder<String, JMethodSymbol> importedMethods = newMapBuilder();

        Set<String> lazyImportedPackagesAndTypes = new LinkedHashSet<>();

        fillImportOnDemands(importsOnDemand, importedTypes, importedFields, importedMethods, lazyImportedPackagesAndTypes);

        ShadowGroup<JVariableSymbol> vars = VARS.shadow(parent.variables(), importedFields);
        ShadowGroup<JMethodSymbol> methods = METHODS.shadow(parent.methods(), importedMethods);
        ShadowGroup<JTypeDeclSymbol> types;
        if (lazyImportedPackagesAndTypes.isEmpty()) {
            // then we don't need to use the lazy impl
            types = TYPES.shadow(parent.types(), importedTypes);
        } else {
            types = TYPES.augmentWithCache(
                parent.types(),
                true,
                importedTypes.getMutableMap(),
                Resolvers.importedOnDemand(lazyImportedPackagesAndTypes, processor.getSymResolver(), thisPackage)
            );
        }

        return buildTable(parent, vars, methods, types);
    }

    JSymbolTable singleImportsSymbolTable(JSymbolTable parent, List<ASTImportDeclaration> singleImports) {
        if (singleImports.isEmpty()) {
            return parent;
        }

        Builder<String, JTypeDeclSymbol> importedTypes = newMapBuilder();
        Builder<String, JVariableSymbol> importedFields = newMapBuilder();
        Builder<String, JMethodSymbol> importedMethods = newMapBuilder();

        fillSingleImports(singleImports, importedTypes, importedFields, importedMethods);

        return buildTable(
            parent,
            VARS.shadow(parent.variables(), importedFields),
            METHODS.shadow(parent.methods(), importedMethods),
            TYPES.shadow(parent.types(), importedTypes)
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

    JSymbolTable javaLangSymTable(JSymbolTable parent) {
        return typesInPackage(parent, "java.lang");
    }

    JSymbolTable samePackageSymTable(JSymbolTable parent) {
        return typesInPackage(parent, thisPackage);
    }

    @NonNull
    private JSymbolTable typesInPackage(JSymbolTable parent, String packageName) {
        assert isValidJavaPackageName(packageName) : "Not a package name: " + packageName;

        return SymbolTableImpl.withTypes(
            parent,
            new CachedShadowGroup<>(
                parent.types(),
                Resolvers.packageResolver(processor.getSymResolver(), packageName),
                true
            )
        );
    }

    JSymbolTable typeBody(JSymbolTable parent, @NonNull JClassSymbol sym) {

        Pair<NameResolver<JTypeDeclSymbol>, NameResolver<JVariableSymbol>> resolvers = Resolvers.classAndFieldResolvers(sym);

        ShadowGroup<JTypeDeclSymbol> types = parent.types();
        types = TYPES.shadow(types, sym); // self name
        types = TYPES.shadow(types, resolvers.getLeft()); // inner & inherited class names
        types = TYPES.shadow(types, TYPES.groupByName(sym.getTypeParameters()));

        ShadowGroup<JVariableSymbol> fields = VARS.shadow(parent.variables(), resolvers.getRight());
        ShadowGroup<JMethodSymbol> methods = METHODS.augmentWithCache(parent.methods(), true, Resolvers.methodResolver(sym));

        return buildTable(parent, fields, methods, types);
    }

    JSymbolTable typeOnlySymTable(JSymbolTable parent, NodeStream<ASTAnyTypeDeclaration> decls) {
        return SymbolTableImpl.withTypes(parent, TYPES.shadow(parent.types(), TYPES.groupByName(decls, ASTAnyTypeDeclaration::getSymbol)));
    }

    JSymbolTable typeOnlySymTable(JSymbolTable parent, JClassSymbol sym) {
        return SymbolTableImpl.withTypes(parent, TYPES.shadow(parent.types(), TYPES.groupByName(sym)));
    }

    JSymbolTable typeHeader(JSymbolTable parent, JClassSymbol sym) {
        return SymbolTableImpl.withTypes(parent, TYPES.shadow(parent.types(), TYPES.groupByName(sym.getTypeParameters())));
    }

    /**
     * Symbol table for a body declaration. This places a shadowing
     * group for variables, ie, nested variable shadowing groups will
     * be merged into it but not into the parent. This implements shadowing
     * of fields by local variables and formals.
     */
    JSymbolTable bodyDeclaration(JSymbolTable parent, @Nullable ASTFormalParameters formals, @Nullable ASTTypeParameters tparams) {
        return new SymbolTableImpl(
            VARS.shadow(parent.variables(), VARS.groupByName(ASTList.orEmptyStream(formals), fp -> fp.getVarId().getSymbol())),
            TYPES.shadow(parent.types(), TYPES.groupByName(ASTList.orEmptyStream(tparams), ASTTypeParameter::getSymbol)),
            parent.methods()
        );
    }

    JSymbolTable recordCtor(JSymbolTable parent, JConstructorSymbol symbol) {
        return SymbolTableImpl.withVars(parent, VARS.shadow(parent.variables(), VARS.groupByName(symbol.getFormalParameters())));
    }

    /**
     * Local vars are merged into the parent shadowing group. They don't
     * shadow other local vars, they conflict with them.
     */
    JSymbolTable localVarSymTable(JSymbolTable parent, NodeStream<ASTVariableDeclaratorId> ids) {
        List<JVariableSymbol> list = ids.toList(ASTVariableDeclaratorId::getSymbol);
        if (list.size() == 1) {
            return localVarSymTable(parent, list.get(0));
        }
        return SymbolTableImpl.withVars(parent, VARS.augment(parent.variables(), false, VARS.groupByName(list)));
    }

    JSymbolTable localTypeSymTable(JSymbolTable parent, JClassSymbol sym) {
        // TODO is this really not a shadow barrier?
        return SymbolTableImpl.withTypes(parent, TYPES.augment(parent.types(), false, sym));
    }

    JSymbolTable localVarSymTable(JSymbolTable parent, JVariableSymbol id) {
        return SymbolTableImpl.withVars(parent, VARS.augment(parent.variables(), false, id));
    }

}
