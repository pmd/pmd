/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule;

import java.util.List;
import java.util.logging.Logger;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.PLSQLLanguageModule;
import net.sourceforge.pmd.lang.plsql.ast.ASTAccessibleByClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTAlterTrigger;
import net.sourceforge.pmd.lang.plsql.ast.ASTAlterTypeSpec;
import net.sourceforge.pmd.lang.plsql.ast.ASTAnalyticClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTArgument;
import net.sourceforge.pmd.lang.plsql.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.plsql.ast.ASTArguments;
import net.sourceforge.pmd.lang.plsql.ast.ASTAssignment;
import net.sourceforge.pmd.lang.plsql.ast.ASTAttribute;
import net.sourceforge.pmd.lang.plsql.ast.ASTAttributeDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTBetweenCondition;
import net.sourceforge.pmd.lang.plsql.ast.ASTBlock;
import net.sourceforge.pmd.lang.plsql.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.plsql.ast.ASTBulkCollectIntoClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTCallSpecTail;
import net.sourceforge.pmd.lang.plsql.ast.ASTCaseExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTCaseStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTCaseWhenClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTCloseStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTCollectionDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTCollectionName;
import net.sourceforge.pmd.lang.plsql.ast.ASTCollectionTypeDefinition;
import net.sourceforge.pmd.lang.plsql.ast.ASTCollectionTypeName;
import net.sourceforge.pmd.lang.plsql.ast.ASTColumn;
import net.sourceforge.pmd.lang.plsql.ast.ASTColumnAlias;
import net.sourceforge.pmd.lang.plsql.ast.ASTComment;
import net.sourceforge.pmd.lang.plsql.ast.ASTComparisonCondition;
import net.sourceforge.pmd.lang.plsql.ast.ASTCompilationDataType;
import net.sourceforge.pmd.lang.plsql.ast.ASTCompilationDeclarationFragment;
import net.sourceforge.pmd.lang.plsql.ast.ASTCompilationExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTCompoundCondition;
import net.sourceforge.pmd.lang.plsql.ast.ASTCompoundTriggerBlock;
import net.sourceforge.pmd.lang.plsql.ast.ASTCondition;
import net.sourceforge.pmd.lang.plsql.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTConditionalCompilationStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTConditionalInsertClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTCrossOuterApplyClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTCursorBody;
import net.sourceforge.pmd.lang.plsql.ast.ASTCursorForLoopStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTCursorSpecification;
import net.sourceforge.pmd.lang.plsql.ast.ASTCursorUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTDDLCommand;
import net.sourceforge.pmd.lang.plsql.ast.ASTDDLEvent;
import net.sourceforge.pmd.lang.plsql.ast.ASTDMLTableExpressionClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTDatabaseEvent;
import net.sourceforge.pmd.lang.plsql.ast.ASTDatabaseLink;
import net.sourceforge.pmd.lang.plsql.ast.ASTDatatype;
import net.sourceforge.pmd.lang.plsql.ast.ASTDatatypeDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTDateTimeLiteral;
import net.sourceforge.pmd.lang.plsql.ast.ASTDeclarativeSection;
import net.sourceforge.pmd.lang.plsql.ast.ASTDeclarativeUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTDeleteStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTDirectory;
import net.sourceforge.pmd.lang.plsql.ast.ASTElseClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTElsifClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTEmbeddedSqlStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTErrorLoggingClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTExceptionDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTExceptionHandler;
import net.sourceforge.pmd.lang.plsql.ast.ASTExistsCondition;
import net.sourceforge.pmd.lang.plsql.ast.ASTExitStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTExpressionList;
import net.sourceforge.pmd.lang.plsql.ast.ASTExpressionListMultiple;
import net.sourceforge.pmd.lang.plsql.ast.ASTExpressionListSingle;
import net.sourceforge.pmd.lang.plsql.ast.ASTExtractExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTFetchStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTFloatingPointCondition;
import net.sourceforge.pmd.lang.plsql.ast.ASTForAllIndex;
import net.sourceforge.pmd.lang.plsql.ast.ASTForAllStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTForIndex;
import net.sourceforge.pmd.lang.plsql.ast.ASTForStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTForUpdateClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.plsql.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.plsql.ast.ASTFromClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTFunctionCall;
import net.sourceforge.pmd.lang.plsql.ast.ASTFunctionName;
import net.sourceforge.pmd.lang.plsql.ast.ASTGlobal;
import net.sourceforge.pmd.lang.plsql.ast.ASTGotoStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTGroupByClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTGroupingExpressionList;
import net.sourceforge.pmd.lang.plsql.ast.ASTGroupingSetsClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTHierarchicalQueryClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTHostArrayName;
import net.sourceforge.pmd.lang.plsql.ast.ASTID;
import net.sourceforge.pmd.lang.plsql.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTInCondition;
import net.sourceforge.pmd.lang.plsql.ast.ASTInlineConstraint;
import net.sourceforge.pmd.lang.plsql.ast.ASTInlinePragma;
import net.sourceforge.pmd.lang.plsql.ast.ASTInnerCrossJoinClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
import net.sourceforge.pmd.lang.plsql.ast.ASTInsertIntoClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTInsertStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTIntoClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTIsASetCondition;
import net.sourceforge.pmd.lang.plsql.ast.ASTIsEmptyCondition;
import net.sourceforge.pmd.lang.plsql.ast.ASTIsNullCondition;
import net.sourceforge.pmd.lang.plsql.ast.ASTIsOfTypeCondition;
import net.sourceforge.pmd.lang.plsql.ast.ASTJavaInterfaceClass;
import net.sourceforge.pmd.lang.plsql.ast.ASTJoinClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTKEYWORD_UNRESERVED;
import net.sourceforge.pmd.lang.plsql.ast.ASTLabel;
import net.sourceforge.pmd.lang.plsql.ast.ASTLabelledStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTLikeCondition;
import net.sourceforge.pmd.lang.plsql.ast.ASTLikeExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTListaggOverflowClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTLiteral;
import net.sourceforge.pmd.lang.plsql.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTMemberCondition;
import net.sourceforge.pmd.lang.plsql.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.plsql.ast.ASTMultiSetCondition;
import net.sourceforge.pmd.lang.plsql.ast.ASTMultiTableInsert;
import net.sourceforge.pmd.lang.plsql.ast.ASTMultiplicativeExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTName;
import net.sourceforge.pmd.lang.plsql.ast.ASTNonDMLEvent;
import net.sourceforge.pmd.lang.plsql.ast.ASTNonDMLTrigger;
import net.sourceforge.pmd.lang.plsql.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.plsql.ast.ASTNumericLiteral;
import net.sourceforge.pmd.lang.plsql.ast.ASTObjectDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTObjectExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTObjectNameDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTOpenStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTOrderByClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTOutOfLineConstraint;
import net.sourceforge.pmd.lang.plsql.ast.ASTOuterJoinClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTOuterJoinExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTOuterJoinType;
import net.sourceforge.pmd.lang.plsql.ast.ASTPackageBody;
import net.sourceforge.pmd.lang.plsql.ast.ASTPackageSpecification;
import net.sourceforge.pmd.lang.plsql.ast.ASTParallelClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTPartitionExtensionClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTPipelineStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTPragma;
import net.sourceforge.pmd.lang.plsql.ast.ASTPragmaClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.plsql.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTQualifiedID;
import net.sourceforge.pmd.lang.plsql.ast.ASTQualifiedName;
import net.sourceforge.pmd.lang.plsql.ast.ASTQueryBlock;
import net.sourceforge.pmd.lang.plsql.ast.ASTQueryPartitionClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTRaiseStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTRead2NextOccurrence;
import net.sourceforge.pmd.lang.plsql.ast.ASTReadPastNextOccurrence;
import net.sourceforge.pmd.lang.plsql.ast.ASTReferencesClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTRegexpLikeCondition;
import net.sourceforge.pmd.lang.plsql.ast.ASTRelationalExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTReturningClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTRollupCubeClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTRowLimitingClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTScalarDataTypeName;
import net.sourceforge.pmd.lang.plsql.ast.ASTSchemaName;
import net.sourceforge.pmd.lang.plsql.ast.ASTSelectIntoStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTSelectList;
import net.sourceforge.pmd.lang.plsql.ast.ASTSelectStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTSimpleExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTSingleTableInsert;
import net.sourceforge.pmd.lang.plsql.ast.ASTSkip2NextOccurrence;
import net.sourceforge.pmd.lang.plsql.ast.ASTSkip2NextTerminator;
import net.sourceforge.pmd.lang.plsql.ast.ASTSkip2NextTokenOccurrence;
import net.sourceforge.pmd.lang.plsql.ast.ASTSkipPastNextOccurrence;
import net.sourceforge.pmd.lang.plsql.ast.ASTSkipPastNextTokenOccurrence;
import net.sourceforge.pmd.lang.plsql.ast.ASTSqlExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTSqlPlusCommand;
import net.sourceforge.pmd.lang.plsql.ast.ASTSqlStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTStringExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.plsql.ast.ASTSubTypeDefinition;
import net.sourceforge.pmd.lang.plsql.ast.ASTSubmultisetCondition;
import net.sourceforge.pmd.lang.plsql.ast.ASTSubqueryOperation;
import net.sourceforge.pmd.lang.plsql.ast.ASTSubqueryRestrictionClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTSynonym;
import net.sourceforge.pmd.lang.plsql.ast.ASTTable;
import net.sourceforge.pmd.lang.plsql.ast.ASTTableAlias;
import net.sourceforge.pmd.lang.plsql.ast.ASTTableCollectionExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTTableColumn;
import net.sourceforge.pmd.lang.plsql.ast.ASTTableName;
import net.sourceforge.pmd.lang.plsql.ast.ASTTableReference;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerTimingPointSection;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTrimExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeKeyword;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeMethod;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeSpecification;
import net.sourceforge.pmd.lang.plsql.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.plsql.ast.ASTUnaryExpressionNotPlusMinus;
import net.sourceforge.pmd.lang.plsql.ast.ASTUnlabelledStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTUnqualifiedID;
import net.sourceforge.pmd.lang.plsql.ast.ASTUpdateSetClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTUpdateStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTValuesClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTVariableName;
import net.sourceforge.pmd.lang.plsql.ast.ASTVariableOrConstantDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTVariableOrConstantDeclarator;
import net.sourceforge.pmd.lang.plsql.ast.ASTVariableOrConstantDeclaratorId;
import net.sourceforge.pmd.lang.plsql.ast.ASTVariableOrConstantInitializer;
import net.sourceforge.pmd.lang.plsql.ast.ASTView;
import net.sourceforge.pmd.lang.plsql.ast.ASTViewColumn;
import net.sourceforge.pmd.lang.plsql.ast.ASTWhereClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.plsql.ast.ASTWindowingClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTWithClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTWithinClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTWrappedObject;
import net.sourceforge.pmd.lang.plsql.ast.ASTXMLAttributesClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTXMLElement;
import net.sourceforge.pmd.lang.plsql.ast.ASTXMLExists;
import net.sourceforge.pmd.lang.plsql.ast.ASTXMLNamespacesClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTXMLPassingClause;
import net.sourceforge.pmd.lang.plsql.ast.ASTXMLTable;
import net.sourceforge.pmd.lang.plsql.ast.ASTXMLTableColum;
import net.sourceforge.pmd.lang.plsql.ast.ASTXMLTableOptions;
import net.sourceforge.pmd.lang.plsql.ast.ExecutableCode;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitor;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ImmutableLanguage;

public abstract class AbstractPLSQLRule extends AbstractRule implements PLSQLParserVisitor, ImmutableLanguage {
    private static final Logger LOGGER = Logger.getLogger(AbstractPLSQLRule.class.getName());
    private static final String CLASS_NAME = AbstractPLSQLRule.class.getName();

    public AbstractPLSQLRule() {
        super.setLanguage(LanguageRegistry.getLanguage(PLSQLLanguageModule.NAME));
        // Enable Type Resolution on PLSQL Rules by default
        super.setTypeResolution(true);
    }

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        visitAll(nodes, ctx);
    }

    protected void visitAll(List<? extends Node> nodes, RuleContext ctx) {
        LOGGER.entering(CLASS_NAME, "visitAll");
        for (Object element : nodes) {
            ASTInput node = (ASTInput) element;
            visit(node, ctx);
        }
        LOGGER.exiting(CLASS_NAME, "visitAll");
    }

    /**
     * Gets the Image of the first parent node of type
     * ASTClassOrInterfaceDeclaration or <code>null</code>
     *
     * @param node
     *            the node which will be searched
     */
    protected final String getDeclaringType(Node node) {
        Node c;

        /*
         * Choose the Object Type
         */
        c = node.getFirstParentOfType(ASTPackageSpecification.class);
        if (c != null) {
            return c.getImage();
        }

        c = node.getFirstParentOfType(ASTTypeSpecification.class);
        if (c != null) {
            return c.getImage();
        }

        c = node.getFirstParentOfType(ASTPackageBody.class);
        if (c != null) {
            return c.getImage();
        }

        c = node.getFirstParentOfType(ASTTriggerUnit.class);
        if (c != null) {
            return c.getImage();
        }

        // Finally Schema-level Methods
        c = node.getFirstParentOfType(ASTProgramUnit.class);
        if (c != null) {
            return c.getImage();
        }

        return null;
    }

    public static boolean isQualifiedName(Node node) {
        return node.getImage().indexOf('.') != -1;
    }

    public static boolean importsPackage(ASTInput node, String packageName) {
        return false;
    }

    /*
     * Duplicate PLSQLParserVisitor API
     */
    @Override
    public Object visit(PLSQLNode node, Object data) {
        for (PLSQLNode child : node.children()) {
            child.jjtAccept(this, data);
        }
        return null;
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
        LOGGER.entering(CLASS_NAME, "visit(ASTPackageSpecification)");
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTPackageBody node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTPackageBody)");
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
        LOGGER.entering(CLASS_NAME, "visit(ASTProgramUnit)");
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
    public Object visit(ASTCursorSpecification node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTCursorBody node, Object data) {
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
    public Object visit(ASTInlineConstraint node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTTableColumn node, Object data) {
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
        LOGGER.entering(CLASS_NAME, "visit(ASTTypeMethod)");
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTTypeSpecification node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTTypeSpecification)");
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
    public Object visit(ASTInlinePragma node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTTriggerUnit node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTTriggerUnit)");
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTTriggerTimingPointSection node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ASTTriggerTimingPointSection)");
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
    public Object visit(ASTIsOfTypeCondition node, Object data) {
        return visit((PLSQLNode) node, data);
    }

    @Override
    public Object visit(ASTIsNullCondition node, Object data) {
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

    /*
     * Treat all Executable Code
     */
    public Object visit(ExecutableCode node, Object data) {
        LOGGER.entering(CLASS_NAME, "visit(ExecutableCode)");
        return visit((PLSQLNode) node, data);
    }
}
