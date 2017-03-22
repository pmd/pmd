/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.strings;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * @author Clément Fournier (clement.fournier@insa-rennes.fr)
 *
 */
public class InefficientEmptyStringCheckTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-strings";

    @Override
    public void setUp() {
        addRule(RULESET, "InefficientEmptyStringCheck");
    }
}
