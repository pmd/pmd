/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Backwards-compatibility only.
 *
 * @deprecated Use {@link JavaVisitor}
 */
@Deprecated
@DeprecatedUntil700
public interface JavaParserVisitor extends JavaVisitor<Object, Object> {

    @Override
    default Object visitNode(Node node, Object param) {
        for (Node c : node.children()) {
            c.acceptVisitor(this, param);
        }
        return param;
    }

    // REMOVE ME
    // deprecated stuff kept for compatibility with existing visitors, not matched by anything

    @Deprecated
    default Object visit(ASTExpression node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTLiteral node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTType node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTReferenceType node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTConditionalOrExpression node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTVariableInitializer node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTAssignmentOperator node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTConditionalAndExpression node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTInclusiveOrExpression node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTExclusiveOrExpression node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTAndExpression node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTEqualityExpression node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTRelationalExpression node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTShiftExpression node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTAdditiveExpression node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTMultiplicativeExpression node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTAnnotationMethodDeclaration node, Object data) {
        return null;
    }

    default Object visit(ASTStatement node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTPrimaryPrefix node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTPrimarySuffix node, Object data) {
        return null;
    }


    @Deprecated
    default Object visit(ASTPrimaryExpression node, Object data) {
        return null;
    }


    @Deprecated
    default Object visit(ASTAllocationExpression node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTTypeArgument node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTWildcardBounds node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTUnaryExpressionNotPlusMinus node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTBlockStatement node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTInstanceOfExpression node, Object data) {
        return null;
    }


    @Deprecated
    default Object visit(ASTStatementExpression node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTMethodDeclarator node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTArguments node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTClassOrInterfaceBodyDeclaration node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTResultType node, Object data) {
        return null;
    }

}
