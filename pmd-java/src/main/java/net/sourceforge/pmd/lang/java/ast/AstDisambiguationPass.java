/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.symbols.table.internal.JavaSemanticErrors.CANNOT_RESOLVE_AMBIGUOUS_NAME;

import java.util.Iterator;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.internal.JavaSemanticErrors;
import net.sourceforge.pmd.lang.java.symbols.table.internal.ReferenceCtx;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JVariableSig;
import net.sourceforge.pmd.lang.java.types.JVariableSig.FieldSig;
import net.sourceforge.pmd.lang.java.types.ast.LazyTypeResolver;

/**
 * This implements name disambiguation following <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-6.html#jls-6.5.2">JLS§6.5.2</a>.
 * (see also <a href="https://docs.oracle.com/javase/specs/jls/se13/html/jls-6.html#jls-6.4.2">JLS§6.4.2 - Obscuring</a>)
 */
final class AstDisambiguationPass {

    private AstDisambiguationPass() {
        // façade
    }

    /**
     * Disambiguate the subtrees rooted at the given nodes. After this:
     * <ul>
     * <li>All ClassOrInterfaceTypes either see their ambiguous LHS
     * promoted to a ClassOrInterfaceType, or demoted to a package
     * name (removed from the tree)
     * <li>All ClassOrInterfaceTypes have a non-null symbol, even if
     * it is unresolved EXCEPT the ones of a qualified constructor call.
     * Those references are resolved lazily by {@link LazyTypeResolver},
     * because they depend on the full type resolution of the qualifier
     * expression, and that resolution may use things that are not yet
     * disambiguated
     * <li>There may still be AmbiguousNames, but only in expressions,
     * for the worst kind of ambiguity
     * </ul>
     */
    public static void disambigWithCtx(NodeStream<? extends JavaNode> nodes, ReferenceCtx ctx) {
        assert ctx != null : "Null context";
        nodes.forEach(it -> it.acceptVisitor(DisambigVisitor.INSTANCE, ctx));
    }


    // those ignore JTypeParameterSymbol, for error handling logic to be uniform


    private static void checkParentIsMember(ReferenceCtx ctx, ASTClassOrInterfaceType resolvedType, ASTClassOrInterfaceType parent) {
        JTypeDeclSymbol sym = resolvedType.getReferencedSym();
        JClassSymbol parentClass = ctx.findTypeMember(sym, parent.getSimpleName(), parent);
        if (parentClass == null) {
            ctx.reportUnresolvedMember(parent, ReferenceCtx.Fallback.TYPE, parent.getSimpleName(), sym);
            int numTypeArgs = ASTList.sizeOrZero(parent.getTypeArguments());
            parentClass = ctx.makeUnresolvedReference(sym, parent.getSimpleName(), numTypeArgs);
        }
        parent.setSymbol(parentClass);
    }

    private static @Nullable JClassType enclosingType(JTypeMirror typeResult) {
        return typeResult instanceof JClassType ? ((JClassType) typeResult).getEnclosingType() : null;
    }

    private static final class DisambigVisitor extends JavaVisitorBase<ReferenceCtx, Void> {

        public static final DisambigVisitor INSTANCE = new DisambigVisitor();


        @Override
        protected Void visitChildren(Node node, ReferenceCtx data) {
            // note that this differs from the default impl, because
            // the default declares last = node.getNumChildren()
            // at the beginning of the loop, but in this visitor the
            // number of children may change.
            for (int i = 0; i < node.getNumChildren(); i++) {
                node.getChild(i).acceptVisitor(this, data);
            }
            return null;
        }

        @Override
        public Void visitTypeDecl(ASTAnyTypeDeclaration node, ReferenceCtx data) {
            // since type headers are disambiguated early it doesn't matter
            // if the context is inaccurate in type headers
            return visitChildren(node, data.scopeDownToNested(node.getSymbol()));
        }

        @Override
        public Void visit(ASTAmbiguousName name, ReferenceCtx processor) {
            if (name.wasProcessed()) {
                // don't redo analysis
                return null;
            }

            JSymbolTable symbolTable = name.getSymbolTable();
            assert symbolTable != null : "Symbol tables haven't been set yet??";

            boolean isPackageOrTypeOnly;
            if (name.getParent() instanceof ASTClassOrInterfaceType) {
                isPackageOrTypeOnly = true;
            } else if (name.getParent() instanceof ASTExpression) {
                isPackageOrTypeOnly = false;
            } else {
                throw new AssertionError("Unrecognised context for ambiguous name: " + name.getParent());
            }

            // do resolve
            JavaNode resolved = startResolve(name, processor, isPackageOrTypeOnly);

            // finish
            assert !isPackageOrTypeOnly
                || resolved instanceof ASTTypeExpression
                || resolved instanceof ASTAmbiguousName
                : "Unexpected result " + resolved + " for PackageOrTypeName resolution";

            if (isPackageOrTypeOnly && resolved instanceof ASTTypeExpression) {
                // unambiguous, we just have to check that the parent is a member of the enclosing type

                ASTClassOrInterfaceType resolvedType = (ASTClassOrInterfaceType) ((ASTTypeExpression) resolved).getTypeNode();
                resolved = resolvedType;
                ASTClassOrInterfaceType parent = (ASTClassOrInterfaceType) name.getParent();

                checkParentIsMember(processor, resolvedType, parent);
            }

            if (resolved != name) { // NOPMD - intentional check for reference equality
                ((AbstractJavaNode) name.getParent()).setChild((AbstractJavaNode) resolved, name.getIndexInParent());
            }

            return null;
        }

        @Override
        public Void visit(ASTClassOrInterfaceType type, ReferenceCtx ctx) {

            if (type.getReferencedSym() != null) {
                return null;
            }

            if (type.getFirstChild() instanceof ASTAmbiguousName) {
                type.getFirstChild().acceptVisitor(this, ctx);
            }

            // revisit children, which may have changed
            visitChildren(type, ctx);

            if (type.getReferencedSym() != null) {
                postProcess(type, ctx);
                return null;
            }

            ASTClassOrInterfaceType lhsType = type.getQualifier();
            if (lhsType != null) {
                JTypeDeclSymbol lhsSym = lhsType.getReferencedSym();
                assert lhsSym != null : "Unresolved LHS for " + type;
                checkParentIsMember(ctx, lhsType, type);
            } else {
                if (type.getParent() instanceof ASTConstructorCall
                    && ((ASTConstructorCall) type.getParent()).isQualifiedInstanceCreation()) {
                    // Leave the reference null, this is handled lazily,
                    // because the interaction it depends on the type of
                    // the qualifier
                    return null;
                }

                if (type.getReferencedSym() == null) {
                    setClassSymbolIfNoQualifier(type, ctx);
                }
            }

            assert type.getReferencedSym() != null : "Null symbol for " + type;

            postProcess(type, ctx);
            return null;
        }

        private static void setClassSymbolIfNoQualifier(ASTClassOrInterfaceType type, ReferenceCtx ctx) {
            final JTypeMirror resolved = ctx.resolveSingleTypeName(type.getSymbolTable(), type.getSimpleName(), type);
            JTypeDeclSymbol sym;
            if (resolved == null) {
                ctx.reportCannotResolveSymbol(type, type.getSimpleName());
                sym = setArity(type, ctx, type.getSimpleName());
            } else {
                sym = resolved.getSymbol();
                if (sym.isUnresolved()) {
                    sym = setArity(type, ctx, ((JClassSymbol) sym).getCanonicalName());
                }
            }
            type.setSymbol(sym);
            type.setImplicitEnclosing(enclosingType(resolved));
        }

        private void postProcess(ASTClassOrInterfaceType type, ReferenceCtx ctx) {
            JTypeDeclSymbol sym = type.getReferencedSym();
            if (type.getParent() instanceof ASTAnnotation) {
                if (!(sym instanceof JClassSymbol && (sym.isUnresolved() || ((JClassSymbol) sym).isAnnotation()))) {
                    ctx.getLogger().warning(type, JavaSemanticErrors.EXPECTED_ANNOTATION_TYPE);
                }
                return;
            }

            int actualArity = ASTList.sizeOrZero(type.getTypeArguments());
            int expectedArity = sym instanceof JClassSymbol ? ((JClassSymbol) sym).getTypeParameterCount() : 0;
            if (actualArity != 0 && actualArity != expectedArity) {
                ctx.getLogger().warning(type, JavaSemanticErrors.MALFORMED_GENERIC_TYPE, expectedArity, actualArity);
            }
        }

        private static @NonNull JTypeDeclSymbol setArity(ASTClassOrInterfaceType type, ReferenceCtx ctx, String canonicalName) {
            int arity = ASTList.sizeOrZero(type.getTypeArguments());
            return ctx.makeUnresolvedReference(canonicalName, arity);
        }

        /*

           This is implemented as a set of mutually recursive methods
           that act as a kind of automaton. State transitions:

                        +-----+       +--+        +--+
                        |     |       |  |        |  |
           +-----+      +     v       +  v        +  v
           |START+----> PACKAGE +---> TYPE +----> EXPR
           +-----+                     ^           ^
             |                         |           |
             +-------------------------------------+

           Not pictured are the error transitions.
           Only Type & Expr are valid exit states.
         */

        /**
         * Resolve an ambiguous name occurring in an expression context.
         * Returns the expression to which the name was resolved. If the
         * name is a type, this is a {@link ASTTypeExpression}, otherwise
         * it could be a {@link ASTFieldAccess} or {@link ASTVariableAccess},
         * and in the worst case, the original {@link ASTAmbiguousName}.
         */
        private static ASTExpression startResolve(ASTAmbiguousName name, ReferenceCtx ctx, boolean isPackageOrTypeOnly) {
            Iterator<JavaccToken> tokens = name.tokens().iterator();
            JavaccToken firstIdent = tokens.next();
            TokenUtils.expectKind(firstIdent, JavaTokenKinds.IDENTIFIER);

            JSymbolTable symTable = name.getSymbolTable();

            if (!isPackageOrTypeOnly) {
                // first test if the leftmost segment is an expression
                JVariableSig varResult = symTable.variables().resolveFirst(firstIdent.getImage());

                if (varResult != null) {
                    return resolveExpr(null, varResult, firstIdent, tokens, ctx);
                }
            }

            // otherwise, test if it is a type name

            JTypeMirror typeResult = ctx.resolveSingleTypeName(symTable, firstIdent.getImage(), name);

            if (typeResult != null) {
                JClassType enclosing = enclosingType(typeResult);
                return resolveType(null, enclosing, typeResult.getSymbol(), false, firstIdent, tokens, name, isPackageOrTypeOnly, ctx);
            }

            // otherwise, first is reclassified as package name.
            return resolvePackage(firstIdent, new StringBuilder(firstIdent.getImage()), tokens, name, isPackageOrTypeOnly, ctx);
        }


        /**
         * Classify the given [identifier] as an expression name. This
         * produces a FieldAccess/VariableAccess, depending on whether there is a qualifier.
         * The remaining token chain is reclassified as a sequence of
         * field accesses.
         *
         * TODO Check the field accesses are legal
         *  Also must filter by visibility
         */
        private static ASTExpression resolveExpr(@Nullable ASTExpression qualifier, // lhs
                                                 @Nullable JVariableSig varSym,     // signature, only set if this is the leftmost access
                                                 JavaccToken identifier,            // identifier for the field/var name
                                                 Iterator<JavaccToken> remaining,   // rest of tokens, starting with following '.'
                                                 ReferenceCtx ctx) {

            TokenUtils.expectKind(identifier, JavaTokenKinds.IDENTIFIER);

            ASTNamedReferenceExpr var;
            if (qualifier == null) {
                ASTVariableAccess varAccess = new ASTVariableAccess(identifier);
                varAccess.setTypedSym(varSym);
                var = varAccess;
            } else {
                ASTFieldAccess fieldAccess = new ASTFieldAccess(qualifier, identifier);
                fieldAccess.setTypedSym((FieldSig) varSym);
                var = fieldAccess;
            }


            if (!remaining.hasNext()) { // done
                return var;
            }

            JavaccToken nextIdent = skipToNextIdent(remaining);

            // following must also be expressions (field accesses)
            // we can't assert that for now, as symbols lack type information

            return resolveExpr(var, null, nextIdent, remaining, ctx);
        }

        /**
         * Classify the given [identifier] as a reference to the [sym].
         * This produces a ClassOrInterfaceType with the given [image] (which
         * may be prepended by a package name, or otherwise is just a simple name).
         * We then lookup the following identifier, and take a decision:
         * <ul>
         * <li>If there is a field with the given name in [classSym],
         * then the remaining tokens are reclassified as expression names
         * <li>Otherwise, if there is a member type with the given name
         * in [classSym], then the remaining segment is classified as a
         * type name (recursive call to this procedure)
         * <li>Otherwise, normally a compile-time error occurs. We instead
         * log a warning and treat it as a field access.
         * </ul>
         *
         * @param isPackageOrTypeOnly If true, expressions are disallowed by the context, so we don't check fields
         */
        private static ASTExpression resolveType(final @Nullable ASTClassOrInterfaceType qualifier, // lhs
                                                 final @Nullable JClassType implicitEnclosing,      // enclosing type, if it is implicitly inherited
                                                 final JTypeDeclSymbol sym,                         // symbol for the type
                                                 final boolean isFqcn,                              // whether this is a fully-qualified name
                                                 final JavaccToken identifier,                      // ident of the simple name of the symbol
                                                 final Iterator<JavaccToken> remaining,             // rest of tokens, starting with following '.'
                                                 final ASTAmbiguousName ambig,                      // original ambiguous name
                                                 final boolean isPackageOrTypeOnly,
                                                 final ReferenceCtx ctx) {

            TokenUtils.expectKind(identifier, JavaTokenKinds.IDENTIFIER);

            final ASTClassOrInterfaceType type = new ASTClassOrInterfaceType(qualifier, isFqcn, ambig.getFirstToken(), identifier);
            type.setSymbol(sym);
            type.setImplicitEnclosing(implicitEnclosing);

            if (!remaining.hasNext()) { // done
                return new ASTTypeExpression(type);
            }

            final JavaccToken nextIdent = skipToNextIdent(remaining);
            final String nextSimpleName = nextIdent.getImage();

            if (!isPackageOrTypeOnly) {
                @Nullable FieldSig field = ctx.findStaticField(sym, nextSimpleName);
                if (field != null) {
                    // todo check field is static
                    ASTTypeExpression typeExpr = new ASTTypeExpression(type);
                    return resolveExpr(typeExpr, field, nextIdent, remaining, ctx);
                }
            }

            JClassSymbol inner = ctx.findTypeMember(sym, nextSimpleName, ambig);

            if (inner == null && isPackageOrTypeOnly) {
                // normally compile-time error, continue by considering it an unresolved inner type
                ctx.reportUnresolvedMember(ambig, ReferenceCtx.Fallback.TYPE, nextSimpleName, sym);
                inner = ctx.makeUnresolvedReference(sym, nextSimpleName, 0);
            }

            if (inner != null) {
                return resolveType(type, null, inner, false, nextIdent, remaining, ambig, isPackageOrTypeOnly, ctx);
            }

            // no inner type, yet we have a lhs that is a type...
            // this is normally a compile-time error
            // treat as unresolved field accesses, this is the smoothest for later type res

            // todo report on the specific token failing
            ctx.reportUnresolvedMember(ambig, ReferenceCtx.Fallback.FIELD_ACCESS, nextSimpleName, sym);
            ASTTypeExpression typeExpr = new ASTTypeExpression(type);
            return resolveExpr(typeExpr, null, nextIdent, remaining, ctx); // this will chain for the rest of the name
        }

        /**
         * Classify the given [identifier] as a package name. This means, that
         * we look ahead into the [remaining] tokens, and try to find a class
         * by that name in the given package. Then:
         * <ul>
         * <li>If such a class exists, continue the classification with resolveType
         * <li>Otherwise, the looked ahead segment is itself reclassified as a package name
         * </ul>
         *
         * <p>If we consumed the entire name without finding a suitable
         * class, then we report it and return the original ambiguous name.
         */
        private static ASTExpression resolvePackage(JavaccToken identifier,
                                                    StringBuilder packageImage,
                                                    Iterator<JavaccToken> remaining,
                                                    ASTAmbiguousName ambig,
                                                    boolean isPackageOrTypeOnly,
                                                    ReferenceCtx ctx) {

            TokenUtils.expectKind(identifier, JavaTokenKinds.IDENTIFIER);

            if (!remaining.hasNext()) {
                if (isPackageOrTypeOnly) {
                    // There's one last segment to try, the parent of the ambiguous name
                    // This may only be because this ambiguous name is the package qualification of the parent type
                    forceResolveAsFullPackageNameOfParent(packageImage, ambig, ctx);
                    return ambig; // returning ambig makes the outer routine not replace
                }

                // then this name is unresolved, leave the ambiguous name in the tree
                // this only happens inside expressions
                ctx.getLogger().warning(ambig, CANNOT_RESOLVE_AMBIGUOUS_NAME, packageImage, ReferenceCtx.Fallback.AMBIGUOUS);
                ambig.setProcessed(); // signal that we don't want to retry resolving this
                return ambig;
            }

            JavaccToken nextIdent = skipToNextIdent(remaining);


            packageImage.append('.').append(nextIdent.getImage());
            String canonical = packageImage.toString();

            // Don't interpret periods as nested class separators (this will be handled by resolveType).
            // Otherwise lookup of a fully qualified name would be quadratic
            JClassSymbol nextClass = ctx.resolveClassFromBinaryName(canonical);

            if (nextClass != null) {
                return resolveType(null, null, nextClass, true, nextIdent, remaining, ambig, isPackageOrTypeOnly, ctx);
            } else {
                return resolvePackage(nextIdent, packageImage, remaining, ambig, isPackageOrTypeOnly, ctx);
            }
        }

        /**
         * Force resolution of the ambiguous name as a package name.
         * The parent type's image is set to a package name + simple name.
         */
        private static void forceResolveAsFullPackageNameOfParent(StringBuilder packageImage, ASTAmbiguousName ambig, ReferenceCtx ctx) {
            ASTClassOrInterfaceType parent = (ASTClassOrInterfaceType) ambig.getParent();

            packageImage.append('.').append(parent.getSimpleName());
            String fullName = packageImage.toString();
            JClassSymbol parentClass = ctx.resolveClassFromBinaryName(fullName);
            if (parentClass == null) {
                ctx.getLogger().warning(parent, CANNOT_RESOLVE_AMBIGUOUS_NAME, fullName, ReferenceCtx.Fallback.TYPE);
                parentClass = ctx.makeUnresolvedReference(fullName, ASTList.sizeOrZero(parent.getTypeArguments()));
            }
            parent.setSymbol(parentClass);
            parent.setFullyQualified();
            ambig.deleteInParent();
        }

        private static JavaccToken skipToNextIdent(Iterator<JavaccToken> remaining) {
            JavaccToken dot = remaining.next();
            TokenUtils.expectKind(dot, JavaTokenKinds.DOT);
            assert remaining.hasNext() : "Ambiguous name must end with an identifier";
            return remaining.next();
        }
    }

}
