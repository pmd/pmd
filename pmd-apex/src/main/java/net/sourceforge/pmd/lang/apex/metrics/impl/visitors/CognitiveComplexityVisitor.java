/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.impl.visitors;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTBooleanExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTCatchBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTContinueStatement;
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
import net.sourceforge.pmd.lang.apex.ast.ApexParserVisitorAdapter;

import apex.jorje.data.ast.BooleanOp;
import apex.jorje.data.ast.PrefixOp;

/**
 * @author Gwilym Kuiper
 */
public class CognitiveComplexityVisitor extends ApexParserVisitorAdapter {
    public static class State {
        private int complexity = 0;
        private int nestingLevel = 0;

        private BooleanOp currentBooleanOperation = null;
        private String methodName = null;

        public double getComplexity() {
            return complexity;
        }

        void structureComplexity() {
            complexity += 1;
        }

        void nestingComplexity() {
            complexity += nestingLevel;
        }

        void booleanOperation(BooleanOp op) {
            if (currentBooleanOperation != op) {
                if (op != null) {
                    structureComplexity();
                }

                currentBooleanOperation = op;
            }
        }

        void increaseNestingLevel() {
            structureComplexity();
            nestingComplexity();
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
    public Object visit(ASTIfElseBlockStatement node, Object data) {
        State state = (State) data;

        boolean hasElseStatement = node.hasElseStatement();
        for (ApexNode<?> child : node.children()) {
            // If we don't have an else statement, we get an empty block statement which we shouldn't count
            if (!hasElseStatement && child instanceof ASTBlockStatement) {
                break;
            }

            state.increaseNestingLevel();
            super.visit(child, data);
            state.decreaseNestingLevel();
        }

        return data;
    }

    @Override
    public Object visit(ASTForLoopStatement node, Object data) {
        State state = (State) data;

        state.increaseNestingLevel();
        super.visit(node, data);
        state.decreaseNestingLevel();

        return data;
    }

    @Override
    public Object visit(ASTForEachStatement node, Object data) {
        State state = (State) data;

        state.increaseNestingLevel();
        super.visit(node, data);
        state.decreaseNestingLevel();

        return data;
    }

    @Override
    public Object visit(ASTContinueStatement node, Object data) {
        State state = (State) data;

        state.structureComplexity();
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTBreakStatement node, Object data) {
        State state = (State) data;

        state.structureComplexity();
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTWhileLoopStatement node, Object data) {
        State state = (State) data;

        state.increaseNestingLevel();
        super.visit(node, data);
        state.decreaseNestingLevel();

        return data;
    }

    @Override
    public Object visit(ASTCatchBlockStatement node, Object data) {
        State state = (State) data;

        state.increaseNestingLevel();
        super.visit(node, data);
        state.decreaseNestingLevel();

        return data;
    }

    @Override
    public Object visit(ASTDoLoopStatement node, Object data) {
        State state = (State) data;

        state.increaseNestingLevel();
        super.visit(node, data);
        state.decreaseNestingLevel();

        return data;
    }

    @Override
    public Object visit(ASTTernaryExpression node, Object data) {
        State state = (State) data;

        state.increaseNestingLevel();
        super.visit(node, data);
        state.decreaseNestingLevel();

        return data;
    }

    @Override
    public Object visit(ASTBooleanExpression node, Object data) {
        State state = (State) data;

        BooleanOp op = node.getOperator();
        if (op == BooleanOp.AND || op == BooleanOp.OR) {
            state.booleanOperation(op);
        }

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTPrefixExpression node, Object data) {
        State state = (State) data;

        PrefixOp op = node.getOperator();
        if (op == PrefixOp.NOT) {
            state.booleanOperation(null);
        }

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTBlockStatement node, Object data) {
        State state = (State) data;

        for (ApexNode<?> child : node.children()) {
            child.jjtAccept(this, data);

            // This needs to happen because the current 'run' of boolean operations is terminated
            // once we finish a statement.
            state.booleanOperation(null);
        }

        return data;
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        State state = (State) data;
        state.setMethodName(node.getCanonicalName());
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodCallExpression node, Object data) {
        State state = (State) data;
        state.methodCall(node.getMethodName());
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        State state = (State) data;

        state.increaseNestingLevel();
        super.visit(node, data);
        state.decreaseNestingLevel();

        return state;
    }
}
