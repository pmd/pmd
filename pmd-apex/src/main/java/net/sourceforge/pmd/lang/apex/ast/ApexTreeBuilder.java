/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import apex.jorje.data.Location;
import apex.jorje.data.Locations;
import apex.jorje.parser.impl.ApexLexer;
import apex.jorje.semantic.ast.AstNode;
import apex.jorje.semantic.ast.compilation.AnonymousClass;
import apex.jorje.semantic.ast.compilation.ConstructorPreamble;
import apex.jorje.semantic.ast.compilation.UserClass;
import apex.jorje.semantic.ast.compilation.UserClassMethods;
import apex.jorje.semantic.ast.compilation.UserEnum;
import apex.jorje.semantic.ast.compilation.UserExceptionMethods;
import apex.jorje.semantic.ast.compilation.UserInterface;
import apex.jorje.semantic.ast.compilation.UserTrigger;
import apex.jorje.semantic.ast.condition.StandardCondition;
import apex.jorje.semantic.ast.expression.ArrayLoadExpression;
import apex.jorje.semantic.ast.expression.ArrayStoreExpression;
import apex.jorje.semantic.ast.expression.AssignmentExpression;
import apex.jorje.semantic.ast.expression.BinaryExpression;
import apex.jorje.semantic.ast.expression.BindExpressions;
import apex.jorje.semantic.ast.expression.BooleanExpression;
import apex.jorje.semantic.ast.expression.CastExpression;
import apex.jorje.semantic.ast.expression.ClassRefExpression;
import apex.jorje.semantic.ast.expression.Expression;
import apex.jorje.semantic.ast.expression.IllegalStoreExpression;
import apex.jorje.semantic.ast.expression.InstanceOfExpression;
import apex.jorje.semantic.ast.expression.JavaMethodCallExpression;
import apex.jorje.semantic.ast.expression.JavaVariableExpression;
import apex.jorje.semantic.ast.expression.LiteralExpression;
import apex.jorje.semantic.ast.expression.MapEntryNode;
import apex.jorje.semantic.ast.expression.MethodCallExpression;
import apex.jorje.semantic.ast.expression.NestedExpression;
import apex.jorje.semantic.ast.expression.NestedStoreExpression;
import apex.jorje.semantic.ast.expression.NewKeyValueObjectExpression;
import apex.jorje.semantic.ast.expression.NewListInitExpression;
import apex.jorje.semantic.ast.expression.NewListLiteralExpression;
import apex.jorje.semantic.ast.expression.NewMapInitExpression;
import apex.jorje.semantic.ast.expression.NewMapLiteralExpression;
import apex.jorje.semantic.ast.expression.NewObjectExpression;
import apex.jorje.semantic.ast.expression.NewSetInitExpression;
import apex.jorje.semantic.ast.expression.NewSetLiteralExpression;
import apex.jorje.semantic.ast.expression.PackageVersionExpression;
import apex.jorje.semantic.ast.expression.PostfixExpression;
import apex.jorje.semantic.ast.expression.PrefixExpression;
import apex.jorje.semantic.ast.expression.ReferenceExpression;
import apex.jorje.semantic.ast.expression.SoqlExpression;
import apex.jorje.semantic.ast.expression.SoslExpression;
import apex.jorje.semantic.ast.expression.SuperMethodCallExpression;
import apex.jorje.semantic.ast.expression.SuperVariableExpression;
import apex.jorje.semantic.ast.expression.TernaryExpression;
import apex.jorje.semantic.ast.expression.ThisMethodCallExpression;
import apex.jorje.semantic.ast.expression.ThisVariableExpression;
import apex.jorje.semantic.ast.expression.TriggerVariableExpression;
import apex.jorje.semantic.ast.expression.VariableExpression;
import apex.jorje.semantic.ast.member.Field;
import apex.jorje.semantic.ast.member.Method;
import apex.jorje.semantic.ast.member.Parameter;
import apex.jorje.semantic.ast.member.Property;
import apex.jorje.semantic.ast.member.bridge.BridgeMethodCreator;
import apex.jorje.semantic.ast.modifier.Annotation;
import apex.jorje.semantic.ast.modifier.AnnotationParameter;
import apex.jorje.semantic.ast.modifier.Modifier;
import apex.jorje.semantic.ast.modifier.ModifierNode;
import apex.jorje.semantic.ast.modifier.ModifierOrAnnotation;
import apex.jorje.semantic.ast.statement.BlockStatement;
import apex.jorje.semantic.ast.statement.BreakStatement;
import apex.jorje.semantic.ast.statement.CatchBlockStatement;
import apex.jorje.semantic.ast.statement.ConstructorPreambleStatement;
import apex.jorje.semantic.ast.statement.ContinueStatement;
import apex.jorje.semantic.ast.statement.DmlDeleteStatement;
import apex.jorje.semantic.ast.statement.DmlInsertStatement;
import apex.jorje.semantic.ast.statement.DmlMergeStatement;
import apex.jorje.semantic.ast.statement.DmlUndeleteStatement;
import apex.jorje.semantic.ast.statement.DmlUpdateStatement;
import apex.jorje.semantic.ast.statement.DmlUpsertStatement;
import apex.jorje.semantic.ast.statement.DoLoopStatement;
import apex.jorje.semantic.ast.statement.ExpressionStatement;
import apex.jorje.semantic.ast.statement.FieldDeclaration;
import apex.jorje.semantic.ast.statement.FieldDeclarationStatements;
import apex.jorje.semantic.ast.statement.ForEachStatement;
import apex.jorje.semantic.ast.statement.ForLoopStatement;
import apex.jorje.semantic.ast.statement.IfBlockStatement;
import apex.jorje.semantic.ast.statement.IfElseBlockStatement;
import apex.jorje.semantic.ast.statement.MethodBlockStatement;
import apex.jorje.semantic.ast.statement.MultiStatement;
import apex.jorje.semantic.ast.statement.ReturnStatement;
import apex.jorje.semantic.ast.statement.RunAsBlockStatement;
import apex.jorje.semantic.ast.statement.Statement;
import apex.jorje.semantic.ast.statement.StatementExecuted;
import apex.jorje.semantic.ast.statement.ThrowStatement;
import apex.jorje.semantic.ast.statement.TryCatchFinallyBlockStatement;
import apex.jorje.semantic.ast.statement.VariableDeclaration;
import apex.jorje.semantic.ast.statement.VariableDeclarationStatements;
import apex.jorje.semantic.ast.statement.WhileLoopStatement;
import apex.jorje.semantic.ast.visitor.AdditionalPassScope;
import apex.jorje.semantic.ast.visitor.AstVisitor;
import apex.jorje.semantic.exception.Errors;

public final class ApexTreeBuilder extends AstVisitor<AdditionalPassScope> {

    private static final Map<Class<? extends AstNode>, Constructor<? extends AbstractApexNode<?>>> NODE_TYPE_TO_NODE_ADAPTER_TYPE = new HashMap<>();

    static {
        register(Annotation.class, ASTAnnotation.class);
        register(AnnotationParameter.class, ASTAnnotationParameter.class);
        register(AnonymousClass.class, ASTAnonymousClass.class);
        register(ArrayLoadExpression.class, ASTArrayLoadExpression.class);
        register(ArrayStoreExpression.class, ASTArrayStoreExpression.class);
        register(AssignmentExpression.class, ASTAssignmentExpression.class);
        register(BinaryExpression.class, ASTBinaryExpression.class);
        register(BindExpressions.class, ASTBindExpressions.class);
        register(BlockStatement.class, ASTBlockStatement.class);
        register(BooleanExpression.class, ASTBooleanExpression.class);
        register(BreakStatement.class, ASTBreakStatement.class);
        register(BridgeMethodCreator.class, ASTBridgeMethodCreator.class);
        register(CastExpression.class, ASTCastExpression.class);
        register(CatchBlockStatement.class, ASTCatchBlockStatement.class);
        register(ClassRefExpression.class, ASTClassRefExpression.class);
        register(ConstructorPreamble.class, ASTConstructorPreamble.class);
        register(ConstructorPreambleStatement.class, ASTConstructorPreambleStatement.class);
        register(ContinueStatement.class, ASTContinueStatement.class);
        register(DmlDeleteStatement.class, ASTDmlDeleteStatement.class);
        register(DmlInsertStatement.class, ASTDmlInsertStatement.class);
        register(DmlMergeStatement.class, ASTDmlMergeStatement.class);
        register(DmlUndeleteStatement.class, ASTDmlUndeleteStatement.class);
        register(DmlUpdateStatement.class, ASTDmlUpdateStatement.class);
        register(DmlUpsertStatement.class, ASTDmlUpsertStatement.class);
        register(DoLoopStatement.class, ASTDoLoopStatement.class);
        register(Expression.class, ASTExpression.class);
        register(ExpressionStatement.class, ASTExpressionStatement.class);
        register(Field.class, ASTField.class);
        register(FieldDeclaration.class, ASTFieldDeclaration.class);
        register(FieldDeclarationStatements.class, ASTFieldDeclarationStatements.class);
        register(ForEachStatement.class, ASTForEachStatement.class);
        register(ForLoopStatement.class, ASTForLoopStatement.class);
        register(IfBlockStatement.class, ASTIfBlockStatement.class);
        register(IfElseBlockStatement.class, ASTIfElseBlockStatement.class);
        register(IllegalStoreExpression.class, ASTIllegalStoreExpression.class);
        register(InstanceOfExpression.class, ASTInstanceOfExpression.class);
        register(JavaMethodCallExpression.class, ASTJavaMethodCallExpression.class);
        register(JavaVariableExpression.class, ASTJavaVariableExpression.class);
        register(LiteralExpression.class, ASTLiteralExpression.class);
        register(MapEntryNode.class, ASTMapEntryNode.class);
        register(Method.class, ASTMethod.class);
        register(MethodBlockStatement.class, ASTMethodBlockStatement.class);
        register(MethodCallExpression.class, ASTMethodCallExpression.class);
        register(Modifier.class, ASTModifier.class);
        register(ModifierNode.class, ASTModifierNode.class);
        register(ModifierOrAnnotation.class, ASTModifierOrAnnotation.class);
        register(MultiStatement.class, ASTMultiStatement.class);
        register(NestedExpression.class, ASTNestedExpression.class);
        register(NestedStoreExpression.class, ASTNestedStoreExpression.class);
        register(NewKeyValueObjectExpression.class, ASTNewKeyValueObjectExpression.class);
        register(NewListInitExpression.class, ASTNewListInitExpression.class);
        register(NewListLiteralExpression.class, ASTNewListLiteralExpression.class);
        register(NewMapInitExpression.class, ASTNewMapInitExpression.class);
        register(NewMapLiteralExpression.class, ASTNewMapLiteralExpression.class);
        register(NewObjectExpression.class, ASTNewObjectExpression.class);
        register(NewSetInitExpression.class, ASTNewSetInitExpression.class);
        register(NewSetLiteralExpression.class, ASTNewSetLiteralExpression.class);
        register(PackageVersionExpression.class, ASTPackageVersionExpression.class);
        register(Parameter.class, ASTParameter.class);
        register(PostfixExpression.class, ASTPostfixExpression.class);
        register(PrefixExpression.class, ASTPrefixExpression.class);
        register(Property.class, ASTProperty.class);
        register(ReferenceExpression.class, ASTReferenceExpression.class);
        register(ReturnStatement.class, ASTReturnStatement.class);
        register(RunAsBlockStatement.class, ASTRunAsBlockStatement.class);
        register(SoqlExpression.class, ASTSoqlExpression.class);
        register(SoslExpression.class, ASTSoslExpression.class);
        register(StandardCondition.class, ASTStandardCondition.class);
        register(Statement.class, ASTStatement.class);
        register(StatementExecuted.class, ASTStatementExecuted.class);
        register(SuperMethodCallExpression.class, ASTSuperMethodCallExpression.class);
        register(SuperVariableExpression.class, ASTSuperVariableExpression.class);
        register(TernaryExpression.class, ASTTernaryExpression.class);
        register(ThisMethodCallExpression.class, ASTThisMethodCallExpression.class);
        register(ThisVariableExpression.class, ASTThisVariableExpression.class);
        register(ThrowStatement.class, ASTThrowStatement.class);
        register(TriggerVariableExpression.class, ASTTriggerVariableExpression.class);
        register(TryCatchFinallyBlockStatement.class, ASTTryCatchFinallyBlockStatement.class);
        register(UserClass.class, ASTUserClass.class);
        register(UserClassMethods.class, ASTUserClassMethods.class);
        register(UserExceptionMethods.class, ASTUserExceptionMethods.class);
        register(UserEnum.class, ASTUserEnum.class);
        register(UserInterface.class, ASTUserInterface.class);
        register(UserTrigger.class, ASTUserTrigger.class);
        register(VariableDeclaration.class, ASTVariableDeclaration.class);
        register(VariableDeclarationStatements.class, ASTVariableDeclarationStatements.class);
        register(VariableExpression.class, ASTVariableExpression.class);
        register(WhileLoopStatement.class, ASTWhileLoopStatement.class);
    }

    private static <T extends AstNode> void register(Class<T> nodeType, Class<? extends AbstractApexNode<T>> nodeAdapterType) {
        try {
            NODE_TYPE_TO_NODE_ADAPTER_TYPE.put(nodeType, nodeAdapterType.getConstructor(nodeType));
        } catch (SecurityException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    // The nodes having children built.
    private Stack<Node> nodes = new Stack<>();

    // The Apex nodes with children to build.
    private Stack<AstNode> parents = new Stack<>();

    private AdditionalPassScope scope = new AdditionalPassScope(Errors.createErrors());

    private final SourceCodePositioner sourceCodePositioner;
    private final String sourceCode;
    private List<ApexDocTokenLocation> apexDocTokenLocations;

    public ApexTreeBuilder(String sourceCode) {
        this.sourceCode = sourceCode;
        sourceCodePositioner = new SourceCodePositioner(sourceCode);
        apexDocTokenLocations = buildApexDocTokenLocations(sourceCode);
    }

    static <T extends AstNode> AbstractApexNode<T> createNodeAdapter(T node) {
        try {
            @SuppressWarnings("unchecked")
            // the register function makes sure only ApexNode<T> can be added,
            // where T is "T extends AstNode".
            Constructor<? extends AbstractApexNode<T>> constructor = (Constructor<? extends AbstractApexNode<T>>) NODE_TYPE_TO_NODE_ADAPTER_TYPE
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

    public <T extends AstNode> ApexNode<T> build(T astNode) {
        // Create a Node
        AbstractApexNode<T> node = createNodeAdapter(astNode);
        node.calculateLineNumbers(sourceCodePositioner);
        node.handleSourceCode(sourceCode);

        // Append to parent
        Node parent = nodes.isEmpty() ? null : nodes.peek();
        if (parent != null) {
            parent.jjtAddChild(node, parent.getNumChildren());
            node.jjtSetParent(parent);
        }

        // Build the children...
        nodes.push(node);
        parents.push(astNode);
        astNode.traverse(this, scope);
        nodes.pop();
        parents.pop();

        if (nodes.isEmpty()) {
            // add the comments only at the end of the processing as the last step
            addFormalComments();
        }

        return node;
    }

    private void addFormalComments() {
        for (ApexDocTokenLocation tokenLocation : apexDocTokenLocations) {
            ApexNode<?> parent = tokenLocation.nearestNode;
            if (parent != null) {
                ASTFormalComment comment = new ASTFormalComment(tokenLocation.token);
                comment.calculateLineNumbers(sourceCodePositioner, tokenLocation.index,
                        tokenLocation.index + tokenLocation.token.getText().length());

                // move existing nodes so that we can insert the comment as the first node
                for (int i = parent.getNumChildren(); i > 0; i--) {
                    parent.jjtAddChild(parent.getChild(i - 1), i);
                }

                parent.jjtAddChild(comment, 0);
                comment.jjtSetParent(parent);
            }
        }
    }

    private void buildFormalComment(AstNode node) {
        if (parents.peek() == node) {
            ApexNode<?> parent = (ApexNode<?>) nodes.peek();
            assignApexDocTokenToNode(node, parent);
        }
    }

    /**
     * Only remembers the node, to which the comment could belong.
     * Since the visiting order of the nodes does not match the source order,
     * the nodes appearing later in the source might be visiting first.
     * The correct node will then be visited afterwards, and since the distance
     * to the comment is smaller, it overrides the remembered node.
     * @param jorjeNode the original node
     * @param node the potential parent node, to which the comment could belong
     */
    private void assignApexDocTokenToNode(AstNode jorjeNode, ApexNode<?> node) {
        Location loc = jorjeNode.getLoc();
        if (!Locations.isReal(loc)) {
            // Synthetic nodes such as "<clinit>" don't have a location in the
            // source code, since they are generated by the compiler
            return;
        }
        // find the token, that appears as close as possible before the node
        int nodeStart = loc.getStartIndex();
        for (ApexDocTokenLocation tokenLocation : apexDocTokenLocations) {
            if (tokenLocation.index > nodeStart) {
                // this and all remaining tokens are after the node
                // so no need to check the remaining tokens.
                break;
            }

            int distance = nodeStart - tokenLocation.index;
            if (tokenLocation.nearestNode == null || distance < tokenLocation.nearestNodeDistance) {
                tokenLocation.nearestNode = node;
                tokenLocation.nearestNodeDistance = distance;
            }
        }
    }

    private static List<ApexDocTokenLocation> buildApexDocTokenLocations(String source) {
        ANTLRStringStream stream = new ANTLRStringStream(source);
        ApexLexer lexer = new ApexLexer(stream);

        List<ApexDocTokenLocation> tokenLocations = new LinkedList<>();
        int startIndex = 0;
        Token token = lexer.nextToken();
        int endIndex = lexer.getCharIndex();

        while (token.getType() != Token.EOF) {
            if (token.getType() == ApexLexer.BLOCK_COMMENT) {
                // Filter only block comments starting with "/**"
                if (token.getText().startsWith("/**")) {
                    tokenLocations.add(new ApexDocTokenLocation(startIndex, token));
                }
            }
            // TODO : Check other non-doc comments and tokens of type ApexLexer.EOL_COMMENT for "NOPMD" suppressions
            startIndex = endIndex;
            token = lexer.nextToken();
            endIndex = lexer.getCharIndex();
        }

        return tokenLocations;
    }

    private static class ApexDocTokenLocation {
        int index;
        Token token;
        ApexNode<?> nearestNode;
        int nearestNodeDistance;

        ApexDocTokenLocation(int index, Token token) {
            this.index = index;
            this.token = token;
        }
    }

    private boolean visit(AstNode node) {
        if (parents.peek() == node) {
            return true;
        } else {
            build(node);
            return false;
        }
    }

    @Override
    public boolean visit(UserEnum node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(UserInterface node, AdditionalPassScope scope) {
        final boolean ret = visit(node);
        buildFormalComment(node);
        return ret;
    }

    @Override
    public boolean visit(UserTrigger node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(ArrayLoadExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(ArrayStoreExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(AssignmentExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(BinaryExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(BooleanExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(ClassRefExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(InstanceOfExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(JavaMethodCallExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(JavaVariableExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(LiteralExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(ReferenceExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(MethodCallExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(NewListInitExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(NewMapInitExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(NewSetInitExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(NewListLiteralExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(NewObjectExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(NewSetLiteralExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(PackageVersionExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(PostfixExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(PrefixExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(TernaryExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(StandardCondition node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(TriggerVariableExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(VariableExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(BlockStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(BreakStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(ContinueStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(DmlDeleteStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(DmlInsertStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(DmlMergeStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(DmlUndeleteStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(DmlUpdateStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(DmlUpsertStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(DoLoopStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(ExpressionStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(ForEachStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(ForLoopStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(FieldDeclaration node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(FieldDeclarationStatements node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(IfBlockStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(IfElseBlockStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(ReturnStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(RunAsBlockStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(ThrowStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(VariableDeclaration node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(VariableDeclarationStatements node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(WhileLoopStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(BindExpressions node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(SoqlExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(SoslExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(NewMapLiteralExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(MapEntryNode node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(CatchBlockStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(TryCatchFinallyBlockStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(Property node, AdditionalPassScope scope) {
        final boolean ret = visit(node);
        buildFormalComment(node);
        return ret;
    }

    @Override
    public boolean visit(Field node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(Parameter node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(BridgeMethodCreator node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(UserClassMethods node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(UserExceptionMethods node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(Annotation node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(AnnotationParameter node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(ModifierNode node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(SuperMethodCallExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(ThisMethodCallExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(SuperVariableExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(ThisVariableExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(UserClass node, AdditionalPassScope scope) {
        final boolean ret = visit(node);
        buildFormalComment(node);
        return ret;
    }

    @Override
    public boolean visit(Method node, AdditionalPassScope scope) {
        final boolean ret = visit(node);
        buildFormalComment(node);
        return ret;
    }

    @Override
    public boolean visit(AnonymousClass node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(CastExpression node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(NewKeyValueObjectExpression node, AdditionalPassScope scope) {
        return visit(node);
    }
}
