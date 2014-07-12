/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ImmutableLanguage;

public abstract class AbstractJavaRule extends AbstractRule implements JavaParserVisitor, ImmutableLanguage {

    public AbstractJavaRule() {
	super.setLanguage(Language.JAVA);
	// Enable Type Resolution on Java Rules by default
	super.setUsesTypeResolution();
    }

    public void apply(List<? extends Node> nodes, RuleContext ctx) {
	visitAll(nodes, ctx);
    }

    protected void visitAll(List<? extends Node> nodes, RuleContext ctx) {
	for (Object element : nodes) {
	    ASTCompilationUnit node = (ASTCompilationUnit) element;
	    visit(node, ctx);
	}
    }

    /**
     * Gets the Image of the first parent node of type
     * ASTClassOrInterfaceDeclaration or <code>null</code>
     *
     * @param node
     *            the node which will be searched
     */
    protected final String getDeclaringType(Node node) {
	ASTClassOrInterfaceDeclaration c = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
	if (c != null) {
	    return c.getImage();
	}
	return null;
    }

    public static boolean isQualifiedName(Node node) {
	return node.getImage().indexOf('.') != -1;
    }

    public static boolean importsPackage(ASTCompilationUnit node, String packageName) {
	List<ASTImportDeclaration> nodes = node.findChildrenOfType(ASTImportDeclaration.class);
	for (ASTImportDeclaration n : nodes) {
	    if (n.getPackageName().startsWith(packageName)) {
		return true;
	    }
	}
	return false;
    }

    protected boolean isSuppressed(Node node) {
        return JavaRuleViolation.isSupressed(node, this);
    }

    //
    // The following APIs are identical to those in JavaParserVisitorAdapter.
    // Due to Java single inheritance, it preferred to extend from the more
    // complex Rule base class instead of from relatively simple Visitor.
    //
    public Object visit(JavaNode node, Object data) {
	node.childrenAccept(this, data);
	return null;
    }

    public Object visit(ASTExtendsList node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTImplementsList node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTTypeParameters node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTMemberSelector node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTTypeParameter node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTTypeBound node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTClassOrInterfaceBody node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTClassOrInterfaceBodyDeclaration node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTEnumBody node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTEnumConstant node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTReferenceType node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTClassOrInterfaceType node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTTypeArguments node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTTypeArgument node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTWildcardBounds node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTAnnotation node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTNormalAnnotation node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTMarkerAnnotation node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTSingleMemberAnnotation node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTMemberValuePairs node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTMemberValuePair node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTMemberValue node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTMemberValueArrayInitializer node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTAnnotationTypeBody node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTAnnotationTypeMemberDeclaration node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTAnnotationMethodDeclaration node, Object data) {
	return visit((JavaNode)node, data);
    }

    public Object visit(ASTDefaultValue node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTRUNSIGNEDSHIFT node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTRSIGNEDSHIFT node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTCompilationUnit node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTEnumDeclaration node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTAssertStatement node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTPackageDeclaration node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTImportDeclaration node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTTypeDeclaration node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTFieldDeclaration node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTVariableDeclarator node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTVariableDeclaratorId node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTVariableInitializer node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTArrayInitializer node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTMethodDeclarator node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTFormalParameters node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTFormalParameter node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTConstructorDeclaration node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTExplicitConstructorInvocation node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTInitializer node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTType node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTPrimitiveType node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTResultType node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTName node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTNameList node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTAssignmentOperator node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTConditionalExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTConditionalOrExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTConditionalAndExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTInclusiveOrExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTExclusiveOrExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTAndExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTEqualityExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTInstanceOfExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTRelationalExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTShiftExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTAdditiveExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTMultiplicativeExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTUnaryExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTPreIncrementExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTPreDecrementExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTUnaryExpressionNotPlusMinus node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTPostfixExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTCastExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTPrimaryExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTPrimaryPrefix node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTPrimarySuffix node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTLiteral node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTBooleanLiteral node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTNullLiteral node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTArguments node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTArgumentList node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTAllocationExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTArrayDimsAndInits node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTStatement node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTLabeledStatement node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTBlock node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTBlockStatement node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTLocalVariableDeclaration node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTEmptyStatement node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTStatementExpression node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTSwitchStatement node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTSwitchLabel node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTIfStatement node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTWhileStatement node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTDoStatement node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTForStatement node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTForInit node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTStatementExpressionList node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTForUpdate node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTBreakStatement node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTContinueStatement node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTReturnStatement node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTThrowStatement node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTSynchronizedStatement node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTTryStatement node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTFinallyStatement node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTCatchStatement node, Object data) {
	return visit((JavaNode) node, data);
    }

    public Object visit(ASTResourceSpecification node, Object data) {
         return visit((JavaNode)node, data);
    }

    public Object visit(ASTResources node, Object data) {
         return visit((JavaNode)node, data);
    }

    public Object visit(ASTResource node, Object data) {
         return visit((JavaNode)node, data);
    }

    public Object visit(ASTLambdaExpression node, Object data) {
        return visit((JavaNode)node, data);
    }

    public Object visit(ASTMethodReference node, Object data) {
        return visit((JavaNode)node, data);
    }
}
