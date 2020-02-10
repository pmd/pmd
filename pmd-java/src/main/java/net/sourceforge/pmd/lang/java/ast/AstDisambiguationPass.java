/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import static net.sourceforge.pmd.lang.java.symbols.table.internal.SemanticChecksLogger.CANNOT_RESOLVE_AMBIGUOUS_NAME;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.SemanticChecksLogger.CANNOT_RESOLVE_MEMBER;
import static net.sourceforge.pmd.lang.java.symbols.table.internal.SemanticChecksLogger.CANNOT_SELECT_MEMBER_FROM_TVAR;

import java.util.Iterator;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

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
 * This implements name disambiguation following the JLS: https://docs.oracle.com/javase/specs/jls/se8/html/jls-6.html#jls-6.5.2
 *
 * <p>Currently disambiguation of package vs type name is implemented.
 *
 * TODO ambiguous names in expressions - needs type res
 * TODO inherited member types (see bottom of file) - needs type res
 * TODO when symbol tables are updated to use type res, we can set the references directly on the names
 */
public final class AstDisambiguationPass {

    private AstDisambiguationPass() {
        // façade
    }

    public static void traverse(JavaAstProcessor processor, ASTCompilationUnit compilationUnit) {
        compilationUnit.jjtAccept(MyVisitor.INSTANCE, processor);
    }


    private static final class MyVisitor implements SideEffectingVisitor<JavaAstProcessor> {

        public static final MyVisitor INSTANCE = new MyVisitor();

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

            JavaNode resolved = startResolve(name, processor, isPackageOrTypeOnly);

            assert !isPackageOrTypeOnly
                || resolved instanceof ASTTypeExpression
                || resolved instanceof ASTAmbiguousName
                : "Unexpected result " + resolved + " for PackageOrTypeName resolution";

            if (isPackageOrTypeOnly && resolved instanceof ASTTypeExpression) {
                ASTClassOrInterfaceType resolvedType = (ASTClassOrInterfaceType) ((ASTTypeExpression) resolved).getTypeNode();
                resolved = resolvedType;
                // unambiguous, we just have to check that the parent is a member of the enclosing type
                ASTClassOrInterfaceType parent = (ASTClassOrInterfaceType) name.getParent();

                JTypeDeclSymbol sym = resolvedType.getReferencedSym();
                if (!(sym instanceof JClassSymbol)) {
                    processor.getLogger().error(name, CANNOT_SELECT_MEMBER_FROM_TVAR, parent.getSimpleName(), sym.getSimpleName());
                    return;
                } else {
                    JClassSymbol parentClass = ((JClassSymbol) sym).getDeclaredClass(parent.getSimpleName());
                    if (parentClass == null) {
                        processor.getLogger().warning(parent, CANNOT_RESOLVE_MEMBER, parent.getSimpleName(), ((JClassSymbol) sym).getCanonicalName(), "an unresolved type");
                    }
                }

            }

            if (resolved != name) {
                ((AbstractJavaNode) name.getParent()).replaceChildAt(name.getIndexInParent(), resolved);
            }
        }

        /**
         * Resolve an ambiguous name occurring in an expression context.
         * Returns the expression to which the name was resolved. If the
         * name is a type, this is a {@link ASTTypeExpression}, otherwise
         * it could be a {@link ASTFieldAccess} or {@link ASTVariableAccess},
         * and in the worst case, the original {@link ASTAmbiguousName}.
         */
        private ASTExpression startResolve(ASTAmbiguousName name, JavaAstProcessor processor, boolean isPackageOrTypeOnly) {
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
         * TODO We don't have yet the necessary machinery to assert that these are legal field accesses
         */
        private static ASTExpression resolveExpr(@Nullable ASTExpression qualifier,
                                                 // JVariableSymbol varSym, // we don't need that for now
                                                 JavaccToken identifier,
                                                 Iterator<JavaccToken> remaining,
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
         * Classify the given [identifier] as a reference to the [classSym].
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
        private static ASTExpression resolveType(@Nullable ASTClassOrInterfaceType qualifier,
                                                 JTypeDeclSymbol sym,
                                                 String image,
                                                 JavaccToken identifier,
                                                 Iterator<JavaccToken> remaining,
                                                 ASTAmbiguousName ambig,
                                                 boolean isPackageOrTypeOnly,
                                                 JavaAstProcessor processor) {

            TokenUtils.expectKind(identifier, JavaTokenKinds.IDENTIFIER);

            ASTClassOrInterfaceType type = new ASTClassOrInterfaceType(qualifier, image, ambig.jjtGetFirstToken(), identifier);
            type.setSymbol(sym);

            if (!remaining.hasNext()) { // done
                return new ASTTypeExpression(type);
            }

            JavaccToken nextIdent = skipToNextIdent(remaining);

            if (!(sym instanceof JClassSymbol)) {
                // this is broken code, reported above
                return new ASTTypeExpression(type);
            }

            JClassSymbol classSym = (JClassSymbol) sym;

            if (!isPackageOrTypeOnly) {
                // TODO check in supertypes, needs type res
                JFieldSymbol field = classSym.getDeclaredField(nextIdent.getImage());
                if (field != null) {
                    // todo check field is static
                    ASTTypeExpression typeExpr = new ASTTypeExpression(type);
                    return resolveExpr(typeExpr, identifier, remaining, processor);
                }
            }

            JClassSymbol inner = classSym.getDeclaredClass(nextIdent.getImage());
            if (inner != null) {
                return resolveType(type, inner, nextIdent.getImage(), nextIdent, remaining, ambig, isPackageOrTypeOnly, processor);
            }

            // this is normally a compile-time error

            // fallback
            if (!isPackageOrTypeOnly) {
                // treat as field accesses
                processor.getLogger().warning(ambig, CANNOT_RESOLVE_MEMBER, nextIdent.getImage(), classSym.getCanonicalName(), "a field access");

                ASTTypeExpression typeExpr = new ASTTypeExpression(type);
                return resolveExpr(typeExpr, identifier, remaining, processor);
            } else {
                // treat as ambiguous name, it's a better deal for type resolution later (at least in current prototype)
                processor.getLogger().warning(ambig, CANNOT_RESOLVE_MEMBER, nextIdent.getImage(), classSym.getCanonicalName(), "ambiguous");
                return ambig;
            }
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
                processor.getLogger().warning(ambig, CANNOT_RESOLVE_AMBIGUOUS_NAME, packageImage, "ambiguous");
                return ambig;
            }

            JavaccToken nextIdent = skipToNextIdent(remaining);


            String canonical = packageImage + '.' + nextIdent.getImage();

            JClassSymbol nextClass = processor.getSymResolver().resolveClassFromCanonicalName(canonical);

            if (nextClass != null) {
                return resolveType(null, nextClass, canonical, nextIdent, remaining, ambig, isPackageOrTypeOnly, processor);
            } else {
                return resolvePackage(nextIdent, canonical, remaining, ambig, isPackageOrTypeOnly, processor);
            }
        }

        private static void forceResolveAsFullPackageNameOfParent(String packageImage, ASTAmbiguousName ambig, JavaAstProcessor processor) {
            ASTClassOrInterfaceType parent = (ASTClassOrInterfaceType) ambig.getParent();

            String full = packageImage + '.' + parent.getSimpleName();
            JClassSymbol parentClass = processor.getSymResolver().resolveClassFromCanonicalName(full);
            if (parentClass == null) {
                processor.getLogger().warning(parent, CANNOT_RESOLVE_AMBIGUOUS_NAME, full, "package name");
            }
            ambig.deleteInParentPrependImage('.');
        }

        private static JavaccToken skipToNextIdent(Iterator<JavaccToken> remaining) {
            JavaccToken dot = remaining.next();
            TokenUtils.expectKind(dot, JavaTokenKinds.DOT);
            assert remaining.hasNext() : "Ambiguous name must end with an identifier";
            return remaining.next();
        }


        /**
         * Disambiguate a package or type name
         */
        private static void resolvePackageOrTypeName(final ASTAmbiguousName name,
                                                     final ASTClassOrInterfaceType parent,
                                                     final JavaAstProcessor processor) {

            // say we have a.b.C
            // a.b is the ambiguous segment (maybe package or type)
            // the parent is a.b.C

            List<String> segments = name.getSegments();
            String lastSimpleName = parent.getSimpleName();

            // try resolving 'a': type names obscure package names in this position
            ResolveResult<JTypeDeclSymbol> first = name.getSymbolTable().resolveTypeName(segments.get(0));

            if (first != null) {
                // a is a type name
                // then all following segments are nested type names (a.b)

                if (!checkTypeMemberChain(name, segments.subList(1, segments.size()),
                                          lastSimpleName, first.getResult(), processor.getLogger())) {
                    // leave it in the tree, errors were reported
                    // ideally we don't proceed to the next phase (type resolution)
                    return;
                }

                // right-to-left disambig
                ASTAmbiguousName n = name;
                ASTClassOrInterfaceType p = parent;
                do {
                    p = forceTypeCtxImpl(n, p);
                    n = p.getAmbiguousLhs();
                } while (n != null);

                // now p == leftmost
            } else {
                // interpret as FQCN

                // try fetching a.b.C from the classloader
                String canonicalName = name.getName() + "." + lastSimpleName;
                JClassSymbol sym = processor.getSymResolver().resolveClassFromCanonicalName(canonicalName);

                if (sym != null) {
                    // then the ambiguous name is a full qualifier
                    // but any of its segments may be a enclosing type name

                    // right-to-left disambig
                    ASTAmbiguousName n = name;
                    ASTClassOrInterfaceType p = parent;
                    while (sym.getEnclosingClass() != null && n != null) {
                        p = forceTypeCtxImpl(n, p);
                        n = p.getAmbiguousLhs();
                        sym = sym.getEnclosingClass();
                    }

                    if (n != null) {
                        // Some prefix is a package qualifier (the remaining 'n')
                        // eg in a.b.C, we may have found that C is an inner class of a.b, but a is a package name
                        // More simply, it may be that a.b is the package name of a.b.C

                        // Then, we delete the remaining prefix, since it's not ambiguous
                        n.deleteInParentPrependImage('.');
                    }
                } else {
                    // hmm
                    // So here:
                    // * a.b.C is not a known type (may exist but be unresolved)
                    // * a is not a known type either (may be inherited by superclasses, or be in the same package and be unresolved)

                    // either way we hit ambiguity and have to make a choice:
                    // 1. consider a.b.C a FQCN, or
                    // 2. consider than a is an unresolved type

                    // for now we choose 1 always.
                    // we could also leave the ambiguous name in the tree,
                    // but it's not very helpful when the ambiguity is just package vs type,
                    // it just complicates later analysis

                    processor.getLogger().warning(name, CANNOT_RESOLVE_AMBIGUOUS_NAME, canonicalName, "fully qualified name");
                    name.deleteInParentPrependImage('.');
                }
            }
        }


        private static ASTClassOrInterfaceType forceTypeCtxImpl(ASTAmbiguousName name, ASTClassOrInterfaceType parent) {
            ASTClassOrInterfaceType type = name.forceTypeContext();
            parent.replaceChildAt(name.getIndexInParent(), type);
            return type;
        }

        // TODO how strict do we need to be here?
        //   many rules don't absolutely need correctness to work
        //   maybe we need to identify separate "levels" of the tree
        //   eg level 0: lexable (CPD)
        //      level 2: parsable (many syntax-only rules, eg UnnecessaryParentheses)
        //      level 3: type-resolved (more complicated rules)

        /**
         * Checks that each segment is a type member of the previous.
         */
        private static boolean checkTypeMemberChain(ASTAmbiguousName name,
                                                    List<String> segments,
                                                    String lastSegment,
                                                    JTypeDeclSymbol root,
                                                    SemanticChecksLogger logger) {

            if (!(root instanceof JClassSymbol)) {
                // type variables cannot have type members
                reportUnselectable(logger, name, segments.get(0), root);
                return false;
            }

            // TODO should not lookup only the declared class, it potentially may lookup the whole hierarchy
            //  see bottom for test cases

            JClassSymbol sym = (JClassSymbol) root;

            for (String segment : segments) {
                JClassSymbol inner = sym.getDeclaredClass(segment);
                if (inner == null) {
                    reportUnselectable(logger, name, segment, sym);
                    return false;
                }
                sym = inner;
            }

            JClassSymbol last = sym.getDeclaredClass(lastSegment);
            if (last == null) {
                reportUnselectable(logger, name, lastSegment, sym);
                return false;
            }

            return true;
        }


        private static void reportUnselectable(SemanticChecksLogger logger, JavaNode loc, String memberName, JTypeDeclSymbol parentSym) {
            logger.error(
                loc,
                CANNOT_SELECT_MEMBER_FROM_TVAR,
                memberName,
                parentSym instanceof JClassSymbol ? "class" : "type variable",
                parentSym.getSimpleName()
            );
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
