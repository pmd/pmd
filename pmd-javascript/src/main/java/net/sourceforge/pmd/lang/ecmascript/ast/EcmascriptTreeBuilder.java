/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.mozilla.javascript.ast.ArrayComprehension;
import org.mozilla.javascript.ast.ArrayComprehensionLoop;
import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.BreakStatement;
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.Comment;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.ContinueStatement;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ElementGet;
import org.mozilla.javascript.ast.EmptyExpression;
import org.mozilla.javascript.ast.EmptyStatement;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.KeywordLiteral;
import org.mozilla.javascript.ast.Label;
import org.mozilla.javascript.ast.LabeledStatement;
import org.mozilla.javascript.ast.LetNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.ParenthesizedExpression;
import org.mozilla.javascript.ast.ParseProblem;
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
import org.mozilla.javascript.ast.WithStatement;
import org.mozilla.javascript.ast.XmlDotQuery;
import org.mozilla.javascript.ast.XmlExpression;
import org.mozilla.javascript.ast.XmlMemberGet;
import org.mozilla.javascript.ast.XmlString;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

public final class EcmascriptTreeBuilder implements NodeVisitor {

    private static final Map<Class<? extends AstNode>, Constructor<? extends EcmascriptNode<?>>> NODE_TYPE_TO_NODE_ADAPTER_TYPE = new HashMap<>();

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

    private List<ParseProblem> parseProblems;
    private Map<ParseProblem, TrailingCommaNode> parseProblemToNode = new HashMap<>();

    // The nodes having children built.
    private Stack<Node> nodes = new Stack<>();

    // The Rhino nodes with children to build.
    private Stack<AstNode> parents = new Stack<>();

    private final SourceCodePositioner sourceCodePositioner;

    public EcmascriptTreeBuilder(String sourceCode, List<ParseProblem> parseProblems) {
        this.sourceCodePositioner = new SourceCodePositioner(sourceCode);
        this.parseProblems = parseProblems;
    }

    private static <T extends AstNode> void register(Class<T> nodeType,
            Class<? extends EcmascriptNode<T>> nodeAdapterType) {
        try {
            NODE_TYPE_TO_NODE_ADAPTER_TYPE.put(nodeType, nodeAdapterType.getConstructor(nodeType));
        } catch (SecurityException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    static <T extends AstNode> EcmascriptNode<T> createNodeAdapter(T node) {
        try {
            // the register function makes sure only EcmascriptNode<T> can be
            // added, where T is "T extends AstNode".
            @SuppressWarnings("unchecked")
            Constructor<? extends EcmascriptNode<T>> constructor = (Constructor<? extends EcmascriptNode<T>>) NODE_TYPE_TO_NODE_ADAPTER_TYPE
                    .get(node.getClass());
            if (constructor == null) {
                throw new IllegalArgumentException(
                        "There is no Node adapter class registered for the Node class: " + node.getClass());
            }
            return constructor.newInstance(node);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        }
    }

    public <T extends AstNode> EcmascriptNode<T> build(T astNode) {
        EcmascriptNode<T> node = buildInternal(astNode);

        calculateLineNumbers(node);

        // Set all the trailing comma nodes
        for (TrailingCommaNode trailingCommaNode : parseProblemToNode.values()) {
            trailingCommaNode.setTrailingComma(true);
        }

        return node;
    }

    private <T extends AstNode> EcmascriptNode<T> buildInternal(T astNode) {
        // Create a Node
        EcmascriptNode<T> node = createNodeAdapter(astNode);

        // Append to parent
        Node parent = nodes.isEmpty() ? null : nodes.peek();
        if (parent != null) {
            parent.jjtAddChild(node, parent.getNumChildren());
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

    @Override
    public boolean visit(AstNode node) {
        if (parents.peek() == node) {
            return true;
        } else {
            buildInternal(node);
            return false;
        }
    }

    private void handleParseProblems(EcmascriptNode<? extends AstNode> node) {
        if (node instanceof TrailingCommaNode) {
            TrailingCommaNode trailingCommaNode = (TrailingCommaNode) node;
            int nodeStart = node.getNode().getAbsolutePosition();
            int nodeEnd = nodeStart + node.getNode().getLength() - 1;
            for (ParseProblem parseProblem : parseProblems) {
                // The node overlaps the comma (i.e. end of the problem)?
                int problemStart = parseProblem.getFileOffset();
                int commaPosition = problemStart + parseProblem.getLength() - 1;
                if (nodeStart <= commaPosition && commaPosition <= nodeEnd) {
                    if ("Trailing comma is not legal in an ECMA-262 object initializer"
                            .equals(parseProblem.getMessage())) {
                        // Report on the shortest code block containing the
                        // problem (i.e. inner most code in nested structures).
                        EcmascriptNode<?> currentNode = (EcmascriptNode<?>) parseProblemToNode.get(parseProblem);
                        if (currentNode == null || node.getNode().getLength() < currentNode.getNode().getLength()) {
                            parseProblemToNode.put(parseProblem, trailingCommaNode);
                        }
                    }
                }
            }
        }
    }

    private void calculateLineNumbers(EcmascriptNode<?> node) {
        EcmascriptParserVisitorAdapter visitor = new EcmascriptParserVisitorAdapter() {
            @Override
            public Object visit(EcmascriptNode<?> node, Object data) {
                ((AbstractEcmascriptNode<?>) node).calculateLineNumbers(sourceCodePositioner);
                return super.visit(node, data); // also visit the children
            }
        };
        node.jjtAccept(visitor, null);
    }
}
