/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

public class ModelicaParserVisitorAdapter implements ModelicaParserVisitor {

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
