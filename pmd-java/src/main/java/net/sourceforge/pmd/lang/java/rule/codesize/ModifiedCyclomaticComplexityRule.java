/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codesize;

import net.sourceforge.pmd.lang.java.oom.metrics.CycloMetric.Option;

/**
 * Implements the modified cyclomatic complexity rule.
 *
 * <p>Modified rules: Same as standard cyclomatic complexity, but switch statement
 * plus all cases count as 1.
 *
 * @author Alan Hohn, based on work by Donald A. Leckie
 * @version Revised June 12th, 2017 (Clément Fournier)
 * @see net.sourceforge.pmd.lang.java.oom.metrics.CycloMetric
 * @since June 18, 2014
 */
public class ModifiedCyclomaticComplexityRule extends StdCyclomaticComplexityRule {

    public ModifiedCyclomaticComplexityRule() {
        super();
        metricOption = Option.DO_NOT_COUNT_SWITCH_CASES;
    }


}
