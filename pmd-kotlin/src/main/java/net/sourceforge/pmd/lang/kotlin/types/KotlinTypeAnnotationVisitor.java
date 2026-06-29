/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.types;

import java.util.List;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtCatchBlock;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassParameter;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtConstructorInvocation;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtForStatement;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtPropertyDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtUserType;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinVisitorBase;
import net.sourceforge.pmd.lang.kotlin.rule.internal.KotlinTypeAnalysisContext;

import nl.stokpop.typemapper.model.DeclarationAst;
import nl.stokpop.typemapper.model.DeclarationKind;

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
 * @since 7.26.0
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

        @Override
        public Void visitPropertyDeclaration(KtPropertyDeclaration node, Void data) {
            List<DeclarationAst> decls = ctx.declarationsAt(absPath, node.getBeginLine());
            for (DeclarationAst decl : decls) {
                if (decl.getType() != null) {
                    KotlinNodeTypeData.setTypeName(node, decl.getType());
                    AnnotationFqnAnnotator.setAnnotationFqns(node, decl.getAnnotations());
                    break;
                }
            }
            return visitChildren(node, data);
        }

        // Primary constructor val/var parameters (e.g. "class Foo(val name: String)")
        // are KtClassParameter nodes in the AST, not KtPropertyDeclaration.
        // kotlin-type-mapper emits them as kind="property" with a type field.
        @Override
        public Void visitClassParameter(KtClassParameter node, Void data) {
            List<DeclarationAst> decls = ctx.declarationsAt(absPath, node.getBeginLine());
            for (DeclarationAst decl : decls) {
                if (decl.getKind() == DeclarationKind.PROPERTY && decl.getType() != null) {
                    KotlinNodeTypeData.setTypeName(node, decl.getType());
                    AnnotationFqnAnnotator.setAnnotationFqns(node, decl.getAnnotations());
                    break;
                }
            }
            return visitChildren(node, data);
        }

        @Override
        public Void visitFunctionDeclaration(KtFunctionDeclaration node, Void data) {
            List<DeclarationAst> decls = ctx.declarationsAt(absPath, node.getBeginLine());
            for (DeclarationAst decl : decls) {
                if (decl.getReturnType() != null) {
                    KotlinNodeTypeData.setReturnTypeName(node, decl.getReturnType());
                    AnnotationFqnAnnotator.setAnnotationFqns(node, decl.getAnnotations());
                    FunctionParameterAnnotator.setFunctionParameterTypes(node, decl.getParameters());
                    break;
                }
            }
            return visitChildren(node, data);
        }

        @Override
        public Void visitCatchBlock(KtCatchBlock node, Void data) {
            List<DeclarationAst> decls = ctx.declarationsAt(absPath, node.getBeginLine());
            for (DeclarationAst decl : decls) {
                if (decl.getKind() == DeclarationKind.CATCH_VARIABLE && decl.getType() != null) {
                    KotlinNodeTypeData.setTypeName(node, decl.getType());
                    break;
                }
            }
            return visitChildren(node, data);
        }

        @Override
        public Void visitForStatement(KtForStatement node, Void data) {
            List<DeclarationAst> decls = ctx.declarationsAt(absPath, node.getBeginLine());
            for (DeclarationAst decl : decls) {
                if (decl.getKind() == DeclarationKind.FOR_LOOP_VARIABLE && decl.getType() != null) {
                    KotlinNodeTypeData.setTypeName(node, decl.getType());
                    break;
                }
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
                    KotlinNodeTypeData.setTypeName(node, decl.getFqName());
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
