/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * External wrapper for a visitor decorator. This one drives the AST visit, delegating to the base controlless visitor
 * given in the constructor. Add decorators using the {@link #decorateWith(JavaParserVisitorDecorator)}.
 *
 * <p>Important! This modified decorator pattern compels you to use the data object as the accumulator for your result!
 * The {@code visit} methods or your decorators and base visitors must only perform side effects on this object, their
 * return values will be ignored.
 *
 * @author Clément Fournier
 * @since 6.0.0
 *
 * @deprecated Visitor decorators are deprecated because they lead to fragile code.
 */
@Deprecated
public class JavaParserDecoratedVisitor implements JavaParserVisitor {


    private JavaParserControllessVisitor visitor;


    /**
     * Creates a decorated visitor using the parameter as the base visitor. Add decorators using the {@link
     * #decorateWith(JavaParserVisitorDecorator)} method.
     *
     * @param baseVisitor The base visitor
     */
    public JavaParserDecoratedVisitor(JavaParserControllessVisitor baseVisitor) {
        this.visitor = baseVisitor;
    }


    /**
     * Adds a decorator to this decorated visitor.
     *
     * @param decorator The decorator to add
     */
    public void decorateWith(JavaParserVisitorDecorator decorator) {
        decorator.setBase(visitor);
        visitor = decorator;
    }


    @Override
    public Object visit(JavaNode node, Object data) {
        return node.childrenAccept(this, data);
    }


    @Override
    public Object visit(ASTExtendsList node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTImplementsList node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTTypeParameters node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTMemberSelector node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTTypeParameter node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTTypeBound node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTClassOrInterfaceBody node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTClassOrInterfaceBodyDeclaration node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTEnumBody node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTEnumConstant node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTReferenceType node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTClassOrInterfaceType node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTTypeArguments node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTTypeArgument node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTWildcardBounds node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTAnnotation node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTNormalAnnotation node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTMarkerAnnotation node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTSingleMemberAnnotation node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTMemberValuePairs node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTMemberValuePair node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTMemberValue node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTMemberValueArrayInitializer node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTAnnotationTypeBody node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTAnnotationTypeMemberDeclaration node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTAnnotationMethodDeclaration node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTDefaultValue node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    /**
     * @deprecated Will be removed in 7.0.0. Use {@link ASTShiftExpression#getOperator()}
     */
    @Override
    @Deprecated
    public Object visit(ASTRUNSIGNEDSHIFT node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    /**
     * @deprecated Will be removed in 7.0.0. Use {@link ASTShiftExpression#getOperator()}
     */
    @Override
    @Deprecated
    public Object visit(ASTRSIGNEDSHIFT node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTAssertStatement node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTPackageDeclaration node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTTypeDeclaration node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTVariableDeclarator node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTVariableInitializer node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTArrayInitializer node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTMethodDeclarator node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTFormalParameters node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTFormalParameter node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTExplicitConstructorInvocation node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTInitializer node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTType node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTPrimitiveType node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTResultType node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTName node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTNameList node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTAssignmentOperator node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTConditionalExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTConditionalOrExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTConditionalAndExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTInclusiveOrExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTExclusiveOrExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTAndExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTEqualityExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTInstanceOfExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTRelationalExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTShiftExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTAdditiveExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTMultiplicativeExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTUnaryExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTPreIncrementExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTPreDecrementExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTUnaryExpressionNotPlusMinus node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTPostfixExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTCastExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTPrimaryExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTPrimaryPrefix node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTPrimarySuffix node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTLiteral node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTBooleanLiteral node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTNullLiteral node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTArguments node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTArgumentList node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTAllocationExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTArrayDimsAndInits node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTStatement node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTLabeledStatement node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTBlock node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTBlockStatement node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTEmptyStatement node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTStatementExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTSwitchLabel node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTIfStatement node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTDoStatement node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTForStatement node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTForInit node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTStatementExpressionList node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTForUpdate node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTBreakStatement node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTContinueStatement node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTThrowStatement node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTSynchronizedStatement node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTTryStatement node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTResourceSpecification node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTResources node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTResource node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTFinallyStatement node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTCatchStatement node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTLambdaExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTMethodReference node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTModuleDeclaration node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTModuleDirective node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTModuleName node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTSwitchExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTSwitchLabeledBlock node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTSwitchLabeledExpression node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTSwitchLabeledThrowStatement node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTYieldStatement node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }

    @Override
    @Experimental
    public Object visit(ASTTypeTestPattern node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }

    @Override
    @Experimental
    public Object visit(ASTRecordDeclaration node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }

    @Override
    @Experimental
    public Object visit(ASTRecordComponentList node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }

    @Override
    @Experimental
    public Object visit(ASTRecordComponent node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }

    @Override
    @Experimental
    public Object visit(ASTRecordBody node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }

    @Override
    @Experimental
    public Object visit(ASTRecordConstructorDeclaration node, Object data) {
        visitor.visit(node, data);
        return visit((JavaNode) node, data);
    }
}
