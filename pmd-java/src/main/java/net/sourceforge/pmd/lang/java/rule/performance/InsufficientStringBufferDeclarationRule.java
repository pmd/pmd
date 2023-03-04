/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCharLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTNumericLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaVisitorBase;
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
        super(ASTVariableDeclaratorId.class);
    }

    private static class State {
        ASTVariableDeclaratorId variable;
        TypeNode rootNode;
        int capacity;
        int anticipatedLength;
        Map<Node, Map<Node, Integer>> branches = new HashMap<>();

        State(ASTVariableDeclaratorId variable, TypeNode rootNode, int capacity, int anticipatedLength) {
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


        void addViolation(RuleContext ctx) {
            ctx.addViolation(rootNode, getTypeName(variable), String.valueOf(capacity), String.valueOf(anticipatedLength));
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
            branches.compute(parent, (parent1, map) -> {
                if (map == null) {
                    map = new HashMap<>();
                }
                map.compute(node, (node1, cur) -> cur != null ? cur + counter : counter);
                return map;
            });
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
    public Object visit(ASTVariableDeclaratorId node, Object data) {
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
                        state.addViolation(asCtx(data));
                    }
                    state = newState;
                } else {
                    state.addAnticipatedLength(newState.anticipatedLength);
                }
            }
        }

        if (state.isInsufficient()) {
            state.addViolation(asCtx(data));
        }
        return data;
    }

    private void processMethodCall(State state, ASTMethodCall methodCall) {
        if ("append".equals(methodCall.getMethodName())) {
            int counter = 0;
            Set<ASTLiteral> literals = new HashSet<>();
            literals.addAll(methodCall.getArguments()
                    .descendants(ASTLiteral.class)
                    // exclude literals, that belong to different method calls
                    .filter(n -> n.ancestors(ASTMethodCall.class).first() == methodCall).toList());
            for (ASTLiteral literal : literals) {
                if (literal instanceof ASTStringLiteral) {
                    counter += ((ASTStringLiteral) literal).length();
                } else if (literal instanceof ASTNumericLiteral) {
                    if (literal.getParent() instanceof ASTCastExpression
                        && TypeTestUtil.isA(char.class, (ASTCastExpression) literal.getParent())) {
                        counter += 1;
                    } else {
                        counter += String.valueOf(((ASTNumericLiteral) literal).getConstValue()).length();
                    }
                } else if (literal instanceof ASTCharLiteral) {
                    counter += 1;
                }
            }
    
            ASTIfStatement ifStatement = methodCall.ancestors(ASTIfStatement.class).first();
            ASTSwitchStatement switchStatement = methodCall.ancestors(ASTSwitchStatement.class).first();
            if (ifStatement != null) {
                if (ifStatement.getThenBranch().descendants().any(n -> n == methodCall)) {
                    state.addBranch(ifStatement.getThenBranch(), counter);
                } else if (ifStatement.getElseBranch() != null) {
                    state.addBranch(ifStatement.getElseBranch(), counter);
                }
            } else if (switchStatement != null) {
                state.addBranch(methodCall.ancestors(ASTSwitchBranch.class).first(), counter);
            } else {
                state.addAnticipatedLength(counter);
            }
        } else if ("setLength".equals(methodCall.getMethodName())) {
            int newLength = calculateMaxIntExpression(methodCall.getArguments().get(0));
            if (state.capacity != -1 && newLength > state.capacity) {
                state.capacity = newLength; // a bigger setLength increases capacity
                state.rootNode = methodCall;
            }
            // setLength fills the string builder, any new append adds to this
            state.anticipatedLength = newLength;
        } else if ("ensureCapacity".equals(methodCall.getMethodName())) {
            int newCapacity = calculateMaxIntExpression(methodCall.getArguments().get(0));
            if (newCapacity > state.capacity) {
                // only a bigger new capacity changes the capacity
                state.capacity = newCapacity;
                state.rootNode = methodCall;
            }
        }
    }

    private State getConstructorCapacity(ASTVariableDeclaratorId variable, ASTExpression node) {
        State state = new State(variable, null, -1, 0);

        JavaNode child = node;
        while (child instanceof ASTMethodCall) {
            processMethodCall(state, (ASTMethodCall) child);
            child = child.getFirstChild();
        }
        JavaNode possibleConstructorCall = child;
        if (!(possibleConstructorCall instanceof ASTConstructorCall)) {
            return state;
        }
        ASTConstructorCall constructorCall = (ASTConstructorCall) possibleConstructorCall;
        if (constructorCall.getArguments().size() == 1) {
            ASTExpression argument = constructorCall.getArguments().get(0);
            if (TypeTestUtil.isA(String.class, argument)) {
                int stringLength = calculateMaxLengthOfString(argument);
                return new State(variable, constructorCall,
                                 DEFAULT_BUFFER_SIZE + stringLength, stringLength + state.anticipatedLength);
            } else {
                return new State(variable, constructorCall, calculateMaxIntExpression(argument), state.anticipatedLength);
            }
        }
        return new State(variable, constructorCall, DEFAULT_BUFFER_SIZE, state.anticipatedLength);
    }

    private static final class IntCounter {

        public static final int UNKNOWN = -1;
        private int value = UNKNOWN;

        void setValue(int i) {
            value = i;
        }

        public int getValue() {
            return value;
        }

        public void clearValue() {
            value = UNKNOWN;
        }

        public void add(int value) {
            if (value == UNKNOWN || this.value == UNKNOWN) {
                this.value = UNKNOWN;
                return;
            }
            this.value += value;
        }

        public void max(int value) {
            if (value == UNKNOWN || this.value == UNKNOWN) {
                this.value = UNKNOWN;
                return;
            }
            this.value = Math.max(value, this.value);
        }
    }


    private int calculateMaxLengthOfString(ASTExpression expression) {

        class ExpressionVisitor extends BaseIntAdderVisitor {

            @Override
            public Void visit(ASTStringLiteral node, IntCounter data) {
                data.setValue(node.length());
                return null;
            }
        }

        IntCounter result = new IntCounter();
        expression.acceptVisitor(new ExpressionVisitor(), result);
        return result.getValue();
    }

    private int calculateMaxIntExpression(ASTExpression expression) {

        class ExpressionVisitor extends BaseIntAdderVisitor {

            @Override
            public Void visit(ASTNumericLiteral node, IntCounter data) {
                data.setValue(node.getValueAsInt());
                return null;
            }

            public Void visit(ASTCharLiteral node, IntCounter data) {
                data.setValue(node.getConstValue());
                return null;
            }

            @Override
            public Void visitExpression(ASTExpression node, IntCounter data) {
                data.clearValue();
                return null;
            }
        }

        IntCounter result = new IntCounter();
        expression.acceptVisitor(new ExpressionVisitor(), result);
        return result.getValue();
    }

    abstract static class BaseIntAdderVisitor extends JavaVisitorBase<IntCounter, Void> {

        @Override
        public Void visit(ASTConditionalExpression node, IntCounter data) {
            node.getThenBranch().acceptVisitor(this, data);
            int left = data.getValue();
            node.getElseBranch().acceptVisitor(this, data);

            data.max(left);
            return null;
        }

        @Override
        public Void visit(ASTInfixExpression node, IntCounter data) {
            IntCounter temp = new IntCounter();


            if (BinaryOp.ADD.equals(node.getOperator())) {
                data.setValue(0);
                node.getLeftOperand().acceptVisitor(this, temp);
                data.add(temp.getValue());
                node.getRightOperand().acceptVisitor(this, temp);
                data.add(temp.getValue());
            } else if (BinaryOp.MUL.equals(node.getOperator())) {
                node.getLeftOperand().acceptVisitor(this, temp);
                data.setValue(temp.getValue());
                node.getRightOperand().acceptVisitor(this, temp);
                data.setValue(data.getValue() * temp.getValue());
            }

            return null;
        }
    }
}
