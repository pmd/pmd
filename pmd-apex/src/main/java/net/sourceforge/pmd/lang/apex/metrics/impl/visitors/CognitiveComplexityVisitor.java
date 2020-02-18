package net.sourceforge.pmd.lang.apex.metrics.impl.visitors;

import net.sourceforge.pmd.lang.apex.ast.*;

/**
 * @author Gwilym Kuiper
 */
public class CognitiveComplexityVisitor extends ApexParserVisitorAdapter {
    public static class State {
        private int complexity = 0;

        public double getComplexity() {
            return complexity;
        }

        void increaseComplexity() {
            complexity += 1;
        }
    }

    @Override
    public Object visit(ASTIfBlockStatement node, Object data) {
        State state = (State) data;
        state.increaseComplexity();
        super.visit(node, data);
        return data;
    }
}
