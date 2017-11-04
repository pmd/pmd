/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.rule.errorprone;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class ErrorProneRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/jsp/errorprone.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "JspEncoding");
    }
}
