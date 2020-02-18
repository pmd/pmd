package net.sourceforge.pmd.lang.apex.metrics.impl.visitors;

import net.sourceforge.pmd.lang.apex.ast.*;

/**
 * @author Gwilym Kuiper
 */
public class CognitiveComplexityVisitor extends ApexParserVisitorAdapter {
    public static class State {
        private int complexity = 0;
        private int nestingLevel = 0;

        public double getComplexity() {
            return complexity;
        }

        void structureComplexity() {
            complexity += 1;
        }

        void nestingComplexity() {
            complexity += nestingLevel;
        }

        void increaseNestingLevel() {
            structureComplexity();
            nestingComplexity();
            nestingLevel++;
        }

        void decreaseNestingLevel() {
            nestingLevel--;
        }
    }

    @Override
    public Object visit(ASTIfElseBlockStatement node, Object data) {
        State state = (State) data;

        boolean hasElseStatement = node.getNode().hasElseStatement();
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
}
