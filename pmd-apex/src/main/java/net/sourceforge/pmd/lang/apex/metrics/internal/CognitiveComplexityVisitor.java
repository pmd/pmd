/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.internal;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTBooleanExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTCatchBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDoLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTEmptyReferenceExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTForEachStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTIfElseBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTPrefixExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTReferenceExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTTernaryExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTThisVariableExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTWhileLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexVisitorBase;
import net.sourceforge.pmd.lang.apex.ast.BooleanOperator;
import net.sourceforge.pmd.lang.apex.ast.PrefixOperator;
import net.sourceforge.pmd.lang.apex.metrics.internal.CognitiveComplexityVisitor.State;

/**
 * @author Gwilym Kuiper
 */
public class CognitiveComplexityVisitor extends ApexVisitorBase<State, Void> {

    public static final CognitiveComplexityVisitor INSTANCE = new CognitiveComplexityVisitor();

    public static class State {

        private int complexity = 0;
        private int nestingLevel = 0;

        private BooleanOperator currentBooleanOperation = null;
        private ASTMethod currentMethod = null;

        public int getComplexity() {
            return complexity;
        }

        void hybridComplexity() {
            complexity++;
            nestingLevel++;
        }

        void structureComplexity() {
            complexity++;
            complexity += nestingLevel;
            nestingLevel++;
        }

        void fundamentalComplexity() {
            complexity++;
        }

        void booleanOperation(BooleanOperator op) {
            if (currentBooleanOperation != op) {
                if (op != null) {
                    fundamentalComplexity();
                }

                currentBooleanOperation = op;
            }
        }

        void increaseNestingLevel() {
            nestingLevel++;
        }

        void decreaseNestingLevel() {
            nestingLevel--;
        }

        void methodCall(ASTMethodCallExpression call) {
            if (isRecursiveCall(call)) {
                structureComplexity();
            }
        }

        private boolean isRecursiveCall(ASTMethodCallExpression call) {
            if (currentMethod == null
                || !call.getMethodName().equalsIgnoreCase(currentMethod.getCanonicalName())
                || call.getInputParametersSize() != currentMethod.getArity()) {
                return false;
            }
            if (call.firstChild(ASTEmptyReferenceExpression.class) != null) {
                return true;
            }
            ASTReferenceExpression ref = call.firstChild(ASTReferenceExpression.class);
            if (ref == null) {
                return false;
            }
            if (ref.getNames().isEmpty()) {
                return ref.getNumChildren() == 1 && ref.getChild(0) instanceof ASTThisVariableExpression;
            }
            String refName = ref.getName();
            String definingType = currentMethod.getDefiningType();
            if (definingType != null && definingType.equalsIgnoreCase(refName)) {
                return true;
            }
            ASTUserClass enclosingClass = currentMethod.ancestors(ASTUserClass.class).first();
            return enclosingClass != null && enclosingClass.getSimpleName().equalsIgnoreCase(refName);
        }

        void setCurrentMethod(ASTMethod method) {
            currentMethod = method;
        }
    }

    @Override
    public Void visit(ASTIfElseBlockStatement node, State state) {

        boolean hasElseStatement = node.hasElseStatement();
        for (ApexNode<?> child : node.children()) {
            // If we don't have an else statement, we get an empty block statement which we shouldn't count
            if (!hasElseStatement && child instanceof ASTBlockStatement) {
                break;
            }

            if (child.getIndexInParent() == 0) {
                // the first IfBlock is the first "if"
                state.structureComplexity();
            } else {
                // any other IfBlocks are "else if"
                state.hybridComplexity();
            }
            child.acceptVisitor(this, state);
            state.decreaseNestingLevel();
        }

        return null;
    }

    @Override
    public Void visit(ASTForLoopStatement node, State state) {

        state.structureComplexity();
        super.visit(node, state);
        state.decreaseNestingLevel();

        return null;
    }

    @Override
    public Void visit(ASTForEachStatement node, State state) {
        state.structureComplexity();
        super.visit(node, state);
        state.decreaseNestingLevel();

        return null;
    }

    @Override
    public Void visit(ASTWhileLoopStatement node, State state) {
        state.structureComplexity();
        super.visit(node, state);
        state.decreaseNestingLevel();

        return null;
    }

    @Override
    public Void visit(ASTCatchBlockStatement node, State state) {
        state.structureComplexity();
        super.visit(node, state);
        state.decreaseNestingLevel();

        return null;
    }

    @Override
    public Void visit(ASTDoLoopStatement node, State state) {

        state.structureComplexity();
        super.visit(node, state);
        state.decreaseNestingLevel();

        return null;
    }

    @Override
    public Void visit(ASTTernaryExpression node, State state) {

        state.structureComplexity();
        super.visit(node, state);
        state.decreaseNestingLevel();

        return null;
    }

    @Override
    public Void visit(ASTBooleanExpression node, State state) {

        BooleanOperator op = node.getOp();
        if (op == BooleanOperator.LOGICAL_AND || op == BooleanOperator.LOGICAL_OR) {
            state.booleanOperation(op);
        }

        return super.visit(node, state);
    }

    @Override
    public Void visit(ASTPrefixExpression node, State state) {

        PrefixOperator op = node.getOp();
        if (op == PrefixOperator.LOGICAL_NOT) {
            state.booleanOperation(null);
        }

        return super.visit(node, state);
    }

    @Override
    public Void visit(ASTBlockStatement node, State state) {

        for (ApexNode<?> child : node.children()) {
            // This needs to happen because the current 'run' of boolean operations is terminated
            // once we finish a statement.
            state.booleanOperation(null);
            child.acceptVisitor(this, state);
        }

        return null;
    }

    @Override
    public Void visit(ASTMethod node, State state) {
        state.setCurrentMethod(node);
        return super.visit(node, state);
    }

    @Override
    public Void visit(ASTMethodCallExpression node, State state) {
        state.methodCall(node);
        return super.visit(node, state);
    }

    @Override
    public Void visit(ASTSwitchStatement node, State state) {
        state.structureComplexity();
        super.visit(node, state);
        state.decreaseNestingLevel();
        return null;
    }
}
