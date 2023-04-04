/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;


import static net.sourceforge.pmd.lang.java.symbols.table.ScopeInfo.FORMAL_PARAM;
import static net.sourceforge.pmd.lang.java.symbols.table.ScopeInfo.SAME_FILE;
import static net.sourceforge.pmd.util.AssertionUtil.isValidJavaPackageName;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BinaryOperator;

import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
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
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol;
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol;
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
import net.sourceforge.pmd.util.CollectionUtil;

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


    public void disambig(NodeStream<? extends JavaNode> nodes, ReferenceCtx context) {
        InternalApiBridge.disambigWithCtx(nodes, context);
    }

    SemanticErrorReporter getLogger() {
        return processor.getLogger();
    }

    JClassSymbol loadClassReportFailure(JavaNode location, String fqcn) {
        JClassSymbol loaded = loadClassOrFail(fqcn);
        if (loaded == null) {
            getLogger().warning(location, JavaSemanticErrors.CANNOT_RESOLVE_SYMBOL, fqcn);
        }

        return loaded;
    }

    /** @see SymbolResolver#resolveClassFromCanonicalName(String) */
    @Nullable
    JClassSymbol loadClassOrFail(String fqcn) {
        return processor.getSymResolver().resolveClassFromCanonicalName(fqcn);
    }

    // </editor-fold>

    private @NonNull JSymbolTable buildTable(JSymbolTable parent,
                                             ShadowChainNode<JVariableSig, ScopeInfo> vars,
                                             ShadowChainNode<JMethodSig, ScopeInfo> methods,
                                             ShadowChainNode<JTypeMirror, ScopeInfo> types) {
        if (vars == parent.variables() && methods == parent.methods() && types == parent.types()) { // NOPMD CompareObjectsWithEquals
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
        List<JClassType> importedMethodContainers = new ArrayList<>();

        Set<String> lazyImportedPackagesAndTypes = new LinkedHashSet<>();

        fillImportOnDemands(importsOnDemand, importedTypes, importedFields, importedMethodContainers, lazyImportedPackagesAndTypes);

        NameResolver<JMethodSig> methodResolver =
            NameResolver.composite(CollectionUtil.map(importedMethodContainers, c -> JavaResolvers.staticImportOnDemandMethodResolver(c, thisPackage)));

        ShadowChainNode<JVariableSig, ScopeInfo> vars = VARS.shadow(varNode(parent), ScopeInfo.IMPORT_ON_DEMAND, importedFields);
        ShadowChainNode<JMethodSig, ScopeInfo> methods = METHODS.shadow(methodNode(parent), ScopeInfo.IMPORT_ON_DEMAND, methodResolver);
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

    private void fillImportOnDemands(Iterable<ASTImportDeclaration> importsOnDemand,
                                     ShadowChainBuilder<JTypeMirror, ?>.ResolverBuilder importedTypes,
                                     ShadowChainBuilder<JVariableSig, ?>.ResolverBuilder importedFields,
                                     List<JClassType> importedMethodContainers,
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

                    Pair<ShadowChainBuilder<JTypeMirror, ?>.ResolverBuilder, ShadowChainBuilder<JVariableSig, ?>.ResolverBuilder> pair =
                        JavaResolvers.importOnDemandMembersResolvers(containerType, thisPackage);

                    importedTypes.absorb(pair.getLeft());
                    importedFields.absorb(pair.getRight());

                    importedMethodContainers.add(containerType);
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

        List<NameResolver<? extends JTypeMirror>> importedStaticTypes = new ArrayList<>();
        List<NameResolver<? extends JVariableSig>> importedStaticFields = new ArrayList<>();
        List<NameResolver<? extends JMethodSig>> importedStaticMethods = new ArrayList<>();

        fillSingleImports(singleImports, importedTypes);
        fillSingleStaticImports(singleImports, importedStaticTypes, importedStaticFields, importedStaticMethods);

        return buildTable(
            parent,
            VARS.shadow(varNode(parent), ScopeInfo.SINGLE_IMPORT, NameResolver.composite(importedStaticFields)),
            METHODS.shadow(methodNode(parent), ScopeInfo.SINGLE_IMPORT, NameResolver.composite(importedStaticMethods)),
            TYPES.augment(
                TYPES.shadow(typeNode(parent), ScopeInfo.SINGLE_IMPORT, importedTypes.build()),
                false,
                ScopeInfo.SINGLE_IMPORT,
                NameResolver.composite(importedStaticTypes)
            )
        );

    }


    private void fillSingleImports(Iterable<ASTImportDeclaration> singleImports,
                                   ShadowChainBuilder<JTypeMirror, ?>.ResolverBuilder importedTypes) {

        for (ASTImportDeclaration anImport : singleImports) {
            if (anImport.isImportOnDemand()) {
                throw new IllegalArgumentException(anImport.toString());
            }

            if (!anImport.isStatic()) {
                // Single-Type-Import Declaration
                JClassSymbol type = processor.findSymbolCannotFail(anImport, anImport.getImportedName());
                importedTypes.append(type.getTypeSystem().typeOf(type, false));
            }
        }
    }

    private void fillSingleStaticImports(Iterable<ASTImportDeclaration> singleImports,
                                         List<NameResolver<? extends JTypeMirror>> importedTypes,
                                         List<NameResolver<? extends JVariableSig>> importedFields,
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
                if (idx < 0) {
                    continue; // invalid syntax
                }
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
                importedFields.add(JavaResolvers.staticImportFieldResolver(containerType, thisPackage, simpleName));
                importedTypes.add(JavaResolvers.staticImportClassResolver(containerType, thisPackage, simpleName));
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
        if (!sym.isAnonymousClass()) {
            types = TYPES.shadow(types, ScopeInfo.ENCLOSING_TYPE, t);                                                        // self name
        }
        types = TYPES.shadow(types, ScopeInfo.INHERITED, inherited.getLeft());                                           // inherited classes (note they shadow the enclosing type)
        types = TYPES.shadow(types, ScopeInfo.ENCLOSING_TYPE_MEMBER, TYPES.groupByName(t.getDeclaredClasses()));       // inner classes declared here
        types = TYPES.shadow(types, ScopeInfo.TYPE_PARAM, TYPES.groupByName(sym.getTypeParameters()));                   // type parameters

        ShadowChainNode<JVariableSig, ScopeInfo> fields = varNode(parent);
        fields = VARS.shadow(fields, ScopeInfo.INHERITED, inherited.getRight());
        fields = VARS.shadow(fields, ScopeInfo.ENCLOSING_TYPE_MEMBER, VARS.groupByName(t.getDeclaredFields()));

        ShadowChainNode<JMethodSig, ScopeInfo> methods = methodNode(parent);
        BinaryOperator<List<JMethodSig>> merger = JavaResolvers.methodMerger(Modifier.isStatic(t.getSymbol().getModifiers()));
        methods = METHODS.augmentWithCache(methods, false, ScopeInfo.METHOD_MEMBER, JavaResolvers.subtypeMethodResolver(t), merger);

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
    JSymbolTable localVarSymTable(JSymbolTable parent, JClassType enclosing, Iterable<ASTVariableDeclaratorId> ids) {
        List<JVariableSig> sigs = new ArrayList<>();
        for (ASTVariableDeclaratorId id : ids) {
            sigs.add(id.getTypeSystem().sigOf(enclosing, (JLocalVariableSymbol) id.getSymbol()));
        }
        return SymbolTableImpl.withVars(parent, VARS.augment(varNode(parent), false, ScopeInfo.LOCAL, VARS.groupByName(sigs)));
    }

    JSymbolTable localVarSymTable(JSymbolTable parent, JClassType enclosing, JVariableSymbol id) {
        return SymbolTableImpl.withVars(parent, VARS.augment(varNode(parent), false, ScopeInfo.LOCAL, id.getTypeSystem().sigOf(enclosing, (JLocalVariableSymbol) id)));
    }

    JSymbolTable localTypeSymTable(JSymbolTable parent, JClassType sym) {
        // TODO is this really not a shadow barrier?
        return SymbolTableImpl.withTypes(parent, TYPES.augment(typeNode(parent), false, ScopeInfo.LOCAL, sym));
    }

}
