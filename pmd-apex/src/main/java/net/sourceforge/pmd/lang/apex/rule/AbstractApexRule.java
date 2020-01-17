/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.apex.ApexParserOptions;
import net.sourceforge.pmd.lang.apex.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.apex.ast.ASTAnnotationParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTAnonymousClass;
import net.sourceforge.pmd.lang.apex.ast.ASTArrayLoadExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTArrayStoreExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTBinaryExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTBindExpressions;
import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTBooleanExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTBridgeMethodCreator;
import net.sourceforge.pmd.lang.apex.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTCatchBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTClassRefExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTConstructorPreamble;
import net.sourceforge.pmd.lang.apex.ast.ASTConstructorPreambleStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlDeleteStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlInsertStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlMergeStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUndeleteStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUpdateStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUpsertStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDoLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclarationStatements;
import net.sourceforge.pmd.lang.apex.ast.ASTForEachStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTFormalComment;
import net.sourceforge.pmd.lang.apex.ast.ASTIfBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTIfElseBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTIllegalStoreExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTInstanceOfExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTJavaMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTJavaVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTMapEntryNode;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTModifier;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierOrAnnotation;
import net.sourceforge.pmd.lang.apex.ast.ASTMultiStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTNestedExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTNestedStoreExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTNewKeyValueObjectExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTNewListInitExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTNewListLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTNewMapInitExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTNewMapLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTNewObjectExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTNewSetInitExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTNewSetLiteralExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTPackageVersionExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTPostfixExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTPrefixExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
import net.sourceforge.pmd.lang.apex.ast.ASTReferenceExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTRunAsBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTSoslExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTStandardCondition;
import net.sourceforge.pmd.lang.apex.ast.ASTStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTStatementExecuted;
import net.sourceforge.pmd.lang.apex.ast.ASTSuperMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTSuperVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTTernaryExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTThisMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTThisVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTTriggerVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTTryCatchFinallyBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClassMethods;
import net.sourceforge.pmd.lang.apex.ast.ASTUserEnum;
import net.sourceforge.pmd.lang.apex.ast.ASTUserExceptionMethods;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.ASTUserTrigger;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclarationStatements;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTWhileLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.AbstractApexNodeBase;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexParserVisitor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ImmutableLanguage;
import net.sourceforge.pmd.renderers.CodeClimateRule;

public abstract class AbstractApexRule extends AbstractRule
        implements ApexParserVisitor, ImmutableLanguage, CodeClimateRule {

    public AbstractApexRule() {
        super.setLanguage(LanguageRegistry.getLanguage(ApexLanguageModule.NAME));
        definePropertyDescriptor(CODECLIMATE_CATEGORIES);
        definePropertyDescriptor(CODECLIMATE_REMEDIATION_MULTIPLIER);
        definePropertyDescriptor(CODECLIMATE_BLOCK_HIGHLIGHTING);
    }

    @Override
    public ParserOptions getParserOptions() {
        return new ApexParserOptions();
    }

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        visitAll(nodes, ctx);
    }

    protected void visitAll(List<? extends Node> nodes, RuleContext ctx) {
        for (Object element : nodes) {
            if (element instanceof ASTUserClass) {
                visit((ASTUserClass) element, ctx);
            } else if (element instanceof ASTUserInterface) {
                visit((ASTUserInterface) element, ctx);
            } else if (element instanceof ASTUserTrigger) {
                visit((ASTUserTrigger) element, ctx);
            }
        }
    }

    /**
     * @deprecated Use {@link #visit(ApexNode, Object)}. That method
     *     also visits comments now.
     */
    @Deprecated
    @Override
    public Object visit(AbstractApexNodeBase node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }


    @Override
    public Object visit(ApexNode<?> node, Object data) {
        for (ApexNode<?> child : node.children()) {
            child.jjtAccept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTModifierNode node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTParameter node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTBlockStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTUserClassMethods node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTBridgeMethodCreator node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTLiteralExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTConstructorPreambleStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTUserInterface node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTUserEnum node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTWhileLoopStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTTryCatchFinallyBlockStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTForLoopStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTIfElseBlockStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTIfBlockStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTForEachStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTField node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTBreakStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTThrowStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTDoLoopStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTTernaryExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTSoqlExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTBooleanExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTAnnotation node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTAnonymousClass node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTArrayLoadExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTArrayStoreExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTAssignmentExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTBinaryExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTBindExpressions node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTCatchBlockStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTClassRefExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTContinueStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTDmlDeleteStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTDmlInsertStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTDmlMergeStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTDmlUndeleteStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTDmlUpdateStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTDmlUpsertStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTExpressionStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTFieldDeclarationStatements node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTInstanceOfExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTJavaMethodCallExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTJavaVariableExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTMapEntryNode node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTMethodCallExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTModifierOrAnnotation node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNewListInitExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNewListLiteralExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNewMapInitExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNewMapLiteralExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNewObjectExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNewSetInitExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNewSetLiteralExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTPackageVersionExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTPostfixExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTPrefixExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTProperty node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTReferenceExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTRunAsBlockStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTSoslExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTStandardCondition node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTSuperMethodCallExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTSuperVariableExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTThisMethodCallExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTThisVariableExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTTriggerVariableExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTUserExceptionMethods node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTUserTrigger node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTVariableDeclaration node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTVariableDeclarationStatements node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTVariableExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTAnnotationParameter node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTCastExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTConstructorPreamble node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTIllegalStoreExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTMethodBlockStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTModifier node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTMultiStatement node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNestedExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNestedStoreExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTNewKeyValueObjectExpression node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTStatementExecuted node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    @Override
    public Object visit(ASTFormalComment node, Object data) {
        return visit((ApexNode<?>) node, data);
    }
}
