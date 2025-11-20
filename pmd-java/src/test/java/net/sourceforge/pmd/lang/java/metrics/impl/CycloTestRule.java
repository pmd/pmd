/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics.CycloOption;

/**
 * Tests cyclo.
 *
 * @author Clément Fournier
 */
public class CycloTestRule extends JavaIntMetricWithOptionsTestRule<CycloOption> {

    public CycloTestRule() {
        super(JavaMetrics.CYCLO, CycloOption.class);
    }

}
