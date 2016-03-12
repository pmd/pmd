/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import net.sourceforge.pmd.lang.ast.Node;

import org.mozilla.apex.ast.ArrayComprehension;
import org.mozilla.apex.ast.ArrayComprehensionLoop;
import org.mozilla.apex.ast.ArrayLiteral;
import org.mozilla.apex.ast.Assignment;
import org.mozilla.apex.ast.AstNode;
import org.mozilla.apex.ast.AstRoot;
import org.mozilla.apex.ast.Block;
import org.mozilla.apex.ast.BreakStatement;
import org.mozilla.apex.ast.CatchClause;
import org.mozilla.apex.ast.Comment;
import org.mozilla.apex.ast.ConditionalExpression;
import org.mozilla.apex.ast.ContinueStatement;
import org.mozilla.apex.ast.DoLoop;
import org.mozilla.apex.ast.ElementGet;
import org.mozilla.apex.ast.EmptyExpression;
import org.mozilla.apex.ast.EmptyStatement;
import org.mozilla.apex.ast.ExpressionStatement;
import org.mozilla.apex.ast.ForInLoop;
import org.mozilla.apex.ast.ForLoop;
import org.mozilla.apex.ast.FunctionCall;
import org.mozilla.apex.ast.FunctionNode;
import org.mozilla.apex.ast.IfStatement;
import org.mozilla.apex.ast.InfixExpression;
import org.mozilla.apex.ast.KeywordLiteral;
import org.mozilla.apex.ast.Label;
import org.mozilla.apex.ast.LabeledStatement;
import org.mozilla.apex.ast.LetNode;
import org.mozilla.apex.ast.Name;
import org.mozilla.apex.ast.NewExpression;
import org.mozilla.apex.ast.NodeVisitor;
import org.mozilla.apex.ast.NumberLiteral;
import org.mozilla.apex.ast.ObjectLiteral;
import org.mozilla.apex.ast.ObjectProperty;
import org.mozilla.apex.ast.ParenthesizedExpression;
import org.mozilla.apex.ast.ParseProblem;
import org.mozilla.apex.ast.PropertyGet;
import org.mozilla.apex.ast.RegExpLiteral;
import org.mozilla.apex.ast.ReturnStatement;
import org.mozilla.apex.ast.Scope;
import org.mozilla.apex.ast.StringLiteral;
import org.mozilla.apex.ast.SwitchCase;
import org.mozilla.apex.ast.SwitchStatement;
import org.mozilla.apex.ast.ThrowStatement;
import org.mozilla.apex.ast.TryStatement;
import org.mozilla.apex.ast.UnaryExpression;
import org.mozilla.apex.ast.VariableDeclaration;
import org.mozilla.apex.ast.VariableInitializer;
import org.mozilla.apex.ast.WhileLoop;
import org.mozilla.apex.ast.WithStatement;
import org.mozilla.apex.ast.XmlDotQuery;
import org.mozilla.apex.ast.XmlExpression;
import org.mozilla.apex.ast.XmlMemberGet;
import org.mozilla.apex.ast.XmlString;

public final class ApexTreeBuilder implements NodeVisitor {

    private static final Map<Class<? extends AstNode>, Constructor<? extends ApexNode<?>>> NODE_TYPE_TO_NODE_ADAPTER_TYPE = new HashMap<>();
    static {
	register(ArrayComprehension.class, ASTArrayComprehension.class);
	register(ArrayComprehensionLoop.class, ASTArrayComprehensionLoop.class);
	register(ArrayLiteral.class, ASTArrayLiteral.class);
	register(Assignment.class, ASTAssignment.class);
	register(AstRoot.class, ASTAstRoot.class);
	register(Block.class, ASTBlock.class);
	register(BreakStatement.class, ASTBreakStatement.class);
	register(CatchClause.class, ASTCatchClause.class);
	register(Comment.class, ASTComment.class);
	register(ConditionalExpression.class, ASTConditionalExpression.class);
	register(ContinueStatement.class, ASTContinueStatement.class);
	register(DoLoop.class, ASTDoLoop.class);
	register(ElementGet.class, ASTElementGet.class);
	register(EmptyExpression.class, ASTEmptyExpression.class);
	register(EmptyStatement.class, ASTEmptyStatement.class);
	register(ExpressionStatement.class, ASTExpressionStatement.class);
	register(ForInLoop.class, ASTForInLoop.class);
	register(ForLoop.class, ASTForLoop.class);
	register(FunctionCall.class, ASTFunctionCall.class);
	register(FunctionNode.class, ASTFunctionNode.class);
	register(IfStatement.class, ASTIfStatement.class);
	register(InfixExpression.class, ASTInfixExpression.class);
	register(KeywordLiteral.class, ASTKeywordLiteral.class);
	register(Label.class, ASTLabel.class);
	register(LabeledStatement.class, ASTLabeledStatement.class);
	register(LetNode.class, ASTLetNode.class);
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
	register(WithStatement.class, ASTWithStatement.class);
	register(XmlDotQuery.class, ASTXmlDotQuery.class);
	register(XmlExpression.class, ASTXmlExpression.class);
	register(XmlMemberGet.class, ASTXmlMemberGet.class);
	register(XmlString.class, ASTXmlString.class);
    }

    private static <T extends AstNode> void register(Class<T> nodeType, Class<? extends ApexNode<T>> nodeAdapterType) {
	try {
	    NODE_TYPE_TO_NODE_ADAPTER_TYPE.put(nodeType, nodeAdapterType.getConstructor(nodeType));
	} catch (SecurityException e) {
	    throw new RuntimeException(e);
	} catch (NoSuchMethodException e) {
	    throw new RuntimeException(e);
	}
    }

    private List<ParseProblem> parseProblems;
    private Map<ParseProblem, TrailingCommaNode> parseProblemToNode = new HashMap<>();

    // The nodes having children built.
    private Stack<Node> nodes = new Stack<>();

    // The Rhino nodes with children to build.
    private Stack<AstNode> parents = new Stack<>();

    private final SourceCodePositioner sourceCodePositioner;

    public ApexTreeBuilder(String sourceCode, List<ParseProblem> parseProblems) {
	this.sourceCodePositioner = new SourceCodePositioner(sourceCode);
	this.parseProblems = parseProblems;
    }

    static <T extends AstNode> ApexNode<T> createNodeAdapter(T node) {
	try {
	    @SuppressWarnings("unchecked") // the register function makes sure only ApexNode<T> can be added,
	    // where T is "T extends AstNode".
	    Constructor<? extends ApexNode<T>> constructor = (Constructor<? extends ApexNode<T>>) NODE_TYPE_TO_NODE_ADAPTER_TYPE.get(node.getClass());
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

    public <T extends AstNode> ApexNode<T> build(T astNode) {
	ApexNode<T> node = buildInternal(astNode);

	calculateLineNumbers(node);

	// Set all the trailing comma nodes
	for (TrailingCommaNode trailingCommaNode : parseProblemToNode.values()) {
	    trailingCommaNode.setTrailingComma(true);
	}

	return node;
    }

    private <T extends AstNode> ApexNode<T> buildInternal(T astNode) {
	// Create a Node
	ApexNode<T> node = createNodeAdapter(astNode);

	// Append to parent
	Node parent = nodes.isEmpty() ? null : nodes.peek();
	if (parent != null) {
	    parent.jjtAddChild(node, parent.jjtGetNumChildren());
	    node.jjtSetParent(parent);
	}
	
	handleParseProblems(node);

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
	    buildInternal(node);
	    return false;
	}
    }

    private void handleParseProblems(ApexNode<? extends AstNode> node) {
	if (node instanceof TrailingCommaNode) {
	    TrailingCommaNode trailingCommaNode = (TrailingCommaNode) node;
	    int nodeStart = node.getNode().getAbsolutePosition();
	    int nodeEnd = nodeStart + node.getNode().getLength() - 1;
	    for (ParseProblem parseProblem : parseProblems) {
		// The node overlaps the comma (i.e. end of the problem)?
		int problemStart = parseProblem.getFileOffset();
		int commaPosition = problemStart + parseProblem.getLength() - 1;
		if (nodeStart <= commaPosition && commaPosition <= nodeEnd) {
		    if ("Trailing comma is not legal in an ECMA-262 object initializer".equals(parseProblem.getMessage())) {
			// Report on the shortest code block containing the
			// problem (i.e. inner most code in nested structures).
			ApexNode<?> currentNode = (ApexNode<?>) parseProblemToNode.get(parseProblem);
			if (currentNode == null || node.getNode().getLength() < currentNode.getNode().getLength()) {
			    parseProblemToNode.put(parseProblem, trailingCommaNode);
			}
		    }
		}
	    }
	}
    }

    private void calculateLineNumbers(ApexNode<?> node) {
	ApexParserVisitorAdapter visitor = new ApexParserVisitorAdapter() {
	    @Override
	    public Object visit(ApexNode<?> node, Object data) {
	        ((AbstractApexNode<?>)node).calculateLineNumbers(sourceCodePositioner);
	        return super.visit(node, data); // also visit the children
	    }
	};
	node.jjtAccept(visitor, null);
    }
}
