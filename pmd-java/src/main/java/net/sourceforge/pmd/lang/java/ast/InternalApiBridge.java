/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.internal.JavaAstProcessor;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.symbols.table.internal.ReferenceCtx;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JVariableSig;
import net.sourceforge.pmd.lang.java.types.JVariableSig.FieldSig;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.util.document.TextDocument;

/**
 * Acts as a bridge between outer parts of PMD and the restricted access
 * internal API of this package.
 *
 * <p><b>None of this is published API, and compatibility can be broken anytime!</b>
 * Use this only at your own risk.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
@InternalApi
public final class InternalApiBridge {


    private InternalApiBridge() {

    }

    @Deprecated
    public static ASTVariableDeclaratorId newVarId(String image) {
        ASTVariableDeclaratorId varid = new ASTVariableDeclaratorId(JavaParserImplTreeConstants.JJTVARIABLEDECLARATORID);
        varid.setImage(image);
        return varid;
    }


    /**
     * Creates a fake method name declaration for built-in methods from Java
     * like the Enum Method "valueOf".
     *
     * @param methodName     the method name
     * @param parameterTypes the reference types of each parameter of the method
     *
     * @return a method name declaration
     */
    public static ASTMethodDeclaration createBuiltInMethodDeclaration(final String methodName, final String... parameterTypes) {
        ASTMethodDeclaration methodDeclaration = new ASTMethodDeclaration(0);
        // InternalApiBridge.setModifier(methodDeclaration, JModifier.PUBLIC);

        methodDeclaration.setImage(methodName);

        ASTFormalParameters formalParameters = new ASTFormalParameters(0);
        methodDeclaration.addChild(formalParameters, 0);

        /*
         * jjtAddChild resizes it's child node list according to known indexes.
         * Going backwards makes sure the first time it gets the right size avoiding copies.
         */
        for (int i = parameterTypes.length - 1; i >= 0; i--) {
            ASTFormalParameter formalParameter = new ASTFormalParameter(0);
            formalParameters.addChild(formalParameter, i);

            ASTVariableDeclaratorId variableDeclaratorId = new ASTVariableDeclaratorId(0);
            variableDeclaratorId.setImage("arg" + i);
            formalParameter.addChild(variableDeclaratorId, 1);

            PrimitiveTypeKind primitive = PrimitiveTypeKind.fromName(parameterTypes[i]);
            // TODO : this could actually be a primitive array...
            AbstractJavaNode type = primitive != null
                           ? new ASTPrimitiveType(primitive)
                           : new ASTClassOrInterfaceType(parameterTypes[i]);

            formalParameter.addChild(type, 0);
        }

        return methodDeclaration;
    }

    public static void setSymbol(SymbolDeclaratorNode node, JElementSymbol symbol) {
        if (node instanceof ASTMethodDeclaration) {
            ((ASTMethodDeclaration) node).setSymbol((JMethodSymbol) symbol);
        } else if (node instanceof ASTConstructorDeclaration) {
            ((ASTConstructorDeclaration) node).setSymbol((JConstructorSymbol) symbol);
        } else if (node instanceof ASTAnyTypeDeclaration) {
            ((AbstractAnyTypeDeclaration) node).setSymbol((JClassSymbol) symbol);
        } else if (node instanceof ASTVariableDeclaratorId) {
            ((ASTVariableDeclaratorId) node).setSymbol((JVariableSymbol) symbol);
        } else if (node instanceof ASTTypeParameter) {
            ((ASTTypeParameter) node).setSymbol((JTypeParameterSymbol) symbol);
        } else if (node instanceof ASTRecordComponentList) {
            ((ASTRecordComponentList) node).setSymbol((JConstructorSymbol) symbol);
        } else {
            throw new AssertionError("Cannot set symbol " + symbol + " on node " + node);
        }
    }

    public static void disambigWithCtx(NodeStream<? extends JavaNode> nodes, ReferenceCtx ctx) {
        AstDisambiguationPass.disambigWithCtx(nodes, ctx);
    }

    public static void usageResolution(JavaAstProcessor processor, ASTCompilationUnit root) {
        root.descendants(ASTNamedReferenceExpr.class)
            .crossFindBoundaries()
            .forEach(node -> {
                JVariableSymbol sym = node.getReferencedSym();
                if (sym != null) {
                    ASTVariableDeclaratorId reffed = sym.tryGetNode();
                    if (reffed != null) { // declared in this file
                        reffed.addUsage(node);
                    }
                }
            });
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

    public static void setFunctionalMethod(ASTMethodReference methodReference, JMethodSig methodType) {
        methodReference.setFunctionalMethod(methodType);
    }

    public static void setFunctionalMethod(ASTLambdaExpression lambda, @Nullable JMethodSig methodType) {
        lambda.setFunctionalMethod(methodType);
    }

    public static void setCompileTimeDecl(ASTMethodReference methodReference, JMethodSig methodType) {
        methodReference.setCompileTimeDecl(methodType);
    }

    public static void initTypeResolver(ASTCompilationUnit acu, JavaAstProcessor processor, TypeInferenceLogger typeResolver) {
        acu.setTypeResolver(new LazyTypeResolver(processor, typeResolver));
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

    public static void setSymbolTable(JavaNode node, JSymbolTable table) {
        ((AbstractJavaNode) node).setSymbolTable(table);
    }

    public static void setScope(JavaNode node, Scope scope) {
        ((AbstractJavaNode) node).setScope(scope);
    }

    public static void setQname(ASTAnyTypeDeclaration declaration, String binaryName, @Nullable String canon) {
        ((AbstractAnyTypeDeclaration) declaration).setBinaryName(binaryName, canon);
    }

    public static void assignComments(ASTCompilationUnit root) {
        CommentAssignmentPass.assignCommentsToDeclarations(root);
    }

    public static JavaccTokenDocument javaTokenDoc(TextDocument fullText) {
        return new JavaTokenDocument(fullText);
    }
}
