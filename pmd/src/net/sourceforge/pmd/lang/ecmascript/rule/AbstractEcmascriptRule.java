/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptParserOptions;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTArrayComprehension;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTArrayComprehensionLoop;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTArrayLiteral;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTAssignment;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTAstRoot;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTBlock;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTComment;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTDoLoop;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTElementGet;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTEmptyExpression;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTForInLoop;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTForLoop;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTFunctionCall;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTFunctionNode;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTKeywordLiteral;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTLabel;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTLabeledStatement;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTLetNode;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTName;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTNewExpression;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTNumberLiteral;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTObjectLiteral;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTObjectProperty;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTParenthesizedExpression;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTPropertyGet;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTRegExpLiteral;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTScope;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTSwitchCase;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTWhileLoop;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTWithStatement;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTXmlDotQuery;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTXmlExpression;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTXmlMemberGet;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTXmlString;
import net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptNode;
import net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParserVisitor;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ImmutableLanguage;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;

public abstract class AbstractEcmascriptRule extends AbstractRule implements EcmascriptParserVisitor, ImmutableLanguage {

    private static final BooleanProperty RECORDING_COMMENTS_DESCRIPTOR = EcmascriptParserOptions.RECORDING_COMMENTS_DESCRIPTOR;
    private static final BooleanProperty RECORDING_LOCAL_JSDOC_COMMENTS_DESCRIPTOR = EcmascriptParserOptions.RECORDING_LOCAL_JSDOC_COMMENTS_DESCRIPTOR;
    private static final EnumeratedProperty<EcmascriptParserOptions.Version> RHINO_LANGUAGE_VERSION = EcmascriptParserOptions.RHINO_LANGUAGE_VERSION;

    public AbstractEcmascriptRule() {
	super.setLanguage(Language.ECMASCRIPT);
	definePropertyDescriptor(RECORDING_COMMENTS_DESCRIPTOR);
	definePropertyDescriptor(RECORDING_LOCAL_JSDOC_COMMENTS_DESCRIPTOR);
	definePropertyDescriptor(RHINO_LANGUAGE_VERSION);
    }

    @Override
    public ParserOptions getParserOptions() {
	return new EcmascriptParserOptions(this);
    }

    public void apply(List<? extends Node> nodes, RuleContext ctx) {
	visitAll(nodes, ctx);
    }

    protected void visitAll(List<? extends Node> nodes, RuleContext ctx) {
	for (Object element : nodes) {
	    ASTAstRoot node = (ASTAstRoot) element;
	    visit(node, ctx);
	}
    }

    //
    // The following APIs are identical to those in EcmascriptParserVisitorAdapter.
    // Due to Java single inheritance, it preferred to extend from the more
    // complex Rule base class instead of from relatively simple Visitor.
    //

    public Object visit(EcmascriptNode node, Object data) {
	node.childrenAccept(this, data);
	return null;
    }

    public Object visit(ASTArrayComprehension node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTArrayComprehensionLoop node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTArrayLiteral node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTAssignment node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTAstRoot node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTBlock node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTBreakStatement node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTCatchClause node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTComment node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTConditionalExpression node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTContinueStatement node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTDoLoop node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTElementGet node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTEmptyExpression node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTExpressionStatement node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTForInLoop node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTForLoop node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTFunctionCall node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTFunctionNode node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTIfStatement node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTInfixExpression node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTKeywordLiteral node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTLabel node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTLabeledStatement node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTLetNode node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTName node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTNewExpression node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTNumberLiteral node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTObjectLiteral node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTObjectProperty node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTParenthesizedExpression node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTPropertyGet node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTRegExpLiteral node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTReturnStatement node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTScope node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTStringLiteral node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTSwitchCase node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTSwitchStatement node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTThrowStatement node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTTryStatement node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTUnaryExpression node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTVariableDeclaration node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTVariableInitializer node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTWhileLoop node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTWithStatement node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTXmlDotQuery node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTXmlExpression node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTXmlMemberGet node, Object data) {
	return visit((EcmascriptNode) node, data);
    }

    public Object visit(ASTXmlString node, Object data) {
	return visit((EcmascriptNode) node, data);
    }
}
