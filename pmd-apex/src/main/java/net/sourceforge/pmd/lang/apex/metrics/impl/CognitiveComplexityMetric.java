/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.impl;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.metrics.impl.visitors.CognitiveComplexityVisitor;
import net.sourceforge.pmd.lang.metrics.MetricOptions;

/**
 * Measures the cognitive complexity of a Class / Method in Apex.
 *
 * See https://www.sonarsource.com/docs/CognitiveComplexity.pdf for information about the metric
 *
 * @author Gwilym Kuiper
 */
public class CognitiveComplexityMetric extends AbstractApexOperationMetric {
    @Override
    public double computeFor(ASTMethod node, MetricOptions options) {
        CognitiveComplexityVisitor.State resultingState = (CognitiveComplexityVisitor.State) node.jjtAccept(new CognitiveComplexityVisitor(), new CognitiveComplexityVisitor.State());
        return resultingState.getComplexity();
    }
}
