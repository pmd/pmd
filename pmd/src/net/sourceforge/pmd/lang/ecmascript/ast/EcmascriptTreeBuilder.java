/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import net.sourceforge.pmd.lang.ast.Node;

import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.BreakStatement;
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.ContinueStatement;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ElementGet;
import org.mozilla.javascript.ast.EmptyExpression;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.ParenthesizedExpression;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.RegExpLiteral;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.SwitchCase;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.ThrowStatement;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.UnaryExpression;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.mozilla.javascript.ast.WhileLoop;

public class EcmascriptTreeBuilder implements NodeVisitor {

    protected static final Map<Class<? extends AstNode>, Constructor<? extends EcmascriptNode>> nodeTypeToNodeAdapterType = new HashMap<Class<? extends AstNode>, Constructor<? extends EcmascriptNode>>();
    static {
	register(ArrayLiteral.class, ASTArrayLiteral.class);
	register(Assignment.class, ASTAssignment.class);
	register(AstRoot.class, ASTAstRoot.class);
	register(Block.class, ASTBlock.class);
	register(BreakStatement.class, ASTBreakStatement.class);
	register(CatchClause.class, ASTCatchClause.class);
	register(ConditionalExpression.class, ASTConditionalExpression.class);
	register(ContinueStatement.class, ASTContinueStatement.class);
	register(DoLoop.class, ASTDoLoop.class);
	register(ElementGet.class, ASTElementGet.class);
	register(EmptyExpression.class, ASTEmptyExpression.class);
	register(ExpressionStatement.class, ASTExpressionStatement.class);
	register(ForInLoop.class, ASTForInLoop.class);
	register(ForLoop.class, ASTForLoop.class);
	register(FunctionCall.class, ASTFunctionCall.class);
	register(FunctionNode.class, ASTFunctionNode.class);
	register(IfStatement.class, ASTIfStatement.class);
	register(InfixExpression.class, ASTInfixExpression.class);
	register(KeywordLiteral.class, ASTKeywordLiteral.class);
	register(Name.class, ASTName.class);
	register(NewExpression.class, ASTNewExpression.class);
	register(NumberLiteral.class, ASTNumberLiteral.class);
	register(ObjectLiteral.class, ASTObjectLiteral.class);
	register(ObjectProperty.class, ASTObjectProperty.class);
	register(ParenthesizedExpression.class, ASTParenthesizedExpression.class);
	register(PropertyGet.class, ASTPropertyGet.class);
	register(RegExpLiteral.class, ASTRegExpLiteral.class);
	register(ReturnStatement.class, ASTReturnStatement.class);
	register(Scope.class, ASTScope.class);
	register(StringLiteral.class, ASTStringLiteral.class);
	register(SwitchCase.class, ASTSwitchCase.class);
	register(SwitchStatement.class, ASTSwitchStatement.class);
	register(ThrowStatement.class, ASTThrowStatement.class);
	register(TryStatement.class, ASTTryStatement.class);
	register(UnaryExpression.class, ASTUnaryExpression.class);
	register(VariableDeclaration.class, ASTVariableDeclaration.class);
	register(VariableInitializer.class, ASTVariableInitializer.class);
	register(WhileLoop.class, ASTWhileLoop.class);
    }

    protected static void register(Class<? extends AstNode> nodeType, Class<? extends EcmascriptNode> nodeAdapterType) {
	try {
	    nodeTypeToNodeAdapterType.put(nodeType, nodeAdapterType.getConstructor(nodeType));
	} catch (SecurityException e) {
	    throw new RuntimeException(e);
	} catch (NoSuchMethodException e) {
	    throw new RuntimeException(e);
	}
    }

    // The nodes having children built.
    protected Stack<Node> nodes = new Stack<Node>();

    // The Rhino nodes with children to build.
    protected Stack<AstNode> parents = new Stack<AstNode>();

    protected EcmascriptNode createNodeAdapter(AstNode node) {
	try {
	    Constructor<? extends EcmascriptNode> constructor = nodeTypeToNodeAdapterType.get(node.getClass());
	    if (constructor == null) {
		throw new IllegalArgumentException("There is no Node adapter class registered for the Node class: "
			+ node.getClass());
	    }
	    return constructor.newInstance(node);
	} catch (InstantiationException e) {
	    throw new RuntimeException(e);
	} catch (IllegalAccessException e) {
	    throw new RuntimeException(e);
	} catch (InvocationTargetException e) {
	    throw new RuntimeException(e.getTargetException());
	}
    }

    public EcmascriptNode build(AstNode astNode) {
	// Create a Node
	EcmascriptNode node = createNodeAdapter(astNode);

	// Append to parent
	Node parent = nodes.isEmpty() ? null : nodes.peek();
	if (parent != null) {
	    parent.jjtAddChild(node, parent.jjtGetNumChildren());
	    node.jjtSetParent(parent);
	}

	// Build the children...
	nodes.push(node);
	parents.push(astNode);
	astNode.visit(this);
	nodes.pop();
	parents.pop();

	return node;
    }

    public boolean visit(AstNode node) {
	if (parents.peek() == node) {
	    return true;
	} else {
	    build(node);
	    return false;
	}
    }
}
