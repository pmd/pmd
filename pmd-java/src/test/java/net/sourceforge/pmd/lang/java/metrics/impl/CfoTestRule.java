/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.java.metrics.JavaMetrics;
import net.sourceforge.pmd.lang.java.metrics.JavaMetrics.ClassFanOutOption;

/**
 * @author Andreas Pabst
 */
public class CfoTestRule extends JavaIntMetricWithOptionsTestRule<ClassFanOutOption> {

    public CfoTestRule() {
        super(JavaMetrics.FAN_OUT, ClassFanOutOption.class);
    }
}
