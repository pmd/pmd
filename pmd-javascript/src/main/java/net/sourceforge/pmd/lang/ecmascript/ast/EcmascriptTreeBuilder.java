/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.ast.ArrayComprehension;
import org.mozilla.javascript.ast.ArrayComprehensionLoop;
import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.BigIntLiteral;
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
import org.mozilla.javascript.ast.ErrorNode;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.GeneratorExpression;
import org.mozilla.javascript.ast.GeneratorExpressionLoop;
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
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.SwitchCase;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.TaggedTemplateLiteral;
import org.mozilla.javascript.ast.TemplateCharacters;
import org.mozilla.javascript.ast.TemplateLiteral;
import org.mozilla.javascript.ast.ThrowStatement;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.UnaryExpression;
import org.mozilla.javascript.ast.UpdateExpression;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.mozilla.javascript.ast.WhileLoop;
import org.mozilla.javascript.ast.WithStatement;
import org.mozilla.javascript.ast.XmlDotQuery;
import org.mozilla.javascript.ast.XmlElemRef;
import org.mozilla.javascript.ast.XmlExpression;
import org.mozilla.javascript.ast.XmlLiteral;
import org.mozilla.javascript.ast.XmlMemberGet;
import org.mozilla.javascript.ast.XmlPropRef;
import org.mozilla.javascript.ast.XmlString;
import org.mozilla.javascript.ast.Yield;

final class EcmascriptTreeBuilder implements NodeVisitor {

    private static final Map<Class<? extends AstNode>, Constructor<? extends AbstractEcmascriptNode<?>>> NODE_TYPE_TO_NODE_ADAPTER_TYPE = new HashMap<>();

    static {
        register(ArrayComprehension.class, ASTArrayComprehension.class);
        register(ArrayComprehensionLoop.class, ASTArrayComprehensionLoop.class);
        register(ArrayLiteral.class, ASTArrayLiteral.class);
        register(Assignment.class, ASTAssignment.class);
        register(AstRoot.class, ASTAstRoot.class);
        register(BigIntLiteral.class, ASTBigIntLiteral.class);
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
        register(ErrorNode.class, ASTErrorNode.class);
        register(ExpressionStatement.class, ASTExpressionStatement.class);
        register(ForInLoop.class, ASTForInLoop.class);
        register(ForLoop.class, ASTForLoop.class);
        register(FunctionCall.class, ASTFunctionCall.class);
        register(FunctionNode.class, ASTFunctionNode.class);
        register(GeneratorExpression.class, ASTGeneratorExpression.class);
        register(GeneratorExpressionLoop.class, ASTGeneratorExpressionLoop.class);
        register(IfStatement.class, ASTIfStatement.class);
        register(InfixExpression.class, ASTInfixExpression.class);
        // - not a real node - register(Jump.class, ASTJump.class);
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
        register(ScriptNode.class, ASTScriptNode.class);
        register(StringLiteral.class, ASTStringLiteral.class);
        register(SwitchCase.class, ASTSwitchCase.class);
        register(SwitchStatement.class, ASTSwitchStatement.class);
        register(TaggedTemplateLiteral.class, ASTTaggedTemplateLiteral.class);
        register(TemplateCharacters.class, ASTTemplateCharacters.class);
        register(TemplateLiteral.class, ASTTemplateLiteral.class);
        register(ThrowStatement.class, ASTThrowStatement.class);
        register(TryStatement.class, ASTTryStatement.class);
        register(UnaryExpression.class, ASTUnaryExpression.class);
        register(UpdateExpression.class, ASTUpdateExpression.class);
        register(VariableDeclaration.class, ASTVariableDeclaration.class);
        register(VariableInitializer.class, ASTVariableInitializer.class);
        register(WhileLoop.class, ASTWhileLoop.class);
        register(WithStatement.class, ASTWithStatement.class);
        register(XmlDotQuery.class, ASTXmlDotQuery.class);
        register(XmlElemRef.class, ASTXmlElemRef.class);
        register(XmlExpression.class, ASTXmlExpression.class);
        register(XmlMemberGet.class, ASTXmlMemberGet.class);
        register(XmlPropRef.class, ASTXmlPropRef.class);
        register(XmlString.class, ASTXmlString.class);
        register(XmlLiteral.class, ASTXmlLiteral.class);
        register(Yield.class, ASTYield.class);
    }

    private final List<ParseProblem> parseProblems;
    private final Map<ParseProblem, AbstractEcmascriptNode<?>> parseProblemToNode = new HashMap<>();

    // The nodes having children built.
    private final Deque<AbstractEcmascriptNode<?>> nodes = new ArrayDeque<>();

    // The Rhino nodes with children to build.
    private final Deque<AstNode> parents = new ArrayDeque<>();

    EcmascriptTreeBuilder(List<ParseProblem> parseProblems) {
        this.parseProblems = parseProblems;
    }

    private static <T extends AstNode> void register(Class<T> nodeType,
            Class<? extends AbstractEcmascriptNode<T>> nodeAdapterType) {
        try {
            NODE_TYPE_TO_NODE_ADAPTER_TYPE.put(nodeType, nodeAdapterType.getDeclaredConstructor(nodeType));
        } catch (SecurityException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    static <T extends AstNode> AbstractEcmascriptNode<T> createNodeAdapter(T node) {
        try {
            // the register function makes sure only AbstractEcmascriptNode<T> can be
            // added, where T is "T extends AstNode".
            @SuppressWarnings("unchecked")
            Constructor<? extends AbstractEcmascriptNode<T>> constructor = (Constructor<? extends AbstractEcmascriptNode<T>>) NODE_TYPE_TO_NODE_ADAPTER_TYPE.get(node.getClass());
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

        // Set all the trailing comma nodes
        for (AbstractEcmascriptNode<?> trailingCommaNode : parseProblemToNode.values()) {
            trailingCommaNode.setTrailingCommaExists(true);
        }

        return node;
    }

    private <T extends AstNode> EcmascriptNode<T> buildInternal(T astNode) {
        // Create a Node
        AbstractEcmascriptNode<T> node = createNodeAdapter(astNode);

        // Append to parent
        AbstractEcmascriptNode<?> parent = nodes.isEmpty() ? null : nodes.peek();
        if (parent != null) {
            parent.addChild(node, parent.getNumChildren());
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
        if (node.equals(parents.peek())) {
            return true;
        } else {
            buildInternal(node);
            return false;
        }
    }

    private void handleParseProblems(AbstractEcmascriptNode<? extends AstNode> node) {
        if (node instanceof TrailingCommaNode) {
            int nodeStart = node.node.getAbsolutePosition();
            int nodeEnd = nodeStart + node.node.getLength() - 1;

            // This will fetch the localized message
            // See https://github.com/pmd/pmd/issues/384
            String trailingCommaLocalizedMessage = ScriptRuntime.getMessageById("msg.extra.trailing.comma");

            for (ParseProblem parseProblem : parseProblems) {

                // The node overlaps the comma (i.e. end of the problem)?
                int problemStart = parseProblem.getFileOffset();
                int commaPosition = problemStart + parseProblem.getLength() - 1;
                if (nodeStart <= commaPosition && commaPosition <= nodeEnd) {
                    if (trailingCommaLocalizedMessage.equals(parseProblem.getMessage())) {
                        // Report on the shortest code block containing the
                        // problem (i.e. inner most code in nested structures).
                        AbstractEcmascriptNode<?> currentNode = parseProblemToNode.get(parseProblem);
                        if (currentNode == null || node.node.getLength() < currentNode.node.getLength()) {
                            parseProblemToNode.put(parseProblem, node);
                        }
                    }
                }
            }
        }
    }
}
