package net.sourceforge.pmd.lang.apex.metrics.impl.visitors;

import net.sourceforge.pmd.lang.apex.ast.*;

/**
 * @author Gwilym Kuiper
 */
public class CognitiveComplexityVisitor extends ApexParserVisitorAdapter {
    public static class State {
        public double getComplexity() {
            return 0.0;
        }
    }
}
