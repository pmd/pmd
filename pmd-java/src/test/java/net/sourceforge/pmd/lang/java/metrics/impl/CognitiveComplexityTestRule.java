/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;

/**
 * @author Denis Borovikov
 */
public class CognitiveComplexityTestRule extends JavaIntMetricTestRule {

    public CognitiveComplexityTestRule() {
        super(JavaMetrics.COGNITIVE_COMPLEXITY);
    }
}
