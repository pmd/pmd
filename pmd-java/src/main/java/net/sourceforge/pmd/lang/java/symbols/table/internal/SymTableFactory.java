/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;


import static net.sourceforge.pmd.internal.util.AssertionUtil.isValidJavaPackageName;
import static net.sourceforge.pmd.lang.java.symbols.table.ScopeInfo.FORMAL_PARAM;
import static net.sourceforge.pmd.lang.java.symbols.table.ScopeInfo.SAME_FILE;
import static net.sourceforge.pmd.lang.java.types.JVariableSig.FieldSig;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
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
import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;
import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol;
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolResolver;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ScopeInfo;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.NameResolver;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChainBuilder;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChainNode;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.JVariableSig;

final class SymTableFactory {


    private final String thisPackage;
    private final JavaAstProcessor processor;

    static final ShadowChainBuilder<JTypeMirror, ScopeInfo> TYPES = new ShadowChainBuilder<JTypeMirror, ScopeInfo>() {

        @Override
        public String getSimpleName(JTypeMirror type) {
            if (type instanceof JClassType) {
                return ((JClassType) type).getSymbol().getSimpleName();
            } else if (type instanceof JTypeVar) {
                JTypeDeclSymbol sym = type.getSymbol();
                assert sym != null : "Must contain named symbols";
                return sym.getSimpleName();
            }
            throw new AssertionError("Cannot contain type " + type);
        }
    };

    static final ShadowChainBuilder<JVariableSig, ScopeInfo> VARS = new ShadowChainBuilder<JVariableSig, ScopeInfo>() {
        @Override
        public String getSimpleName(JVariableSig sym) {
            return sym.getSymbol().getSimpleName();
        }
    };

    static final ShadowChainBuilder<JMethodSig, ScopeInfo> METHODS = new ShadowChainBuilder<JMethodSig, ScopeInfo>() {
        @Override
        public String getSimpleName(JMethodSig sym) {
            return sym.getName();
        }
    };


    SymTableFactory(String thisPackage, JavaAstProcessor processor) {
        this.thisPackage = thisPackage;
        this.processor = processor;
    }

    // <editor-fold defaultstate="collapsed" desc="Utilities for classloading">


    public void disambig(NodeStream<? extends JavaNode> nodes, ASTAnyTypeDeclaration context, boolean outsideContext) {
        InternalApiBridge.disambig(processor, nodes, context, outsideContext);
    }

    SemanticChecksLogger getLogger() {
        return processor.getLogger();
    }

    JClassSymbol loadClassReportFailure(JavaNode location, String fqcn) {
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

    boolean canBeImported(JAccessibleElementSymbol member) {
        return JavaResolvers.canBeImportedIn(thisPackage, member);
    }

    @NonNull
    private JMethodSig sigOf(JMethodSymbol m) {
        return processor.getTypeSystem().sigOf(m);
    }

    // </editor-fold>

    @NonNull
    private JSymbolTable buildTable(JSymbolTable parent,
                                    ShadowChainNode<JVariableSig, ScopeInfo> vars,
                                    ShadowChainNode<JMethodSig, ScopeInfo> methods,
                                    ShadowChainNode<JTypeMirror, ScopeInfo> types) {
        if (vars == parent.variables() && methods == parent.methods() && types == parent.types()) {
            return parent;
        } else {
            return new SymbolTableImpl(vars, types, methods);
        }
    }

    private ShadowChainNode<JTypeMirror, ScopeInfo> typeNode(JSymbolTable parent) {
        return parent.types().asNode();
    }

    private ShadowChainNode<JVariableSig, ScopeInfo> varNode(JSymbolTable parent) {
        return parent.variables().asNode();
    }

    private ShadowChainNode<JMethodSig, ScopeInfo> methodNode(JSymbolTable parent) {
        return parent.methods().asNode();
    }

    JSymbolTable importsOnDemand(JSymbolTable parent, Collection<ASTImportDeclaration> importsOnDemand) {
        if (importsOnDemand.isEmpty()) {
            return parent;
        }

        ShadowChainBuilder<JTypeMirror, ScopeInfo>.ResolverBuilder importedTypes = TYPES.new ResolverBuilder();
        ShadowChainBuilder<JVariableSig, ScopeInfo>.ResolverBuilder importedFields = VARS.new ResolverBuilder();
        ShadowChainBuilder<JMethodSig, ScopeInfo>.ResolverBuilder importedMethods = METHODS.new ResolverBuilder();

        Set<String> lazyImportedPackagesAndTypes = new LinkedHashSet<>();

        fillImportOnDemands(importsOnDemand, importedTypes, importedFields, importedMethods, lazyImportedPackagesAndTypes);

        ShadowChainNode<JVariableSig, ScopeInfo> vars = VARS.shadow(varNode(parent), ScopeInfo.IMPORT_ON_DEMAND, importedFields);
        ShadowChainNode<JMethodSig, ScopeInfo> methods = METHODS.shadow(methodNode(parent), ScopeInfo.IMPORT_ON_DEMAND, importedMethods);
        ShadowChainNode<JTypeMirror, ScopeInfo> types;
        if (lazyImportedPackagesAndTypes.isEmpty()) {
            // then we don't need to use the lazy impl
            types = TYPES.shadow(typeNode(parent), ScopeInfo.IMPORT_ON_DEMAND, importedTypes);
        } else {
            types = TYPES.shadowWithCache(
                typeNode(parent),
                ScopeInfo.IMPORT_ON_DEMAND,
                importedTypes.getMutableMap(),
                JavaResolvers.importedOnDemand(lazyImportedPackagesAndTypes, processor.getSymResolver(), thisPackage)
            );
        }

        return buildTable(parent, vars, methods, types);
    }

    // FIXME import on demands may import inherited fields
    // Make them truly on-demand, ie lazy (there's already the necessary resolvers in JavaResolver)

    private void fillImportOnDemands(Iterable<ASTImportDeclaration> importsOnDemand,
                                     ShadowChainBuilder<JTypeMirror, ?>.ResolverBuilder importedTypes,
                                     ShadowChainBuilder<JVariableSig, ?>.ResolverBuilder importedFields,
                                     ShadowChainBuilder<JMethodSig, ?>.ResolverBuilder importedMethods,
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

                    JClassType containerType = (JClassType) containerClass.getTypeSystem().typeOf(containerClass, false);

                    for (JMethodSymbol m : containerClass.getDeclaredMethods()) {
                        if (Modifier.isStatic(m.getModifiers()) && canBeImported(m)) {
                            importedMethods.append(sigOf(m));
                        }
                    }

                    for (FieldSig f : containerType.getDeclaredFields()) {
                        if (Modifier.isStatic(f.getSymbol().getModifiers()) && canBeImported(f.getSymbol())) {
                            importedFields.append(f);
                        }
                    }

                    for (JClassType t : containerType.getDeclaredClasses()) {
                        if (Modifier.isStatic(t.getSymbol().getModifiers()) && canBeImported(t.getSymbol())) {
                            importedTypes.append(t);
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


    JSymbolTable singleImportsSymbolTable(JSymbolTable parent, Collection<ASTImportDeclaration> singleImports) {
        if (singleImports.isEmpty()) {
            return parent;
        }

        ShadowChainBuilder<JTypeMirror, ScopeInfo>.ResolverBuilder importedTypes = TYPES.new ResolverBuilder();
        ShadowChainBuilder<JVariableSig, ScopeInfo>.ResolverBuilder importedFields = VARS.new ResolverBuilder();
        List<NameResolver<? extends JMethodSig>> importedStaticMethods = new ArrayList<>();
        fillSingleImports(singleImports, importedTypes, importedFields, importedStaticMethods);

        return buildTable(
            parent,
            VARS.shadow(varNode(parent), ScopeInfo.SINGLE_IMPORT, importedFields.build()),
            METHODS.shadow(methodNode(parent), ScopeInfo.SINGLE_IMPORT, NameResolver.composite(importedStaticMethods)),
            TYPES.shadow(typeNode(parent), ScopeInfo.SINGLE_IMPORT, importedTypes.build())
        );

    }


    private void fillSingleImports(Iterable<ASTImportDeclaration> singleImports,
                                   ShadowChainBuilder<JTypeMirror, ?>.ResolverBuilder importedTypes,
                                   ShadowChainBuilder<JVariableSig, ?>.ResolverBuilder importedFields,
                                   List<NameResolver<? extends JMethodSig>> importedStaticMethods) {

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
                String className = name.substring(0, idx);

                JClassSymbol containerClass = loadClassReportFailure(anImport, className);

                if (containerClass == null) {
                    // the auxclasspath is wrong
                    // bc static imports can't import toplevel types
                    // already reported
                    continue;
                }

                JClassType containerType = (JClassType) containerClass.getTypeSystem().declaration(containerClass);

                importedStaticMethods.add(JavaResolvers.staticImportMethodResolver(containerType, thisPackage, simpleName));

                JavaResolvers.getMemberFieldResolver(containerType, thisPackage, null, simpleName)
                             .resolveHere(simpleName)
                             .forEach(importedFields::appendWithoutDuplicate);

                JavaResolvers.getMemberClassResolver(containerType, thisPackage, null, simpleName)
                             .resolveHere(simpleName)
                             .forEach(importedTypes::appendWithoutDuplicate);

            } else {
                // Single-Type-Import Declaration
                JClassSymbol type = findSymbolCannotFail(name);
                importedTypes.append(type.getTypeSystem().typeOf(type, false));
            }
        }
    }

    JSymbolTable javaLangSymTable(JSymbolTable parent) {
        return typesInPackage(parent, "java.lang", ScopeInfo.JAVA_LANG);
    }

    JSymbolTable samePackageSymTable(JSymbolTable parent) {
        return typesInPackage(parent, thisPackage, ScopeInfo.SAME_PACKAGE);
    }

    @NonNull
    private JSymbolTable typesInPackage(JSymbolTable parent, String packageName, ScopeInfo scopeTag) {
        assert isValidJavaPackageName(packageName) : "Not a package name: " + packageName;

        return SymbolTableImpl.withTypes(
            parent,
            TYPES.augmentWithCache(typeNode(parent), true, scopeTag, JavaResolvers.packageResolver(processor.getSymResolver(), packageName))
        );
    }

    JSymbolTable typeBody(JSymbolTable parent, @NonNull JClassType t) {

        Pair<NameResolver<JTypeMirror>, NameResolver<JVariableSig>> inherited = JavaResolvers.inheritedMembersResolvers(t);

        JClassSymbol sym = t.getSymbol();

        ShadowChainNode<JTypeMirror, ScopeInfo> types = typeNode(parent);
        types = TYPES.shadow(types, ScopeInfo.ENCLOSING_TYPE, t);                                                        // self name
        types = TYPES.shadow(types, ScopeInfo.INHERITED, inherited.getLeft());                                           // inherited classes (note they shadow the enclosing type)
        types = TYPES.shadow(types, ScopeInfo.ENCLOSING_TYPE_MEMBER, TYPES.groupByName(t.getDeclaredClasses()));       // inner classes declared here
        types = TYPES.shadow(types, ScopeInfo.TYPE_PARAM, TYPES.groupByName(sym.getTypeParameters()));                   // type parameters

        ShadowChainNode<JVariableSig, ScopeInfo> fields = varNode(parent);
        fields = VARS.shadow(fields, ScopeInfo.INHERITED, inherited.getRight());
        fields = VARS.shadow(fields, ScopeInfo.ENCLOSING_TYPE_MEMBER, VARS.groupByName(t.getDeclaredFields()));

        ShadowChainNode<JMethodSig, ScopeInfo> methods = methodNode(parent);
        methods = METHODS.augmentWithCache(methods, false, ScopeInfo.METHOD_MEMBER, JavaResolvers.subtypeMethodResolver(t), JavaResolvers.methodMerger());

        return buildTable(parent, fields, methods, types);
    }

    JSymbolTable typesInFile(JSymbolTable parent, NodeStream<ASTAnyTypeDeclaration> decls) {
        return SymbolTableImpl.withTypes(parent, TYPES.shadow(typeNode(parent), SAME_FILE, TYPES.groupByName(decls, ASTAnyTypeDeclaration::getTypeMirror)));
    }

    JSymbolTable selfType(JSymbolTable parent, JClassType sym) {
        return SymbolTableImpl.withTypes(parent, TYPES.shadow(typeNode(parent), ScopeInfo.ENCLOSING_TYPE_MEMBER, TYPES.groupByName(sym)));
    }

    JSymbolTable typeHeader(JSymbolTable parent, JClassSymbol sym) {
        return SymbolTableImpl.withTypes(parent, TYPES.shadow(typeNode(parent), ScopeInfo.TYPE_PARAM, TYPES.groupByName(sym.getTypeParameters())));
    }

    /**
     * Symbol table for a body declaration. This places a shadowing
     * group for variables, ie, nested variable shadowing groups will
     * be merged into it but not into the parent. This implements shadowing
     * of fields by local variables and formals.
     */
    JSymbolTable bodyDeclaration(JSymbolTable parent, JClassType enclosing, @Nullable ASTFormalParameters formals, @Nullable ASTTypeParameters tparams) {
        return new SymbolTableImpl(
            VARS.shadow(varNode(parent), ScopeInfo.FORMAL_PARAM, VARS.groupByName(ASTList.orEmptyStream(formals), fp -> {
                JVariableSymbol sym = fp.getVarId().getSymbol();
                return sym.getTypeSystem().sigOf(enclosing, (JFormalParamSymbol) sym);
            })),
            TYPES.shadow(typeNode(parent), ScopeInfo.TYPE_PARAM, TYPES.groupByName(ASTList.orEmptyStream(tparams), ASTTypeParameter::getTypeMirror)),
            methodNode(parent)
        );
    }

    JSymbolTable recordCtor(JSymbolTable parent, JClassType recordType, JConstructorSymbol symbol) {
        return SymbolTableImpl.withVars(parent, VARS.shadow(varNode(parent), FORMAL_PARAM, VARS.groupByName(symbol.getFormalParameters(), s -> s.getTypeSystem().sigOf(recordType, s))));
    }

    /**
     * Local vars are merged into the parent shadowing group. They don't
     * shadow other local vars, they conflict with them.
     */
    JSymbolTable localVarSymTable(JSymbolTable parent, JClassType enclosing, NodeStream<ASTVariableDeclaratorId> ids) {
        List<JVariableSymbol> list = ids.toList(ASTVariableDeclaratorId::getSymbol);
        if (list.size() == 1) {
            return localVarSymTable(parent, enclosing, list.get(0));
        }
        return SymbolTableImpl.withVars(parent, VARS.augment(varNode(parent), false, ScopeInfo.LOCAL, VARS.groupByName(list, s -> s.getTypeSystem().sigOf(enclosing, (JLocalVariableSymbol) s))));
    }

    JSymbolTable localTypeSymTable(JSymbolTable parent, JClassType sym) {
        // TODO is this really not a shadow barrier?
        return SymbolTableImpl.withTypes(parent, TYPES.augment(typeNode(parent), false, ScopeInfo.LOCAL, sym));
    }

    JSymbolTable localVarSymTable(JSymbolTable parent, JClassType enclosing, JVariableSymbol id) {
        return SymbolTableImpl.withVars(parent, VARS.augment(varNode(parent), false, ScopeInfo.LOCAL, id.getTypeSystem().sigOf(enclosing, (JLocalVariableSymbol) id)));
    }

}
