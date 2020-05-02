/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.symbols.table.internal.SemanticChecksLogger.CANNOT_RESOLVE_AMBIGUOUS_NAME;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.SemanticChecksLogger.CANNOT_RESOLVE_MEMBER;

import java.util.Iterator;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.ResolveResult;
import net.sourceforge.pmd.lang.java.symbols.table.internal.SemanticChecksLogger;

/**
 * This implements name disambiguation following <a href="https://docs.oracle.com/javase/specs/jls/se8/html/jls-6.html#jls-6.5.2">JLS§6.5.2</a>.
 * (see also <a href="https://docs.oracle.com/javase/specs/jls/se13/html/jls-6.html#jls-6.4.2">JLS§6.4.2 - Obscuring</a>)
 *
 * <p>Currently disambiguation of package vs type name is fully implemented,
 * with the following limitations: TODO
 * - field accesses are not checked to be legal (and their symbol is not
 * resolved).
 * - inherited members are not considered. Same thing happens in the current
 * symbol table. Since that is so, visibility/accessibility is not handled either.
 * See bottom of file for some test cases.
 *
 * This is because we don't have full access to types yet. This is the next
 * step.
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
     * it is unresolved
     * <li>There may still be AmbiguousNames, but only in expressions,
     * for the worst kind of ambiguity
     * </ul>
     */
    public static void disambig(JavaAstProcessor processor, NodeStream<? extends JavaNode> nodes) {
        JavaAstProcessor.bench("AST disambiguation", () -> nodes.forEach(it -> it.jjtAccept(DisambigVisitor.INSTANCE, processor)));
    }


    private static void reportUnresolvedMember(JavaNode location, JavaAstProcessor processor, Fallback fallbackStrategy, String memberName, JTypeDeclSymbol owner) {
        if (owner.isUnresolved()) {
            // would already have been reported on owner
            return;
        }

        String ownerName = owner instanceof JClassSymbol ? ((JClassSymbol) owner).getCanonicalName()
                                                         : "type variable " + owner.getSimpleName();

        processor.getLogger().warning(location, CANNOT_RESOLVE_MEMBER, memberName, ownerName, fallbackStrategy);
    }


    private enum Fallback {
        AMBIGUOUS("ambiguous"),
        FIELD_ACCESS("a field access"),
        PACKAGE_NAME("a package name"),
        TYPE("an unresolved type");

        private final String displayName;

        Fallback(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    // those ignore JTypeParameterSymbol, for error handling logic to be uniform

    @Nullable
    private static JFieldSymbol findStaticField(JTypeDeclSymbol classSym, String name) {
        // todo check supertypes
        return classSym instanceof JClassSymbol ? ((JClassSymbol) classSym).getDeclaredField(name)
                                                : null;
    }

    @Nullable
    private static JClassSymbol findTypeMember(JTypeDeclSymbol classSym, String name) {
        // todo check supertypes
        return classSym instanceof JClassSymbol ? ((JClassSymbol) classSym).getDeclaredClass(name)
                                                : null;
    }

    private static String unresolvedQualifier(JTypeDeclSymbol owner, String simpleName) {
        return owner instanceof JClassSymbol
               ? ((JClassSymbol) owner).getCanonicalName() + '.' + simpleName
               // this is not valid code, may break some assumptions elsewhere
               : owner.getSimpleName() + '.' + simpleName;
    }


    private static void checkParentIsMember(JavaAstProcessor processor, ASTClassOrInterfaceType resolvedType, ASTClassOrInterfaceType parent) {
        JTypeDeclSymbol sym = resolvedType.getReferencedSym();
        JClassSymbol parentClass = findTypeMember(sym, parent.getSimpleName());
        if (parentClass == null) {
            reportUnresolvedMember(parent, processor, Fallback.TYPE, parent.getSimpleName(), sym);
            String fullName = unresolvedQualifier(sym, parent.getSimpleName());
            int numTypeArgs = ASTList.orEmpty(parent.getTypeArguments()).size();
            parentClass = processor.makeUnresolvedReference(fullName, numTypeArgs);
        }
        parent.setSymbol(parentClass);
    }

    private static final class DisambigVisitor extends SideEffectingVisitorAdapter<JavaAstProcessor> {

        public static final DisambigVisitor INSTANCE = new DisambigVisitor();

        private void visitChildren(JavaNode node, JavaAstProcessor data) {
            // note that this differs from the default impl, because
            // the default declares last = node.getNumChildren()
            // at the beginning of the loop, but in this visitor the
            // number of children may change.
            for (int i = 0; i < node.getNumChildren(); i++) {
                node.getChild(i).jjtAccept(this, data);
            }
        }

        @Override
        public void visit(ASTAmbiguousName name, JavaAstProcessor processor) {
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
        }

        @Override
        public void visit(ASTClassOrInterfaceType type, JavaAstProcessor processor) {
            if (type.getReferencedSym() != null) {
                return;
            }

            visitChildren(type, processor);

            if (type.getReferencedSym() != null) {
                return;
            }

            ASTClassOrInterfaceType lhsType = type.getQualifier();
            if (lhsType != null) {
                JTypeDeclSymbol lhsSym = lhsType.getReferencedSym();
                assert lhsSym != null : "Unresolved LHS for " + type;
                checkParentIsMember(processor, lhsType, type);
            } else {
                ResolveResult<JTypeDeclSymbol> result = type.getSymbolTable().resolveTypeName(type.getSimpleName());
                JTypeDeclSymbol sym;
                if (result == null) {
                    processor.getLogger().warning(type, SemanticChecksLogger.CANNOT_RESOLVE_SYMBOL, type.getSimpleName());
                    int arity = ASTList.orEmpty(type.getTypeArguments()).size();
                    sym = processor.makeUnresolvedReference(type.getSimpleName(), arity);
                } else {
                    sym = result.getResult();
                }
                type.setSymbol(sym);

            }
            assert type.getReferencedSym() != null : "Null symbol for " + type;
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
        private static ASTExpression startResolve(ASTAmbiguousName name, JavaAstProcessor processor, boolean isPackageOrTypeOnly) {
            Iterator<JavaccToken> tokens = TokenUtils.tokenRange(name);
            JavaccToken firstIdent = tokens.next();
            TokenUtils.expectKind(firstIdent, JavaTokenKinds.IDENTIFIER);

            JSymbolTable symTable = name.getSymbolTable();

            if (!isPackageOrTypeOnly) {
                // first test if the leftmost segment is an expression
                ResolveResult<JVariableSymbol> varResult = symTable.resolveValueName(firstIdent.getImage());

                if (varResult != null) {
                    return resolveExpr(null, firstIdent, tokens, processor);
                }
            }

            // otherwise, test if it is a type name

            ResolveResult<JTypeDeclSymbol> typeResult = symTable.resolveTypeName(firstIdent.getImage());

            if (typeResult != null) {
                JTypeDeclSymbol result = typeResult.getResult();
                return resolveType(null, result, firstIdent.getImage(), firstIdent, tokens, name, isPackageOrTypeOnly, processor);
            }

            // otherwise, first is reclassified as package name.
            return resolvePackage(firstIdent, firstIdent.getImage(), tokens, name, isPackageOrTypeOnly, processor);
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
                                                 // JVariableSymbol varSym,         // we don't need that for now
                                                 JavaccToken identifier,            // identifier for the field/var name
                                                 Iterator<JavaccToken> remaining,   // rest of tokens, starting with following '.'
                                                 JavaAstProcessor processor) {

            TokenUtils.expectKind(identifier, JavaTokenKinds.IDENTIFIER);

            ASTExpression var = qualifier == null ? new ASTVariableAccess(identifier)
                                                  : new ASTFieldAccess(qualifier, identifier);

            if (!remaining.hasNext()) { // done
                return var;
            }

            JavaccToken nextIdent = skipToNextIdent(remaining);

            // following must also be expressions (field accesses)
            // we can't assert that for now, as symbols lack type information

            return resolveExpr(var, nextIdent, remaining, processor);
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
                                                 final JTypeDeclSymbol sym,                         // symbol for the type
                                                 final String image,                                // image of the new ClassType, possibly with a prepended package name
                                                 final JavaccToken identifier,                      // ident of the simple name of the symbol
                                                 final Iterator<JavaccToken> remaining,             // rest of tokens, starting with following '.'
                                                 final ASTAmbiguousName ambig,                      // original ambiguous name
                                                 final boolean isPackageOrTypeOnly,
                                                 final JavaAstProcessor processor) {

            TokenUtils.expectKind(identifier, JavaTokenKinds.IDENTIFIER);

            final ASTClassOrInterfaceType type = new ASTClassOrInterfaceType(qualifier, image, ambig.getFirstToken(), identifier);
            type.setSymbol(sym);

            if (!remaining.hasNext()) { // done
                return new ASTTypeExpression(type);
            }

            final JavaccToken nextIdent = skipToNextIdent(remaining);
            final String nextSimpleName = nextIdent.getImage();

            if (!isPackageOrTypeOnly) {
                JFieldSymbol field = findStaticField(sym, nextSimpleName);
                if (field != null) {
                    // todo check field is static
                    ASTTypeExpression typeExpr = new ASTTypeExpression(type);
                    return resolveExpr(typeExpr, nextIdent, remaining, processor);
                }
            }

            JClassSymbol inner = findTypeMember(sym, nextSimpleName);

            if (inner == null && isPackageOrTypeOnly) {
                // normally compile-time error, continue by considering it an unresolved inner type
                reportUnresolvedMember(ambig, processor, Fallback.TYPE, nextSimpleName, sym);
                inner = processor.makeUnresolvedReference(sym, nextSimpleName);
            }

            if (inner != null) {
                return resolveType(type, inner, nextSimpleName, nextIdent, remaining, ambig, isPackageOrTypeOnly, processor);
            }

            // no inner type, yet we have a lhs that is a type...
            // this is normally a compile-time error
            // treat as unresolved field accesses, this is the smoothest for later type res

            // todo report on the specific token failing
            reportUnresolvedMember(ambig, processor, Fallback.FIELD_ACCESS, nextSimpleName, sym);
            ASTTypeExpression typeExpr = new ASTTypeExpression(type);
            return resolveExpr(typeExpr, nextIdent, remaining, processor); // this will chain for the rest of the name
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
                                                    String packageImage,
                                                    Iterator<JavaccToken> remaining,
                                                    ASTAmbiguousName ambig,
                                                    boolean isPackageOrTypeOnly,
                                                    JavaAstProcessor processor) {

            TokenUtils.expectKind(identifier, JavaTokenKinds.IDENTIFIER);

            if (!remaining.hasNext()) {
                if (isPackageOrTypeOnly) {
                    // There's one last segment to try, the parent of the ambiguous name
                    // This may only be because this ambiguous name is the package qualification of the parent type
                    forceResolveAsFullPackageNameOfParent(packageImage, ambig, processor);
                    return ambig; // returning ambig makes the outer routine not replace
                }

                // then this name is unresolved, leave the ambiguous name in the tree
                // this only happens inside expressions
                processor.getLogger().warning(ambig, CANNOT_RESOLVE_AMBIGUOUS_NAME, packageImage, Fallback.AMBIGUOUS);
                return ambig;
            }

            JavaccToken nextIdent = skipToNextIdent(remaining);


            String canonical = packageImage + '.' + nextIdent.getImage();

            // Don't interpret periods as nested class separators (this will be handled by resolveType).
            // Otherwise lookup of a fully qualified name would be quadratic
            JClassSymbol nextClass = processor.getSymResolver().resolveClassFromBinaryName(canonical);

            if (nextClass != null) {
                return resolveType(null, nextClass, canonical, nextIdent, remaining, ambig, isPackageOrTypeOnly, processor);
            } else {
                return resolvePackage(nextIdent, canonical, remaining, ambig, isPackageOrTypeOnly, processor);
            }
        }

        /**
         * Force resolution of the ambiguous name as a package name.
         * The parent type's image is set to a package name + simple name.
         */
        private static void forceResolveAsFullPackageNameOfParent(String packageImage, ASTAmbiguousName ambig, JavaAstProcessor processor) {
            ASTClassOrInterfaceType parent = (ASTClassOrInterfaceType) ambig.getParent();

            String fullName = packageImage + '.' + parent.getSimpleName();
            JClassSymbol parentClass = processor.getSymResolver().resolveClassFromCanonicalName(fullName);
            if (parentClass == null) {
                processor.getLogger().warning(parent, CANNOT_RESOLVE_AMBIGUOUS_NAME, fullName, Fallback.TYPE);
                parentClass = processor.makeUnresolvedReference(fullName);
            }
            parent.setSymbol(parentClass);
            ambig.deleteInParentPrependImage('.');
        }

        private static JavaccToken skipToNextIdent(Iterator<JavaccToken> remaining) {
            JavaccToken dot = remaining.next();
            TokenUtils.expectKind(dot, JavaTokenKinds.DOT);
            assert remaining.hasNext() : "Ambiguous name must end with an identifier";
            return remaining.next();
        }
    }


    /*
        TODO: inheritance of type members

        class Scratch {
            interface A { class Mem {} }
            class Foo implements A { }

            Foo.Mem m; // ok, Foo inherits A.Mem
        }

        class Scratch2 {
            interface A { class Mem {} }
            interface B { class Mem {} }
            class Foo implements A, B { }

            Foo.Mem m; // not ok, Foo.Mem is ambiguous between A.Mem, B.Mem
        }

        TODO both names must be visible to produce an ambiguity error
     */

}
