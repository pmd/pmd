/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Stack;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.Token;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.apex.ApexParserOptions;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

@Deprecated
@InternalApi
public final class ApexTreeBuilder {

    private static final String DOC_COMMENT_PREFIX = "/**";

    private static final Map<Class<? extends Node>, Constructor<? extends AbstractApexNode<?>>>
        NODE_TYPE_TO_NODE_ADAPTER_TYPE = new HashMap<>();

    static {
        /*
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
        register(ElseWhenBlock.class, ASTElseWhenBlock.class);
        register(EmptyReferenceExpression.class, ASTEmptyReferenceExpression.class);
        register(Expression.class, ASTExpression.class);
        register(ExpressionStatement.class, ASTExpressionStatement.class);
        register(Field.class, ASTField.class);
        register(FieldDeclaration.class, ASTFieldDeclaration.class);
        register(FieldDeclarationStatements.class, ASTFieldDeclarationStatements.class);
        register(ForEachStatement.class, ASTForEachStatement.class);
        register(ForLoopStatement.class, ASTForLoopStatement.class);
        register(IdentifierCase.class, ASTIdentifierCase.class);
        register(IfBlockStatement.class, ASTIfBlockStatement.class);
        register(IfElseBlockStatement.class, ASTIfElseBlockStatement.class);
        register(IllegalStoreExpression.class, ASTIllegalStoreExpression.class);
        register(InstanceOfExpression.class, ASTInstanceOfExpression.class);
        register(InvalidDependentCompilation.class, ASTInvalidDependentCompilation.class);
        register(JavaMethodCallExpression.class, ASTJavaMethodCallExpression.class);
        register(JavaVariableExpression.class, ASTJavaVariableExpression.class);
        register(LiteralCase.class, ASTLiteralCase.class);
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
        register(SwitchStatement.class, ASTSwitchStatement.class);
        register(TernaryExpression.class, ASTTernaryExpression.class);
        register(ThisMethodCallExpression.class, ASTThisMethodCallExpression.class);
        register(ThisVariableExpression.class, ASTThisVariableExpression.class);
        register(ThrowStatement.class, ASTThrowStatement.class);
        register(TriggerVariableExpression.class, ASTTriggerVariableExpression.class);
        register(TryCatchFinallyBlockStatement.class, ASTTryCatchFinallyBlockStatement.class);
        register(TypeWhenBlock.class, ASTTypeWhenBlock.class);
        register(UserClass.class, ASTUserClass.class);
        register(UserClassMethods.class, ASTUserClassMethods.class);
        register(UserExceptionMethods.class, ASTUserExceptionMethods.class);
        register(UserEnum.class, ASTUserEnum.class);
        register(UserInterface.class, ASTUserInterface.class);
        register(UserTrigger.class, ASTUserTrigger.class);
        register(ValueWhenBlock.class, ASTValueWhenBlock.class);
        register(VariableDeclaration.class, ASTVariableDeclaration.class);
        register(VariableDeclarationStatements.class, ASTVariableDeclarationStatements.class);
        register(VariableExpression.class, ASTVariableExpression.class);
        register(WhileLoopStatement.class, ASTWhileLoopStatement.class);
         */
        // TODO(b/239648780)
    }

    private static <T extends Node> void register(Class<T> nodeType,
            Class<? extends AbstractApexNode<T>> nodeAdapterType) {
        try {
            NODE_TYPE_TO_NODE_ADAPTER_TYPE.put(nodeType, nodeAdapterType.getDeclaredConstructor(nodeType));
        } catch (SecurityException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    // The nodes having children built.
    private Stack<ApexNode<?>> nodes = new Stack<>();

    // The Apex nodes with children to build.
    private Stack<Node> parents = new Stack<>();

    private final SourceCodePositioner sourceCodePositioner;
    private final String sourceCode;
    private final CommentInformation commentInfo;

    public ApexTreeBuilder(String sourceCode, ApexParserOptions parserOptions) {
        this.sourceCode = sourceCode;
        sourceCodePositioner = new SourceCodePositioner(sourceCode);
        commentInfo = extractInformationFromComments(sourceCode, parserOptions.getSuppressMarker());
    }

    static <T extends Node> AbstractApexNode<T> createNodeAdapter(T node) {
        try {
            @SuppressWarnings("unchecked")
            // the register function makes sure only ApexNode<T> can be added,
            // where T is "T extends Node".
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

    public <T extends Node> ApexNode<T> build(T astNode) {
        // Create a Node
        AbstractApexNode<T> node = createNodeAdapter(astNode);
        node.handleSourceCode(sourceCode);

        // Append to parent
        ApexNode<?> parent = nodes.isEmpty() ? null : nodes.peek();
        if (parent != null) {
            parent.jjtAddChild(node, parent.getNumChildren());
            node.jjtSetParent(parent);
        }

        // Build the children...
        nodes.push(node);
        parents.push(astNode);
        // astNode.traverse(this, scope);
        // TODO(b/239648780)
        nodes.pop();
        parents.pop();

        if (nodes.isEmpty()) {
            // add the comments only at the end of the processing as the last step
            addFormalComments();
        }

        // calculate line numbers after the tree is built
        // so that we can look at parent/children to figure
        // out the positions if necessary.
        node.calculateLineNumbers(sourceCodePositioner);

        // If appropriate, determine whether this node contains comments or not
        if (node instanceof AbstractApexCommentContainerNode) {
            AbstractApexCommentContainerNode<?> commentContainer = (AbstractApexCommentContainerNode<?>) node;
            if (containsComments(commentContainer)) {
                commentContainer.setContainsComment(true);
            }
        }

        return node;
    }

    private boolean containsComments(ASTCommentContainer<?> commentContainer) {
        /*
        Location loc = commentContainer.getNode().getLoc();
        if (!Locations.isReal(loc)) {
            // Synthetic nodes don't have a location and can't have comments
            return false;
        }

        List<TokenLocation> allComments = commentInfo.allCommentTokens;
        // find the first comment after the start of the container node
        int index = Collections.binarySearch(commentInfo.allCommentTokensByStartIndex, loc.getStartIndex());

        // no exact hit found - this is expected: there is no comment token starting at the very same index as the node
        assert index < 0 : "comment token is at the same position as non-comment token";
        // extract "insertion point"
        index = ~index;

        // now check whether the next comment after the node is still inside the node
        return index >= 0 && index < allComments.size()
            && loc.getStartIndex() < allComments.get(index).index
            && loc.getEndIndex() > allComments.get(index).index;
         */
        // TODO(b/239648780)
        return false;
    }

    private void addFormalComments() {
        for (ApexDocTokenLocation tokenLocation : commentInfo.docTokenLocations) {
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

    private void buildFormalComment(Node node) {
        if (node.equals(parents.peek())) {
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
     *
     * @param jorjeNode the original node
     * @param node the potential parent node, to which the comment could belong
     */
    private void assignApexDocTokenToNode(Node jorjeNode, ApexNode<?> node) {
        /*
        Location loc = jorjeNode.getLoc();
        if (!Locations.isReal(loc)) {
            // Synthetic nodes such as "<clinit>" don't have a location in the
            // source code, since they are generated by the compiler
            return;
        }
        // find the token, that appears as close as possible before the node
        int nodeStart = loc.getStartIndex();
        for (ApexDocTokenLocation tokenLocation : commentInfo.docTokenLocations) {
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
         */
        // TODO(b/239648780)
    }

    private static CommentInformation extractInformationFromComments(String source, String suppressMarker) {
        /*
        ANTLRStringStream stream = new ANTLRStringStream(source);
        ApexLexer lexer = new ApexLexer(stream);

        ArrayList<TokenLocation> allCommentTokens = new ArrayList<>();
        List<ApexDocTokenLocation> tokenLocations = new ArrayList<>();
        Map<Integer, String> suppressMap = new HashMap<>();

        int startIndex = 0;
        Token token = lexer.nextToken();
        int endIndex = lexer.getCharIndex();

        boolean checkForCommentSuppression = suppressMarker != null;

        while (token.getType() != Token.EOF) {
            // Keep track of all comment tokens
            if (token.getType() == ApexLexer.BLOCK_COMMENT || token.getType() == ApexLexer.EOL_COMMENT) {
                assert allCommentTokens.isEmpty()
                    || allCommentTokens.get(allCommentTokens.size() - 1).index < startIndex
                    : "Comments should be sorted";
                if (!token.getText().startsWith(DOC_COMMENT_PREFIX)) {
                    allCommentTokens.add(new TokenLocation(startIndex, token));
                }
            }

            if (token.getType() == ApexLexer.BLOCK_COMMENT) {
                // Filter only block comments starting with "/**"
                if (token.getText().startsWith(DOC_COMMENT_PREFIX)) {
                    tokenLocations.add(new ApexDocTokenLocation(startIndex, token));
                }
            } else if (checkForCommentSuppression && token.getType() == ApexLexer.EOL_COMMENT) {
                // check if it starts with the suppress marker
                String trimmedCommentText = token.getText().substring(2).trim();

                if (trimmedCommentText.startsWith(suppressMarker)) {
                    String userMessage = trimmedCommentText.substring(suppressMarker.length()).trim();
                    suppressMap.put(token.getLine(), userMessage);
                }
            }

            startIndex = endIndex;
            token = lexer.nextToken();
            endIndex = lexer.getCharIndex();
        }

        return new CommentInformation(suppressMap, allCommentTokens, tokenLocations);
         */
        // TODO(b/239648780)
        return null;
    }

    private static class CommentInformation {

        final Map<Integer, String> suppressMap;
        final List<TokenLocation> allCommentTokens;
        final TokenListByStartIndex allCommentTokensByStartIndex;
        final List<ApexDocTokenLocation> docTokenLocations;

        <T extends List<TokenLocation> & RandomAccess>
            CommentInformation(Map<Integer, String> suppressMap, T allCommentTokens, List<ApexDocTokenLocation> docTokenLocations) {
            this.suppressMap = suppressMap;
            this.allCommentTokens = allCommentTokens;
            this.docTokenLocations = docTokenLocations;
            this.allCommentTokensByStartIndex = new TokenListByStartIndex(allCommentTokens);
        }
    }

    /**
     * List that maps comment tokens to their start index without copy.
     * This is used to implement a "binary search by key" routine which unfortunately isn't in the stdlib.
     *
     * <p>
     * Note that the provided token list must implement {@link RandomAccess}.
     */
    private static final class TokenListByStartIndex extends AbstractList<Integer> implements RandomAccess {

        private final List<TokenLocation> tokens;

        <T extends List<TokenLocation> & RandomAccess> TokenListByStartIndex(T tokens) {
            this.tokens = tokens;
        }

        @Override
        public Integer get(int index) {
            return tokens.get(index).index;
        }

        @Override
        public int size() {
            return tokens.size();
        }
    }

    private static class TokenLocation {
        int index;
        Token token;

        TokenLocation(int index, Token token) {
            this.index = index;
            this.token = token;
        }
    }

    private static class ApexDocTokenLocation extends TokenLocation {
        ApexNode<?> nearestNode;
        int nearestNodeDistance;

        ApexDocTokenLocation(int index, Token token) {
            super(index, token);
        }
    }

    private boolean visit(Node node) {
        if (node.equals(parents.peek())) {
            return true;
        } else {
            build(node);
            return false;
        }
    }

    public Map<Integer, String> getSuppressMap() {
        return commentInfo.suppressMap;
    }

    /*
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

    @Override
    public boolean visit(SwitchStatement node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(ElseWhenBlock node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(TypeWhenBlock node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(ValueWhenBlock node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(LiteralCase node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(IdentifierCase node, AdditionalPassScope scope) {
        return visit(node);
    }

    @Override
    public boolean visit(EmptyReferenceExpression node, AdditionalPassScope scope) {
        return visit(node);
    }
     */
    // TODO(b/239648780)
}
