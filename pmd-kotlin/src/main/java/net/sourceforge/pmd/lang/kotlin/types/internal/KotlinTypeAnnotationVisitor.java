/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.types.internal;

import java.util.List;
import java.util.function.Predicate;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtCatchBlock;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassParameter;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtConstructorInvocation;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtForStatement;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtMultiVariableDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtPropertyDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtUserType;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtVariableDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinVisitorBase;
import net.sourceforge.pmd.lang.kotlin.rule.internal.KotlinTypeAnalysisContext;
import net.sourceforge.pmd.lang.kotlin.types.InternalApiBridge;
import net.sourceforge.pmd.lang.kotlin.types.KotlinTypeName;

import nl.stokpop.typemapper.model.DeclarationAst;
import nl.stokpop.typemapper.model.DeclarationKind;
import nl.stokpop.typemapper.model.TypeAst;

/**
 * Walks a parsed Kotlin AST and sets type/annotation attributes on nodes using
 * pre-analyzed type data from kotlin-type-mapper:
 *
 * <ul>
 *   <li>type data on {@code PropertyDeclaration} nodes (property type)</li>
 *   <li>type data on {@code ClassParameter} nodes -- primary constructor
 *       {@code val}/{@code var} params (e.g. {@code class Foo(val name: String)})</li>
 *   <li>type data on {@code FunctionDeclaration} nodes (return type)</li>
 *   <li>type data on {@code FunctionValueParameter} nodes (parameter type)
 *       -- delegated to {@link FunctionParameterAnnotator}</li>
 *   <li>type data on {@code CatchBlock} nodes (caught exception type)</li>
 *   <li>type data on {@code ForStatement} nodes (loop variable type)</li>
 *   <li>type data on {@code UnescapedAnnotation} <em>and</em>
 *       {@code SingleAnnotation} nodes (annotation FQN)
 *       -- delegated to {@link AnnotationFqnAnnotator}</li>
 *   <li>type data on declaration nodes (comma-joined FQN list)
 *       -- delegated to {@link AnnotationFqnAnnotator}</li>
 *   <li>type data on {@code DelegationSpecifier} nodes (supertype FQN)
 *       -- delegated to {@link DelegationSpecifierAnnotator}</li>
 * </ul>
 *
 * <p>The visitor is constructed once per analysis run (from the
 * {@link KotlinTypeAnalysisContext} produced by kotlin-type-mapper) and applied
 * to each file's root node during the post-parse step inside
 * {@code KotlinLanguageProcessor}.
 *
 * @since 7.27.0
 * @experimental
 */
@Experimental
public final class KotlinTypeAnnotationVisitor {

    private final KotlinTypeAnalysisContext ctx;

    public KotlinTypeAnnotationVisitor(KotlinTypeAnalysisContext ctx) {
        this.ctx = ctx;
    }

    public KotlinTypeAnalysisContext getContext() {
        return ctx;
    }

    /**
     * Annotates all {@code PropertyDeclaration}, {@code FunctionDeclaration},
     * {@code ClassDeclaration}, {@code CatchBlock}, and {@code ForStatement} nodes in
     * the given AST root, and sets {@code @TypeName} on their annotation children
     * as well as on {@code FunctionValueParameter} children of function declarations.
     *
     * @param root     the root node of the parsed Kotlin file
     * @param absPath  the absolute path of the file (used to look up declarations by file)
     */
    public void annotate(KtKotlinFile root, String absPath) {
        root.acceptVisitor(new AnnotatingVisitor(ctx, absPath), null);
    }

    /**
     * Visitor that annotates PMD AST nodes with type/annotation attributes from
     * the kotlin-type-mapper data indexed by file path and line number.
     *
     * <p>Delegation-specifier, annotation-attribute, and parameter-type logic is delegated to
     * {@link DelegationSpecifierAnnotator}, {@link AnnotationFqnAnnotator},
     * and {@link FunctionParameterAnnotator} respectively.
     */
    private static final class AnnotatingVisitor extends KotlinVisitorBase<Void, Void> {

        private final KotlinTypeAnalysisContext ctx;
        private final String absPath;

        AnnotatingVisitor(KotlinTypeAnalysisContext ctx, String absPath) {
            this.ctx = ctx;
            this.absPath = absPath;
        }

        /**
         * Picks the declaration matching {@code candidateFilter} whose source-column range
         * overlaps {@code node}'s, to disambiguate multiple declarations recorded on the same
         * line (e.g. {@code val a: String = ""; val b: String? = null}). Falls back to the
         * first matching candidate if no column overlap is found (e.g. an annotation placed on
         * its own line, resolved via the +/-1 line tolerance in
         * {@link KotlinTypeAnalysisContext#declarationsAt}, where column data for the two lines
         * isn't comparable).
         */
        private static DeclarationAst selectDeclaration(
                List<DeclarationAst> decls, Node node, Predicate<DeclarationAst> candidateFilter) {
            DeclarationAst firstMatch = null;
            for (DeclarationAst decl : decls) {
                if (!candidateFilter.test(decl)) {
                    continue;
                }
                if (firstMatch == null) {
                    firstMatch = decl;
                }
                if (columnsOverlap(node, decl)) {
                    return decl;
                }
            }
            return firstMatch;
        }

        private static boolean columnsOverlap(Node node, DeclarationAst decl) {
            // No end-column data (older kotlin-type-mapper JSON schema) -- can't compare.
            return decl.getEndColumn() > 0
                    && decl.getColumn() <= node.getEndColumn() && decl.getEndColumn() >= node.getBeginColumn();
        }

        @Override
        public Void visitPropertyDeclaration(KtPropertyDeclaration node, Void data) {
            annotatePropertyType(node);
            return visitChildren(node, data);
        }

        // Restricted to kind=PROPERTY so that a destructuring declaration
        // (e.g. "val (a, b) = ...", whose components are recorded as
        // kind=DESTRUCTURED_VARIABLE at the same line) doesn't leak the first
        // component's type onto the whole PropertyDeclaration node: there is no
        // single type for the destructured tuple as a whole.
        private void annotatePropertyType(KotlinNode node) {
            List<DeclarationAst> decls = ctx.declarationsAt(absPath, node.getBeginLine());
            DeclarationAst decl = selectDeclaration(decls, node,
                    d -> d.getKind() == DeclarationKind.PROPERTY && d.getType() != null);
            if (decl != null) {
                InternalApiBridge.setType(node, toKotlinTypeName(decl.getType()));
                AnnotationFqnAnnotator.setAnnotationFqns(node, decl.getAnnotations());
            }
        }

        // Each declared variable is its own KtVariableDeclaration node: "x" in
        // "val x: String = ...", "item" in "for (item in items)", and each of "a"/"b"
        // in destructuring declarations ("val (a, b) = ...", "for ((a, b) in ...)",
        // "{ (a, b) -> ... }"). Matched individually (narrow, precise column range)
        // so each gets its own correct type, instead of relying on the wider
        // PropertyDeclaration/ForStatement-level match.
        @Override
        public Void visitVariableDeclaration(KtVariableDeclaration node, Void data) {
            List<DeclarationAst> decls = ctx.declarationsAt(absPath, node.getBeginLine());
            // Destructured components only match their own DESTRUCTURED_VARIABLE entry, never
            // the enclosing for-loop parameter, whose column range also covers "(a, b)".
            boolean destructured = node.getParent() instanceof KtMultiVariableDeclaration;
            DeclarationAst decl = selectDeclaration(decls, node,
                    d -> (destructured ? d.getKind() == DeclarationKind.DESTRUCTURED_VARIABLE : isVariableKind(d.getKind()))
                            && d.getType() != null);
            if (decl != null) {
                InternalApiBridge.setType(node, toKotlinTypeName(decl.getType()));
            }
            return visitChildren(node, data);
        }

        private static boolean isVariableKind(DeclarationKind kind) {
            return kind == DeclarationKind.PROPERTY || kind == DeclarationKind.FOR_LOOP_VARIABLE;
        }

        // Primary constructor val/var parameters (e.g. "class Foo(val name: String)")
        // are KtClassParameter nodes in the AST, not KtPropertyDeclaration.
        // kotlin-type-mapper emits them as kind="property" with a type field.
        @Override
        public Void visitClassParameter(KtClassParameter node, Void data) {
            annotatePropertyType(node);
            return visitChildren(node, data);
        }

        @Override
        public Void visitFunctionDeclaration(KtFunctionDeclaration node, Void data) {
            List<DeclarationAst> decls = ctx.declarationsAt(absPath, node.getBeginLine());
            DeclarationAst decl = selectDeclaration(decls, node, d -> d.getReturnType() != null);
            if (decl != null) {
                InternalApiBridge.setReturnType(node, toKotlinTypeName(decl.getReturnType()));
                AnnotationFqnAnnotator.setAnnotationFqns(node, decl.getAnnotations());
                FunctionParameterAnnotator.setFunctionParameterTypes(node, decl.getParameters());
            }
            return visitChildren(node, data);
        }

        @Override
        public Void visitCatchBlock(KtCatchBlock node, Void data) {
            List<DeclarationAst> decls = ctx.declarationsAt(absPath, node.getBeginLine());
            DeclarationAst decl = selectDeclaration(decls, node,
                    d -> d.getKind() == DeclarationKind.CATCH_VARIABLE && d.getType() != null);
            if (decl != null) {
                InternalApiBridge.setType(node, toKotlinTypeName(decl.getType()));
            }
            return visitChildren(node, data);
        }

        @Override
        public Void visitForStatement(KtForStatement node, Void data) {
            List<DeclarationAst> decls = ctx.declarationsAt(absPath, node.getBeginLine());
            DeclarationAst decl = selectDeclaration(decls, node,
                    d -> d.getKind() == DeclarationKind.FOR_LOOP_VARIABLE && d.getType() != null);
            if (decl != null) {
                InternalApiBridge.setType(node, toKotlinTypeName(decl.getType()));
            }
            return visitChildren(node, data);
        }

        @Override
        public Void visitClassDeclaration(KtClassDeclaration node, Void data) {
            List<DeclarationAst> decls = ctx.declarationsAt(absPath, node.getBeginLine());
            for (DeclarationAst decl : decls) {
                if (decl.getKind() == DeclarationKind.CLASS
                        || decl.getKind() == DeclarationKind.DATA_CLASS
                        || decl.getKind() == DeclarationKind.SEALED_CLASS
                        || decl.getKind() == DeclarationKind.INTERFACE
                        || decl.getKind() == DeclarationKind.ENUM) {
                    // Set @TypeName to the class's own FQN (useful in Designer + XPath)
                    InternalApiBridge.setType(node, new KotlinTypeName(
                            decl.getFqName(), false, false, decl.getFqName()));
                    AnnotationFqnAnnotator.setAnnotationFqns(node, decl.getAnnotations());
                    DelegationSpecifierAnnotator.setDelegationSpecifierTypes(node, decl.getSuperTypes());
                    break;
                }
            }
            return visitChildren(node, data);
        }
    }

    /** Extracts the simple (unqualified) name from a fully-qualified name. */
    static String simpleNameOf(String name) {
        int dot = name.lastIndexOf('.');
        return dot >= 0 ? name.substring(dot + 1) : name;
    }

    /** Returns the raw type name by removing generic arguments, e.g. {@code List<String> -> List}. */
    static String rawTypeNameOf(String name) {
        int angle = name.indexOf('<');
        return angle >= 0 ? name.substring(0, angle).trim() : name;
    }

    /** Converts a kotlin-type-mapper {@link TypeAst} to a PMD-owned {@link KotlinTypeName}. */
    static KotlinTypeName toKotlinTypeName(TypeAst typeAst) {
        return new KotlinTypeName(
                typeAst.getFqName(),
                typeAst.isNullable(),
                typeAst.isUnresolved(),
                typeAst.toFqString());
    }

    /**
     * Finds the first {@code KtUserType} directly inside a {@code KtConstructorInvocation}.
     * Shared helper used by both {@link DelegationSpecifierAnnotator} and
     * {@link AnnotationFqnAnnotator}.
     */
    static KtUserType findUserTypeInConstructorInvocation(KtConstructorInvocation ctorInvocation) {
        for (int j = 0; j < ctorInvocation.getNumChildren(); j++) {
            if (ctorInvocation.getChild(j) instanceof KtUserType) {
                return (KtUserType) ctorInvocation.getChild(j);
            }
        }
        return null;
    }
}
