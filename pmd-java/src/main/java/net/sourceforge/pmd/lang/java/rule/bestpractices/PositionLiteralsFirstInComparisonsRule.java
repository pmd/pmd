/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

/**
 * @deprecated Replaced by {@link LiteralsFirstInComparisonsRule}
 */
@Deprecated
public class PositionLiteralsFirstInComparisonsRule extends AbstractPositionLiteralsFirstInComparisons {

    public PositionLiteralsFirstInComparisonsRule() {
        super(".equals");
    }

}
