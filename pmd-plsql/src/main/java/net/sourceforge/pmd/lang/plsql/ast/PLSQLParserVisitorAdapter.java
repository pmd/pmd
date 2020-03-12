/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

public class PLSQLParserVisitorAdapter implements PLSQLParserVisitor {

    @Override
    public Object visit(PLSQLNode node, Object data) {
        return node.childrenAccept(this, data);
    }

    @Override
    public Object visit(ASTInput node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTDDLCommand node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTSqlPlusCommand node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTGlobal node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTBlock node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTPackageSpecification node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTPackageBody node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTDeclarativeUnit node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTDeclarativeSection node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTCompilationDeclarationFragment node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTProgramUnit node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTObjectNameDeclaration node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTFormalParameter node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTMethodDeclarator node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTFormalParameters node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTVariableOrConstantDeclarator node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTVariableOrConstantDeclaratorId node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTVariableOrConstantInitializer node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTDatatype node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTCompilationDataType node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTCollectionTypeName node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTScalarDataTypeName node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTDateTimeLiteral node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTExceptionHandler node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTSkip2NextTerminator node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTSkip2NextOccurrence node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTSkipPastNextOccurrence node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTSkip2NextTokenOccurrence node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTSkipPastNextTokenOccurrence node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTRead2NextOccurrence node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTReadPastNextOccurrence node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTSqlStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTWrappedObject node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTUnlabelledStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTLabelledStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTCaseStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTCaseWhenClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTElseClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTElsifClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTLoopStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTForStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTForIndex node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTForAllIndex node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTForAllStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTGotoStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTContinueStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTExitStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTRaiseStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTCloseStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTOpenStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTFetchStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTEmbeddedSqlStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTPipelineStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTConditionalCompilationStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTSubTypeDefinition node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTCollectionTypeDefinition node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTCollectionDeclaration node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTObjectDeclaration node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTCallSpecTail node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTCursorUnit node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTExpression node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTCompilationExpression node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTAssignment node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTCaseExpression node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTLikeExpression node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTTrimExpression node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTObjectExpression node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTConditionalOrExpression node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTConditionalAndExpression node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTEqualityExpression node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTRelationalExpression node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTAdditiveExpression node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTStringExpression node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTMultiplicativeExpression node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTUnaryExpression node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTExtractExpression node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTUnaryExpressionNotPlusMinus node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTPrimaryExpression node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTPrimaryPrefix node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTPrimarySuffix node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTLiteral node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTStringLiteral node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTBooleanLiteral node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTNullLiteral node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTMultiSetCondition node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTNumericLiteral node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTLabel node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTName node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTQualifiedName node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTArguments node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTArgumentList node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTArgument node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTVariableOrConstantDeclaration node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTDatatypeDeclaration node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTPragma node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTInlinePragma node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTExceptionDeclaration node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTParallelClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTTable node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTTableColumn node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTInlineConstraint node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTView node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTSynonym node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTDirectory node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTDatabaseLink node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTViewColumn node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTComment node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTTypeMethod node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTTypeSpecification node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTAlterTypeSpec node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTAttributeDeclaration node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTAttribute node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTPragmaClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTTriggerUnit node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTTriggerTimingPointSection node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTCompoundTriggerBlock node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTNonDMLTrigger node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTDDLEvent node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTDatabaseEvent node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTNonDMLEvent node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTAlterTrigger node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTKEYWORD_UNRESERVED node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTID node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTUnqualifiedID node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTQualifiedID node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTTypeKeyword node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTJavaInterfaceClass node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTAccessibleByClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTIsNullCondition node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTIsOfTypeCondition node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override

    public Object visit(ASTOutOfLineConstraint node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTSelectIntoStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTReferencesClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTCrossOuterApplyClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTCursorForLoopStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTInnerCrossJoinClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTJoinClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTOuterJoinClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTOuterJoinType node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTTableReference node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTSelectList node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTBulkCollectIntoClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTIntoClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTColumnAlias node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTTableAlias node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTCollectionName node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTHostArrayName node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTQueryBlock node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTSchemaName node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTTableName node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTComparisonCondition node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTCondition node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTExpressionList node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTWhereClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTSqlExpression node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTInCondition node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTExpressionListMultiple node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTExpressionListSingle node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTCompoundCondition node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTColumn node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTOrderByClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTRowLimitingClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTVariableName node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTFromClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTSubqueryOperation node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTQueryPartitionClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTGroupByClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTGroupingExpressionList node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTGroupingSetsClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTRollupCubeClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTSelectStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTLikeCondition node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTBetweenCondition node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTFloatingPointCondition node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTFunctionCall node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTUpdateStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTUpdateSetClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTDeleteStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTSubqueryRestrictionClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTTableCollectionExpression node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTDMLTableExpressionClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTConditionalInsertClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTInsertIntoClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTInsertStatement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTMultiTableInsert node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTSingleTableInsert node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTValuesClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTExistsCondition node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTHierarchicalQueryClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTIsASetCondition node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTIsEmptyCondition node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTMemberCondition node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTSubmultisetCondition node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTRegexpLikeCondition node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTFunctionName node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTAnalyticClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTWindowingClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTWithinClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTListaggOverflowClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTPartitionExtensionClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTReturningClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTErrorLoggingClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTSimpleExpression node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTXMLTable node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTXMLNamespacesClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTXMLTableOptions node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTXMLPassingClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTXMLTableColum node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTXMLExists node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTXMLAttributesClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTXMLElement node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTOuterJoinExpression node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTForUpdateClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTWithClause node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTCursorSpecification node, Object data) {
        return visit((PLSQLNode) node, data);
    }
}
