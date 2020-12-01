/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import java.math.BigInteger;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.metrics.api.JavaMetrics;
import net.sourceforge.pmd.test.AbstractMetricTestRule;

/**
 * @author Clément Fournier
 */
public class NPathTestRule extends AbstractMetricTestRule<BigInteger> {

    public NPathTestRule() {
        super(JavaMetrics.NPATH);
    }

    @Override
    protected String violationMessage(Node node, BigInteger result) {
        return AllMetricsTest.formatJavaMessage(node, result, super.violationMessage(node, result));
    }

    @Override
    protected BigInteger parseReportLevel(String value) {
        return new BigInteger(value);
    }

    @Override
    protected BigInteger defaultReportLevel() {
        return BigInteger.ZERO;
    }
}
