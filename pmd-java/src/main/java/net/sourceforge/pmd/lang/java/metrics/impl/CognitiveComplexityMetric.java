/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;


import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
import net.sourceforge.pmd.lang.java.metrics.impl.visitors.CognitiveComplexityBaseVisitor;
import net.sourceforge.pmd.lang.java.metrics.impl.visitors.CognitiveComplexityBaseVisitor.State;
import net.sourceforge.pmd.lang.metrics.MetricOptions;

/**
 * Measures the cognitive complexity of a Class / Method in Java.
 *
 * See https://www.sonarsource.com/docs/CognitiveComplexity.pdf for information about the metric
 *
 * @author Denis Borovikov, based on work of Gwilym Kuiper
 *
 * @since May 2021
 */
public class CognitiveComplexityMetric extends AbstractJavaOperationMetric {

    @Override
    public double computeFor(MethodLikeNode node, MetricOptions options) {
        final State resultingState = (State) node
            .jjtAccept(CognitiveComplexityBaseVisitor.INSTANCE, new State());
        return resultingState.getComplexity();
    }

}
