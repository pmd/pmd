/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * This rule finds StringBuffers which may have been pre-sized incorrectly.
 *
 * @author Allan Caplan
 * @author Andreas Dangel
 * @see <a href="https://sourceforge.net/p/pmd/discussion/188194/thread/aba9dae7/">Check StringBuffer sizes against
 *      usage </a>
 */
public class InsufficientStringBufferDeclarationRule extends AbstractJavaRulechainRule {

    private static final int DEFAULT_BUFFER_SIZE = 16;

    public InsufficientStringBufferDeclarationRule() {
        super(ASTVariableId.class);
    }

    private static class State {
        static final int UNKNOWN_CAPACITY = -1;
        ASTVariableId variable;
        TypeNode rootNode;
        int capacity;
        int anticipatedLength;
        Map<Node, Map<Node, Integer>> branches = new HashMap<>();

        State(ASTVariableId variable, TypeNode rootNode, int capacity, int anticipatedLength) {
            this.variable = variable;
            this.rootNode = rootNode;
            this.capacity = capacity;
            this.anticipatedLength = anticipatedLength;
        }


        public void addAnticipatedLength(int length) {
            this.anticipatedLength += length;
        }


        public boolean isInsufficient() {
            processBranches();
            return capacity >= 0 && anticipatedLength > capacity;
        }


        public Object[] getParamsForViolation() {
            return new String[] { getTypeName(variable), String.valueOf(capacity), String.valueOf(anticipatedLength) };
        }


        private String getTypeName(TypeNode node) {
            return node.getTypeMirror().getSymbol().getSimpleName();
        }


        public void addBranch(Node node, int counter) {
            Node parent = node.ancestors(ASTIfStatement.class).last();
            if (parent == null) {
                parent = node.ancestors(ASTSwitchStatement.class).last();
            }
            if (parent == null) {
                return;
            }
            branches.putIfAbsent(parent, new HashMap<>());
            Map<Node, Integer> blocks = branches.get(parent);
            if (!blocks.containsKey(node)) {
                blocks.put(node, counter);
            } else {
                blocks.put(node, blocks.get(node) + counter);
            }
        }


        private void processBranches() {
            for (Map<Node, Integer> blocks : branches.values()) {
                int counter = 0;
                for (Integer i : blocks.values()) {
                    counter = Math.max(counter, i);
                }
                addAnticipatedLength(counter);
            }
            branches.clear();
        }

        @Override
        public String toString() {
            return "State[capacity=" + capacity + ",anticipatedLength=" + anticipatedLength + "]";
        }
    }

    @Override
    public Object visit(ASTVariableId node, Object data) {
        if (!TypeTestUtil.isA(StringBuilder.class, node) && !TypeTestUtil.isA(StringBuffer.class, node)) {
            return data;
        }

        State state = getConstructorCapacity(node, node.getInitializer());

        for (ASTNamedReferenceExpr usage : node.getLocalUsages()) {
            if (usage.getParent() instanceof ASTMethodCall) {
                Node parent = usage.getParent();
                while (parent instanceof ASTMethodCall) {
                    ASTMethodCall methodCall = (ASTMethodCall) parent;
                    processMethodCall(state, methodCall);
                    parent = parent.getParent();
                }
            } else if (usage.getParent() instanceof ASTAssignmentExpression) {
                ASTAssignmentExpression assignment = (ASTAssignmentExpression) usage.getParent();
                State newState = getConstructorCapacity(node, assignment.getRightOperand());

                if (newState.rootNode != null) {
                    if (state.isInsufficient()) {
                        asCtx(data).addViolation(state.rootNode, state.getParamsForViolation());
                    }
                    state = newState;
                } else {
                    state.addAnticipatedLength(newState.anticipatedLength);
                }
            }
        }

        if (state.isInsufficient()) {
            asCtx(data).addViolation(state.rootNode, state.getParamsForViolation());
        }
        return data;
    }

    private void processMethodCall(State state, ASTMethodCall methodCall) {
        if ("append".equals(methodCall.getMethodName())) {
            Set<ASTLiteral> literals = collectArgumentsOfType(methodCall, ASTLiteral.class);
            int literalsCount = literals.stream().mapToInt(value -> calculateExpectedLength(value)).sum();

            Set<ASTVariableAccess> variables = collectArgumentsOfType(methodCall, ASTVariableAccess.class);
            int variablesCount = variables.stream().mapToInt(value -> calculateExpectedLength(value)).sum();

            int counter = literalsCount + variablesCount;
            ASTIfStatement ifStatement = methodCall.ancestors(ASTIfStatement.class).first();
            ASTSwitchBranch switchBranch = methodCall.ancestors(ASTSwitchBranch.class).first();
            if (ifStatement != null) {
                if (ifStatement.getThenBranch().descendants().any(n -> n == methodCall)) {
                    state.addBranch(ifStatement.getThenBranch(), counter);
                } else if (ifStatement.getElseBranch() != null) {
                    state.addBranch(ifStatement.getElseBranch(), counter);
                }
            } else if (switchBranch != null) {
                state.addBranch(switchBranch, counter);
            } else {
                state.addAnticipatedLength(counter);
            }
        } else if ("setLength".equals(methodCall.getMethodName())) {
            int newLength = calculateExpression(methodCall.getArguments().get(0));
            if (state.capacity != State.UNKNOWN_CAPACITY && newLength > state.capacity) {
                state.capacity = newLength; // a bigger setLength increases capacity
                state.rootNode = methodCall;
            }
            // setLength fills the string builder, any new append adds to this
            state.anticipatedLength = newLength;
        } else if ("ensureCapacity".equals(methodCall.getMethodName())) {
            int newCapacity = calculateExpression(methodCall.getArguments().get(0));
            if (newCapacity > state.capacity) {
                // only a bigger new capacity changes the capacity
                state.capacity = newCapacity;
                state.rootNode = methodCall;
            }
        }
    }

    private static int countExpression(ASTExpression expr) {
        return Optional.of(expr.getConstFoldingResult())
                .map(ASTExpression.ConstResult::getValue)
                .map(String::valueOf)
                .map(String::length)
                .orElse(0);
    }

    private static int calculateExpectedLength(ASTExpression expression) {
        if (expression instanceof ASTNullLiteral) {
            return "null".length();
        }
        if (expression.getParent() instanceof ASTCastExpression
                && TypeTestUtil.isA(char.class, (ASTCastExpression) expression.getParent())) {
            return 1;
        }
        if (expression.getParent() instanceof ASTConditionalExpression) {
            ASTConditionalExpression conditionalExpression = (ASTConditionalExpression) expression.getParent();
            final ASTExpression other;
            if (expression == conditionalExpression.getThenBranch()) {
                other = conditionalExpression.getElseBranch();
            } else {
                other = conditionalExpression.getThenBranch();
            }
            int thisExpression = countExpression(expression);
            int otherExpression = countExpression(other);
            if (thisExpression > otherExpression) {
                return thisExpression;
            } else {
                return 0;
            }
        }
        return countExpression(expression);
    }

    private static <T extends ASTExpression> Set<T> collectArgumentsOfType(ASTMethodCall methodCall, Class<T> type) {
        return NodeStream.union(
                // direct children
                methodCall.getArguments().children(type),
                // string concatenation
                methodCall.getArguments().children(ASTInfixExpression.class)
                        .filter(e -> e.getOperator() == BinaryOp.ADD && TypeTestUtil.isA(String.class, e.getTypeMirror()))
                        .descendants(type)
                        .filter(n -> n.getParent() instanceof ASTInfixExpression),
                // cast expressions
                methodCall.getArguments().children(ASTCastExpression.class)
                        .children(type),
                // conditional expression
                methodCall.getArguments().children(ASTConditionalExpression.class)
                        .children()
                        .drop(1) // drop condition
                        .filterIs(type)
            ).collect(Collectors.toSet());
    }

    private State getConstructorCapacity(ASTVariableId variable, ASTExpression node) {
        State state = new State(variable, null, State.UNKNOWN_CAPACITY, 0);

        JavaNode possibleConstructorCall = node;

        JavaNode child = node;
        while (child instanceof ASTMethodCall) {
            processMethodCall(state, (ASTMethodCall) child);
            child = child.getFirstChild();
        }
        possibleConstructorCall = child;
        if (!(possibleConstructorCall instanceof ASTConstructorCall)) {
            return state;
        }
        ASTConstructorCall constructorCall = (ASTConstructorCall) possibleConstructorCall;
        if (constructorCall.getArguments().size() == 1) {
            ASTExpression argument = constructorCall.getArguments().get(0);
            if (TypeTestUtil.isA(String.class, argument)) {
                if (argument.getConstValue() == null) {
                    return state;
                }
                int stringLength = ((String) argument.getConstValue()).length();
                return new State(variable, constructorCall, DEFAULT_BUFFER_SIZE + stringLength, stringLength + state.anticipatedLength);
            } else {
                return new State(variable, constructorCall, calculateExpression(argument), state.anticipatedLength);
            }
        }
        return new State(variable, constructorCall, DEFAULT_BUFFER_SIZE, state.anticipatedLength);
    }

    private int calculateExpression(ASTExpression expression) {
        Object value = expression.getConstValue();
        if (value == null) {
            return State.UNKNOWN_CAPACITY;
        }
        if (value instanceof Character) {
            return (Character) value;
        }
        return (Integer) value;
    }
}
