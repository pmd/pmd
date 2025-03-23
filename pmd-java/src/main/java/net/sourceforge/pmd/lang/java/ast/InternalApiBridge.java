/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.SemanticException;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JRecordComponentSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.internal.ReferenceCtx;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JVariableSig;
import net.sourceforge.pmd.lang.java.types.JVariableSig.FieldSig;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.ast.ExprContext;
import net.sourceforge.pmd.lang.java.types.ast.internal.LazyTypeResolver;
import net.sourceforge.pmd.lang.java.types.internal.infer.Infer;
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger;
import net.sourceforge.pmd.util.AssertionUtil;

/**
 * Internal API.
 *
 * <p>Acts as a bridge between outer parts of PMD and the restricted access
 * internal API of this package.
 *
 * <p><b>None of this is published API, and compatibility can be broken anytime!</b>
 * Use this only at your own risk.
 *
 * @author Clément Fournier
 * @since 7.0.0
 * @apiNote Internal API
 */
@InternalApi
public final class InternalApiBridge {
    private InternalApiBridge() {}

    public static void setSymbol(SymbolDeclaratorNode node, JElementSymbol symbol) {
        if (node instanceof ASTMethodDeclaration) {
            ((ASTMethodDeclaration) node).setSymbol((JMethodSymbol) symbol);
        } else if (node instanceof ASTConstructorDeclaration) {
            ((ASTConstructorDeclaration) node).setSymbol((JConstructorSymbol) symbol);
        } else if (node instanceof ASTTypeDeclaration) {
            ((AbstractTypeDeclaration) node).setSymbol((JClassSymbol) symbol);
        } else if (node instanceof ASTVariableId) {
            ((ASTVariableId) node).setSymbol((JVariableSymbol) symbol);
        } else if (node instanceof ASTTypeParameter) {
            ((ASTTypeParameter) node).setSymbol((JTypeParameterSymbol) symbol);
        } else if (node instanceof ASTRecordComponentList) {
            ((ASTRecordComponentList) node).setSymbol((JConstructorSymbol) symbol);
        } else if (node instanceof ASTRecordComponent) {
            ((ASTRecordComponent) node).setSymbol((JRecordComponentSymbol) symbol);
        } else {
            throw new AssertionError("Cannot set symbol " + symbol + " on node " + node);
        }
    }

    public static void disambigWithCtx(NodeStream<? extends JavaNode> nodes, ReferenceCtx ctx) {
        AstDisambiguationPass.disambigWithCtx(nodes, ctx);
    }

    /**
     * Forcing type resolution allows us to report errors more cleanly
     * than if it was done completely lazy. Failures (other than semantic exceptions)
     * are thrown, because they are bugs in the typeres framework.
     * Semantic exceptions cause execution to abort too, but only right before
     * rules are applied, so several semantic exceptions may be collected.
     */
    public static void forceTypeResolutionPhase(JavaAstProcessor processor, ASTCompilationUnit root) {
        root.descendants(TypeNode.class)
            .crossFindBoundaries()
            .forEach(typeNode -> {
                try {
                    typeNode.getTypeMirror();
                } catch (SemanticException e) {
                    processor.getLogger().acceptError(e);
                }
            });
    }

    public static void usageResolution(JavaAstProcessor processor, ASTCompilationUnit root) {
        root.descendants(ASTNamedReferenceExpr.class)
            .crossFindBoundaries()
            .forEach(node -> {
                JVariableSymbol sym = node.getReferencedSym();
                if (sym != null) {
                    ASTVariableId reffed = sym.tryGetNode();
                    if (reffed != null) { // declared in this file
                        reffed.addUsage(node);
                    }
                }
            });
    }

    public static void overrideResolution(JavaAstProcessor processor, ASTCompilationUnit root) {
        root.descendants(ASTTypeDeclaration.class)
            .crossFindBoundaries()
            .forEach(OverrideResolutionPass::resolveOverrides);
    }

    public static @Nullable JTypeMirror getTypeMirrorInternal(TypeNode node) {
        return ((AbstractJavaTypeNode) node).getTypeMirrorInternal();
    }

    public static void setTypeMirrorInternal(TypeNode node, JTypeMirror inferred) {
        ((AbstractJavaTypeNode) node).setTypeMirror(inferred);
    }

    public static void setSignature(ASTFieldAccess node, FieldSig sig) {
        node.setTypedSym(sig);
    }

    public static void setSignature(ASTVariableAccess node, JVariableSig sig) {
        node.setTypedSym(sig);
    }

    public static void setFunctionalMethod(FunctionalExpression node, JMethodSig methodType) {
        if (node instanceof ASTMethodReference) {
            ((ASTMethodReference) node).setFunctionalMethod(methodType);
        } else if (node instanceof ASTLambdaExpression) {
            ((ASTLambdaExpression) node).setFunctionalMethod(methodType);
        } else {
            throw AssertionUtil.shouldNotReachHere("FunctionalExpression is not handled: " + node);
        }
    }

    public static void setCompileTimeDecl(ASTMethodReference methodReference, JMethodSig methodType) {
        methodReference.setCompileTimeDecl(methodType);
    }

    public static void initTypeResolver(ASTCompilationUnit acu, JavaAstProcessor processor, TypeInferenceLogger logger) {
        acu.setTypeResolver(new LazyTypeResolver(processor, logger));
    }

    public static void setOverload(InvocationNode expression, OverloadSelectionResult result) {
        if (expression instanceof AbstractInvocationExpr) {
            ((AbstractInvocationExpr) expression).setOverload(result);
        } else if (expression instanceof ASTExplicitConstructorInvocation) {
            ((ASTExplicitConstructorInvocation) expression).setOverload(result);
        } else if (expression instanceof ASTEnumConstant) {
            ((ASTEnumConstant) expression).setOverload(result);
        } else {
            throw new IllegalArgumentException("Wrong type: " + expression);
        }
    }

    public static JavaAstProcessor getProcessor(JavaNode n) {
        return n.getRoot().getLazyTypeResolver().getProcessor();
    }

    public static Infer getInferenceEntryPoint(JavaNode n) {
        return n.getRoot().getLazyTypeResolver().getInfer();
    }

    public static @NonNull LazyTypeResolver getLazyTypeResolver(JavaNode n) {
        return n.getRoot().getLazyTypeResolver();
    }

    public static @NonNull ExprContext getTopLevelExprContext(TypeNode n) {
        return n.getRoot().getLazyTypeResolver().getTopLevelContextIncludingInvocation(n);
    }

    public static void setSymbolTable(JavaNode node, JSymbolTable table) {
        ((AbstractJavaNode) node).setSymbolTable(table);
    }

    public static void setQname(ASTTypeDeclaration declaration, String binaryName, @Nullable String canon) {
        ((AbstractTypeDeclaration) declaration).setBinaryName(binaryName, canon);
    }

    public static void assignComments(ASTCompilationUnit root) {
        CommentAssignmentPass.assignCommentsToDeclarations(root);
    }

    public static JavaccTokenDocument.TokenDocumentBehavior javaTokenDoc() {
        return JavaTokenDocumentBehavior.INSTANCE;
    }

    public static void setStandaloneTernary(ASTConditionalExpression node) {
        node.setStandaloneTernary();
    }

    public static boolean isStandaloneInternal(ASTConditionalExpression node) {
        return node.isStandalone();
    }

    public static JTypeMirror buildTypeFromAstInternal(TypeSystem ts, Substitution lexicalSubst, ASTType node) {
        return TypesFromAst.fromAst(ts, lexicalSubst, node);
    }

    public static JTypeDeclSymbol getReferencedSym(ASTClassType type) {
        return type.getReferencedSym();
    }

    public static void setTypedSym(ASTFieldAccess expr, JVariableSig.FieldSig sym) {
        expr.setTypedSym(sym);
    }

    public static void setTypedSym(ASTVariableAccess expr, JVariableSig sym) {
        expr.setTypedSym(sym);
    }
}
