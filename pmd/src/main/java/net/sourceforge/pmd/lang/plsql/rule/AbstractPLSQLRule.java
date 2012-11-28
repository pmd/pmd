package net.sourceforge.pmd.lang.plsql.rule;

import java.util.List;
import java.util.logging.Logger;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.*;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ImmutableLanguage;

public abstract class AbstractPLSQLRule extends AbstractRule implements PLSQLParserVisitor, ImmutableLanguage
{
    private final static Logger LOGGER = Logger.getLogger(AbstractPLSQLRule.class.getPackage().getName()); 
    private final static String CLASS_PATH = AbstractPLSQLRule.class.getName(); 

  
    public AbstractPLSQLRule() {
	super.setLanguage(Language.PLSQL);
	// Enable Type Resolution on PLSQL Rules by default
	super.setUsesTypeResolution();
    }

    public void apply(List<? extends Node> nodes, RuleContext ctx) {
	visitAll(nodes, ctx);
    }

    protected void visitAll(List<? extends Node> nodes, RuleContext ctx) {
        LOGGER.entering(CLASS_PATH,"visitAll");
	for (Object element : nodes) {
	    ASTInput node = (ASTInput) element;
	    visit(node, ctx);
	}
        LOGGER.exiting(CLASS_PATH,"visitAll");
    }

    /**
     * Gets the Image of the first parent node of type
     * ASTClassOrInterfaceDeclaration or <code>null</code>
     *
     * @param node
     *            the node which will be searched
     */
    protected final String getDeclaringType(Node node) {
	SimpleNode c;  

        /*
         * Choose the Object Type
         */
	if (null != (c = node.getFirstParentOfType(ASTPackageSpecification.class))) {
	    return c.getImage();
	}
        else 
	if (null != (c = node.getFirstParentOfType(ASTTypeSpecification.class))) {
	    return c.getImage();
	}
        else 
	if (null != (c = node.getFirstParentOfType(ASTPackageBody.class))) {
	    return c.getImage();
	}
        else 
	if (null != (c = node.getFirstParentOfType(ASTTriggerUnit.class))) {
	    return c.getImage();
	}
        else //Finally Schema-level Methods 
	if (null != (c = node.getFirstParentOfType(ASTProgramUnit.class))) {
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
  public Object visit(SimpleNode node, Object data) { 
	node.childrenAccept(this, data);
        return null;
    }

  
  public Object visit(ASTDatabaseLink node, Object data) { 
        return visit((SimpleNode) node, data); 
    }

  public Object visit(ASTDirectory node, Object data) { 
        return visit((SimpleNode) node, data); 
    }

  public Object visit(ASTSynonym node, Object data) { 
        return visit((SimpleNode) node, data); 
    }

  public Object visit(ASTBlock node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTForAllIndex node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTForIndex node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTCaseStatement node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTCaseWhenClause node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTCloseStatement node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTConditionalCompilationStatement node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTElseClause node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTElsifClause node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTEmbeddedSqlStatement node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTExitStatement node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTFetchStatement node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTForAllStatement node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTForStatement node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTGotoStatement node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTIfStatement node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTLabel node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTLabelledStatement node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTLoopStatement node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTOpenStatement node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTPipelineStatement node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTRaiseStatement node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTReturnStatement node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTUnlabelledStatement node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTWhileStatement node, Object data) { 
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTInput node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTSqlPlusCommand node, Object data) {
        return visit((SimpleNode) node, data); 
    }

  public Object visit(ASTDDLCommand node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTGlobal node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTGlobalBlock node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTPackageSpecification node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ASTPackageSpecification)");
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTPackageBody node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ASTPackageBody)");
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTDeclarativeUnit node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTDeclarativeSection node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTCompilationDeclarationFragment node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTProgramUnit node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ASTProgramUnit)");
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTObjectNameDeclaration node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTFormalParameter node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTMethodDeclaration node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTMethodDeclarator node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTFormalParameters node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTVariableOrConstantDeclarator node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTVariableOrConstantDeclaratorId node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTVariableOrConstantInitializer node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTDatatype node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTCompilationDataType node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTCollectionTypeName node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTScalarDataTypeName node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTDateTimeLiteral node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTExceptionHandler node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTSkip2NextTerminator node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTSkip2NextOccurrence node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTSkipPastNextOccurrence node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTSkip2NextTokenOccurrence node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTSkipPastNextTokenOccurrence node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTRead2NextOccurrence node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTReadPastNextOccurrence node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTSqlStatement node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTWrappedObject node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTStatement node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTSubTypeDefinition node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTFieldDeclaration node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTCollectionTypeDefinition node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTCollectionDeclaration node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTObjectDeclaration node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTCallSpecTail node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTCursorUnit node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTCursorSpecification node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTCursorBody node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTExpression node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTCompilationExpression node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTAssignment node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTCaseExpression node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTLikeExpression node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTTrimExpression node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTObjectExpression node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTConditionalOrExpression node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTConditionalAndExpression node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTContinueStatement node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTEqualityExpression node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTRelationalExpression node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTAdditiveExpression node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTStringExpression node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTMultiplicativeExpression node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTUnaryExpression node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTUnaryExpressionNotPlusMinus node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTPrimaryExpression node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTPrimaryPrefix node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTPrimarySuffix node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTLiteral node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTStringLiteral node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTBooleanLiteral node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTNullLiteral node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTMultiSetCondition node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTNumericLiteral node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTName node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTQualifiedName node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTArguments node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTArgumentList node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTArgument node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTVariableOrConstantDeclaration node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTDatatypeDeclaration node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTPragma node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTExceptionDeclaration node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTParallelClause node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTTable node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTTableColumn node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTView node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTViewColumn node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTComment node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTTypeMethod node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ASTTypeMethod)");
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTTypeSpecification node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ASTTypeSpecification)");
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTAlterTypeSpec node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTAttributeDeclaration node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTAttribute node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTPragmaClause node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTTriggerUnit node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ASTTriggerUnit)");
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTTriggerTimingPointSection node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ASTTriggerTimingPointSection)");
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTCompoundTriggerBlock node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTNonDMLTrigger node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTDDLEvent node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTDatabaseEvent node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTNonDMLEvent node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTAlterTrigger node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTKEYWORD_RESERVED node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTKEYWORD_UNRESERVED node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTID node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTUnqualifiedID node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTQualifiedID node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTTypeKeyword node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTJavaInterfaceClass node, Object data) {
        return visit((SimpleNode) node, data); 
    }


  public Object visit(ASTEqualsOldIDNewID node, Object data) {
        return visit((SimpleNode) node, data); 
    }

  /*
   * Treat all Executable Code 
   */
  public Object visit(ExecutableCode node, Object data) {
        LOGGER.entering(CLASS_PATH,"visit(ExecutableCode)");
        return visit((SimpleNode) node, data); 
    }


}



