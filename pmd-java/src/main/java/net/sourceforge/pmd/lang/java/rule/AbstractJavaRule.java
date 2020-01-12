/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeBody;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeMemberDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTArrayDimsAndInits;
import net.sourceforge.pmd.lang.java.ast.ASTArrayInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTAssertStatement;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTDefaultValue;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTEmptyStatement;
import net.sourceforge.pmd.lang.java.ast.ASTEnumBody;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExclusiveOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExtendsList;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFinallyStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForUpdate;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTImplementsList;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInclusiveOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTInstanceOfExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLabeledStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMarkerAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTMemberSelector;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValue;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValueArrayInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValuePair;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValuePairs;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTModuleDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTModuleDirective;
import net.sourceforge.pmd.lang.java.ast.ASTModuleName;
import net.sourceforge.pmd.lang.java.ast.ASTMultiplicativeExpression;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNameList;
import net.sourceforge.pmd.lang.java.ast.ASTNormalAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPostfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPreDecrementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPreIncrementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTRSIGNEDSHIFT;
import net.sourceforge.pmd.lang.java.ast.ASTRUNSIGNEDSHIFT;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTRelationalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.ASTResourceSpecification;
import net.sourceforge.pmd.lang.java.ast.ASTResources;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTShiftExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSingleMemberAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpressionList;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabeledBlock;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabeledExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabeledThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTTypeArgument;
import net.sourceforge.pmd.lang.java.ast.ASTTypeArguments;
import net.sourceforge.pmd.lang.java.ast.ASTTypeBound;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameter;
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameters;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpressionNotPlusMinus;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWildcardBounds;
import net.sourceforge.pmd.lang.java.ast.ASTYieldStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitor;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ImmutableLanguage;

public abstract class AbstractJavaRule extends AbstractRule implements JavaParserVisitor, ImmutableLanguage {

    public AbstractJavaRule() {
        super.setLanguage(LanguageRegistry.getLanguage(JavaLanguageModule.NAME));
        // Enable Type Resolution on Java Rules by default
        super.setTypeResolution(true);
    }

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        visitAll(nodes, ctx);
    }

    protected void visitAll(List<? extends Node> nodes, RuleContext ctx) {
        for (Object element : nodes) {
            /*
                It is important to note that we are assuming that all nodes here are of type Compilation Unit,
                but our caller method may be called with any type of node, and that's why we need to check the kind
                of instance of each element
            */
            if (element instanceof ASTCompilationUnit) {
                ASTCompilationUnit node = (ASTCompilationUnit) element;
                visit(node, ctx);
            }
        }
    }

    /**
     * Gets the Image of the first parent node of type
     * ASTClassOrInterfaceDeclaration or <code>null</code>
     *
     * @param node
     *            the node which will be searched
     *
     * @deprecated This method just returns the type name as a string
     *     which doesn't leverage any type resolution. Use {@link Node#getFirstParentOfType(Class)}
     *     directly to find the node of type {@link ASTClassOrInterfaceBodyDeclaration} via the
     *     {@code getType} method.
     */
    @Deprecated
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

    /**
     * @deprecated Not useful, and suppression should happen transparently to rule implementations.
     *             This will be removed with 7.0.0
     */
    @Deprecated
    protected boolean isSuppressed(Node node) {
        return JavaRuleViolation.isSupressed(node, this);
    }

    //
    // The following APIs are identical to those in JavaParserVisitorAdapter.
    // Due to Java single inheritance, it preferred to extend from the more
    // complex Rule base class instead of from relatively simple Visitor.
    //
    @Override
    public Object visit(JavaNode node, Object data) {
        for (JavaNode child : node.children()) {
            child.jjtAccept(this, data);
        }
        return null;
    }

    @Override
    public Object visit(ASTExtendsList node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTImplementsList node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTTypeParameters node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTMemberSelector node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTTypeParameter node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTTypeBound node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTClassOrInterfaceBody node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTClassOrInterfaceBodyDeclaration node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTEnumBody node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTEnumConstant node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTReferenceType node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTClassOrInterfaceType node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTTypeArguments node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTTypeArgument node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTWildcardBounds node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTAnnotation node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTNormalAnnotation node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTMarkerAnnotation node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTSingleMemberAnnotation node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTMemberValuePairs node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTMemberValuePair node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTMemberValue node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTMemberValueArrayInitializer node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTAnnotationTypeBody node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTAnnotationTypeMemberDeclaration node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTAnnotationMethodDeclaration node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTDefaultValue node, Object data) {
        return visit((JavaNode) node, data);
    }


    /**
     * @deprecated Will be removed in 7.0.0. Use {@link ASTShiftExpression#getOperator()}
     */
    @Override
    @Deprecated
    public Object visit(ASTRUNSIGNEDSHIFT node, Object data) {
        return visit((JavaNode) node, data);
    }


    /**
     * @deprecated Will be removed in 7.0.0. Use {@link ASTShiftExpression#getOperator()}
     */
    @Override
    @Deprecated
    public Object visit(ASTRSIGNEDSHIFT node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTAssertStatement node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTPackageDeclaration node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTTypeDeclaration node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTVariableDeclarator node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTVariableInitializer node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTArrayInitializer node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTMethodDeclarator node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTFormalParameters node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTFormalParameter node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTExplicitConstructorInvocation node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTInitializer node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTType node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTPrimitiveType node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTResultType node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTName node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTNameList node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTAssignmentOperator node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTConditionalExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTConditionalOrExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTConditionalAndExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTInclusiveOrExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTExclusiveOrExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTAndExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTEqualityExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTInstanceOfExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTRelationalExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTShiftExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTAdditiveExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTMultiplicativeExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTUnaryExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTPreIncrementExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTPreDecrementExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTUnaryExpressionNotPlusMinus node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTPostfixExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTCastExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTPrimaryExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTPrimaryPrefix node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTPrimarySuffix node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTLiteral node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTBooleanLiteral node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTNullLiteral node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTArguments node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTArgumentList node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTAllocationExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTArrayDimsAndInits node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTStatement node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTLabeledStatement node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTBlock node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTBlockStatement node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTEmptyStatement node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTStatementExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTSwitchLabel node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTDoStatement node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTForStatement node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTForInit node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTStatementExpressionList node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTForUpdate node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTBreakStatement node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTContinueStatement node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTThrowStatement node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTSynchronizedStatement node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTTryStatement node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTFinallyStatement node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTCatchStatement node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTResourceSpecification node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTResources node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTResource node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTLambdaExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTMethodReference node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTModuleDeclaration node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTModuleDirective node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTModuleName node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTSwitchExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTSwitchLabeledBlock node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTSwitchLabeledExpression node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTSwitchLabeledThrowStatement node, Object data) {
        return visit((JavaNode) node, data);
    }

    @Override
    public Object visit(ASTYieldStatement node, Object data) {
        return visit((JavaNode) node, data);
    }
}
