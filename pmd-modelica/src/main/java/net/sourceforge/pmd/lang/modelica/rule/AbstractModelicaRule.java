/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.modelica.ModelicaLanguageModule;
import net.sourceforge.pmd.lang.modelica.ast.ASTAddOp;
import net.sourceforge.pmd.lang.modelica.ast.ASTAlgorithmSection;
import net.sourceforge.pmd.lang.modelica.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.modelica.ast.ASTArgument;
import net.sourceforge.pmd.lang.modelica.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.modelica.ast.ASTArithmeticExpression;
import net.sourceforge.pmd.lang.modelica.ast.ASTArraySubscripts;
import net.sourceforge.pmd.lang.modelica.ast.ASTAssignmentFromMultiResultFunctionCall;
import net.sourceforge.pmd.lang.modelica.ast.ASTAssignmentModification;
import net.sourceforge.pmd.lang.modelica.ast.ASTAssignmentStatement;
import net.sourceforge.pmd.lang.modelica.ast.ASTBasePrefix;
import net.sourceforge.pmd.lang.modelica.ast.ASTBlockClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.modelica.ast.ASTClassClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTClassDefinition;
import net.sourceforge.pmd.lang.modelica.ast.ASTClassModification;
import net.sourceforge.pmd.lang.modelica.ast.ASTClassPrefixes;
import net.sourceforge.pmd.lang.modelica.ast.ASTClassSpecifier;
import net.sourceforge.pmd.lang.modelica.ast.ASTColonSubsript;
import net.sourceforge.pmd.lang.modelica.ast.ASTComment;
import net.sourceforge.pmd.lang.modelica.ast.ASTComponentClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTComponentClause1;
import net.sourceforge.pmd.lang.modelica.ast.ASTComponentDeclaration;
import net.sourceforge.pmd.lang.modelica.ast.ASTComponentDeclaration1;
import net.sourceforge.pmd.lang.modelica.ast.ASTComponentList;
import net.sourceforge.pmd.lang.modelica.ast.ASTComponentReference;
import net.sourceforge.pmd.lang.modelica.ast.ASTComposition;
import net.sourceforge.pmd.lang.modelica.ast.ASTConditionAttribute;
import net.sourceforge.pmd.lang.modelica.ast.ASTConnectClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTConnectorClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTConstantClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTConstrainingClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTDeclaration;
import net.sourceforge.pmd.lang.modelica.ast.ASTDerClassSpecifier;
import net.sourceforge.pmd.lang.modelica.ast.ASTDerClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTDiscreteClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTEachClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTElementList;
import net.sourceforge.pmd.lang.modelica.ast.ASTElementModification;
import net.sourceforge.pmd.lang.modelica.ast.ASTElementModificationOrReplaceable;
import net.sourceforge.pmd.lang.modelica.ast.ASTElementRedeclaration;
import net.sourceforge.pmd.lang.modelica.ast.ASTElementReplaceable;
import net.sourceforge.pmd.lang.modelica.ast.ASTElseClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTElseIfClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTElseWhenClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTEncapsulatedClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTEnumList;
import net.sourceforge.pmd.lang.modelica.ast.ASTEnumerationLiteral;
import net.sourceforge.pmd.lang.modelica.ast.ASTEnumerationShortClassSpecifier;
import net.sourceforge.pmd.lang.modelica.ast.ASTEquation;
import net.sourceforge.pmd.lang.modelica.ast.ASTEquationList;
import net.sourceforge.pmd.lang.modelica.ast.ASTEquationSection;
import net.sourceforge.pmd.lang.modelica.ast.ASTExpandableConnectorClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTExpressionList;
import net.sourceforge.pmd.lang.modelica.ast.ASTExtendingLongClassSpecifier;
import net.sourceforge.pmd.lang.modelica.ast.ASTExtendsClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTExternalClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTExternalFunctionCall;
import net.sourceforge.pmd.lang.modelica.ast.ASTFactor;
import net.sourceforge.pmd.lang.modelica.ast.ASTFalseLiteral;
import net.sourceforge.pmd.lang.modelica.ast.ASTFinalClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTFlowClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTForEquation;
import net.sourceforge.pmd.lang.modelica.ast.ASTForIndex;
import net.sourceforge.pmd.lang.modelica.ast.ASTForIndices;
import net.sourceforge.pmd.lang.modelica.ast.ASTForStatement;
import net.sourceforge.pmd.lang.modelica.ast.ASTFunctionArgument;
import net.sourceforge.pmd.lang.modelica.ast.ASTFunctionArguments;
import net.sourceforge.pmd.lang.modelica.ast.ASTFunctionCallArgs;
import net.sourceforge.pmd.lang.modelica.ast.ASTFunctionCallEquation;
import net.sourceforge.pmd.lang.modelica.ast.ASTFunctionCallStatement;
import net.sourceforge.pmd.lang.modelica.ast.ASTFunctionClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTFunctionInvocation;
import net.sourceforge.pmd.lang.modelica.ast.ASTIfClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTIfEquation;
import net.sourceforge.pmd.lang.modelica.ast.ASTIfExpression;
import net.sourceforge.pmd.lang.modelica.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.modelica.ast.ASTImportClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTImportList;
import net.sourceforge.pmd.lang.modelica.ast.ASTImpureClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTInitialClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTInnerClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTInputClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTLanguageSpecification;
import net.sourceforge.pmd.lang.modelica.ast.ASTListOfExpressionLists;
import net.sourceforge.pmd.lang.modelica.ast.ASTLogicalExpression;
import net.sourceforge.pmd.lang.modelica.ast.ASTLogicalTerm;
import net.sourceforge.pmd.lang.modelica.ast.ASTLongModification;
import net.sourceforge.pmd.lang.modelica.ast.ASTModelClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTMulOp;
import net.sourceforge.pmd.lang.modelica.ast.ASTMultipleDefinitionImportClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTName;
import net.sourceforge.pmd.lang.modelica.ast.ASTNamedArgument;
import net.sourceforge.pmd.lang.modelica.ast.ASTNamedArguments;
import net.sourceforge.pmd.lang.modelica.ast.ASTNegated;
import net.sourceforge.pmd.lang.modelica.ast.ASTNumberLiteral;
import net.sourceforge.pmd.lang.modelica.ast.ASTOperator;
import net.sourceforge.pmd.lang.modelica.ast.ASTOperatorClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTOperatorRecordClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTOuterClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTOutputClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTOutputExpressionList;
import net.sourceforge.pmd.lang.modelica.ast.ASTPackageClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTParameterClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTPartialClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTPureClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTRecordClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTRedeclareClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTRegularElement;
import net.sourceforge.pmd.lang.modelica.ast.ASTRegularEquation;
import net.sourceforge.pmd.lang.modelica.ast.ASTRelOp;
import net.sourceforge.pmd.lang.modelica.ast.ASTRelation;
import net.sourceforge.pmd.lang.modelica.ast.ASTRenamingImportClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTReplaceableClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.modelica.ast.ASTShortClassDefinition;
import net.sourceforge.pmd.lang.modelica.ast.ASTShortModification;
import net.sourceforge.pmd.lang.modelica.ast.ASTSimpleExpression;
import net.sourceforge.pmd.lang.modelica.ast.ASTSimpleLongClassSpecifier;
import net.sourceforge.pmd.lang.modelica.ast.ASTSimpleName;
import net.sourceforge.pmd.lang.modelica.ast.ASTSimpleShortClassSpecifier;
import net.sourceforge.pmd.lang.modelica.ast.ASTSingleDefinitionImportClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTStatement;
import net.sourceforge.pmd.lang.modelica.ast.ASTStatementList;
import net.sourceforge.pmd.lang.modelica.ast.ASTStoredDefinition;
import net.sourceforge.pmd.lang.modelica.ast.ASTStreamClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTStringComment;
import net.sourceforge.pmd.lang.modelica.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.modelica.ast.ASTSubscript;
import net.sourceforge.pmd.lang.modelica.ast.ASTSubscriptedName;
import net.sourceforge.pmd.lang.modelica.ast.ASTTerm;
import net.sourceforge.pmd.lang.modelica.ast.ASTThenClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTTrueLiteral;
import net.sourceforge.pmd.lang.modelica.ast.ASTTypeClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTTypePrefix;
import net.sourceforge.pmd.lang.modelica.ast.ASTTypeSpecifier;
import net.sourceforge.pmd.lang.modelica.ast.ASTUnqualifiedImportClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTWhenClause;
import net.sourceforge.pmd.lang.modelica.ast.ASTWhenEquation;
import net.sourceforge.pmd.lang.modelica.ast.ASTWhenStatement;
import net.sourceforge.pmd.lang.modelica.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.modelica.ast.ASTWithinClause;
import net.sourceforge.pmd.lang.modelica.ast.ModelicaNode;
import net.sourceforge.pmd.lang.modelica.ast.ModelicaParserVisitor;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ImmutableLanguage;

/**
 * Base class for rules for Modelica language.
 */
public abstract class AbstractModelicaRule extends AbstractRule implements ModelicaParserVisitor, ImmutableLanguage {
    public AbstractModelicaRule() {
        super.setLanguage(LanguageRegistry.getLanguage(ModelicaLanguageModule.NAME));
    }

    @Override
    public void apply(final List<? extends Node> nodes, final RuleContext ctx) {
        visitAll(nodes, ctx);
    }

    protected void visitAll(final List<? extends Node> nodes, final RuleContext ctx) {
        for (final Object element : nodes) {
            final ASTStoredDefinition node = (ASTStoredDefinition) element;
            visit(node, ctx);
        }
    }

    @Override
    public Object visit(ModelicaNode node, Object data) {
        for (ModelicaNode child : node.children()) {
            child.jjtAccept(this, data);
        }
        return data;
    }

    @Override
    public Object visit(ASTNegated node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTStoredDefinition node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTWithinClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTClassDefinition node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTEncapsulatedClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTClassSpecifier node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTClassPrefixes node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTPartialClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTClassClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTModelClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTRecordClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTOperatorRecordClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTBlockClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTConnectorClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTExpandableConnectorClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTTypeClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTPackageClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTPureClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTImpureClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTOperatorClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTFunctionClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTOperator node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTSimpleLongClassSpecifier node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTExtendingLongClassSpecifier node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTSimpleShortClassSpecifier node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTEnumerationShortClassSpecifier node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTDerClassSpecifier node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTDerClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTBasePrefix node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTEnumList node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTEnumerationLiteral node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTComposition node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTExternalClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTLanguageSpecification node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTExternalFunctionCall node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTElementList node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTRedeclareClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTFinalClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTInnerClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTOuterClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTReplaceableClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTRegularElement node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTImportClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTRenamingImportClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTUnqualifiedImportClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTMultipleDefinitionImportClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTSingleDefinitionImportClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTImportList node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTExtendsClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTConstrainingClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTComponentClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTTypePrefix node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTFlowClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTStreamClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTDiscreteClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTParameterClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTConstantClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTInputClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTOutputClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTTypeSpecifier node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTComponentList node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTComponentDeclaration node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTConditionAttribute node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTDeclaration node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTLongModification node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTShortModification node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTAssignmentModification node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTClassModification node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTArgumentList node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTArgument node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTElementModificationOrReplaceable node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTEachClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTElementModification node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTElementRedeclaration node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTElementReplaceable node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTComponentClause1 node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTComponentDeclaration1 node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTShortClassDefinition node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTEquationSection node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTInitialClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTAlgorithmSection node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTEquation node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTRegularEquation node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTFunctionCallEquation node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTStatement node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTAssignmentStatement node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTFunctionCallStatement node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTAssignmentFromMultiResultFunctionCall node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTBreakStatement node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTIfEquation node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTIfClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTThenClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTElseIfClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTElseClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTForEquation node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTEquationList node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTStatementList node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTForStatement node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTForIndices node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTForIndex node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTWhenEquation node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTWhenClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTElseWhenClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTWhenStatement node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTConnectClause node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTIfExpression node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTSimpleExpression node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTLogicalExpression node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTLogicalTerm node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTRelation node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTRelOp node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTArithmeticExpression node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTAddOp node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTTerm node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTMulOp node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTFactor node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTFalseLiteral node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTTrueLiteral node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTFunctionInvocation node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTListOfExpressionLists node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTNumberLiteral node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTStringLiteral node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTName node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTSimpleName node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTSubscriptedName node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTComponentReference node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTFunctionCallArgs node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTFunctionArguments node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTNamedArguments node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTNamedArgument node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTFunctionArgument node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTOutputExpressionList node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTExpressionList node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTArraySubscripts node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTSubscript node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTColonSubsript node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTComment node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTStringComment node, Object data) {
        return visit((ModelicaNode) node, data);
    }

    @Override
    public Object visit(ASTAnnotation node, Object data) {
        return visit((ModelicaNode) node, data);
    }
}
