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
            nestingLevel++;
        }

        void decreaseNestingLevel() {
            nestingLevel--;
        }
    }

    @Override
    public Object visit(ASTIfBlockStatement node, Object data) {
        State state = (State) data;
        state.structureComplexity();
        state.nestingComplexity();

        state.increaseNestingLevel();
        super.visit(node, data);
        state.decreaseNestingLevel();

        return data;
    }
}
