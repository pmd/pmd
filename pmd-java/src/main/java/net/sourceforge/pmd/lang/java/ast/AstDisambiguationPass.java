/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
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
        public void visit(ASTAmbiguousName node, JavaAstProcessor processor) {
            JSymbolTable symbolTable = node.getSymbolTable();
            assert symbolTable != null : "Symbol tables haven't been set??";

            if (node.getParent() instanceof ASTClassOrInterfaceType) {
                resolvePackageOrTypeName(node, (ASTClassOrInterfaceType) node.getParent(), processor);
            }
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

                    processor.getLogger().warning(name, SemanticChecksLogger.CANNOT_RESOLVE_AMBIGUOUS_NAME, canonicalName, "fully qualified name");
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
                SemanticChecksLogger.CANNOT_SELECT_TYPE_MEMBER,
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
     */

}
