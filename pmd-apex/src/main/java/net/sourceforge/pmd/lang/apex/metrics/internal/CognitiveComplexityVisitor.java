/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.internal;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTBooleanExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTCatchBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDoLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForEachStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTIfElseBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTPrefixExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTTernaryExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTWhileLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexVisitorBase;
import net.sourceforge.pmd.lang.apex.metrics.internal.CognitiveComplexityVisitor.State;

import apex.jorje.data.ast.BooleanOp;
import apex.jorje.data.ast.PrefixOp;

/**
 * @author Gwilym Kuiper
 */
public class CognitiveComplexityVisitor extends ApexVisitorBase<State, Void> {

    public static final CognitiveComplexityVisitor INSTANCE = new CognitiveComplexityVisitor();

    public static class State {

        private int complexity = 0;
        private int nestingLevel = 0;

        private BooleanOp currentBooleanOperation = null;
        private String methodName = null;

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

        void booleanOperation(BooleanOp op) {
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

        void methodCall(String methodCalledName) {
            if (methodCalledName.equals(methodName)) {
                structureComplexity();
            }
        }

        void setMethodName(String name) {
            methodName = name;
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

        BooleanOp op = node.getOperator();
        if (op == BooleanOp.AND || op == BooleanOp.OR) {
            state.booleanOperation(op);
        }

        return super.visit(node, state);
    }

    @Override
    public Void visit(ASTPrefixExpression node, State state) {

        PrefixOp op = node.getOperator();
        if (op == PrefixOp.NOT) {
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
        state.setMethodName(node.getCanonicalName());
        return super.visit(node, state);
    }

    @Override
    public Void visit(ASTMethodCallExpression node, State state) {
        state.methodCall(node.getMethodName());
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
