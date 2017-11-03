/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.rule.security;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class SecurityRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/jsp/security.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "IframeMissingSrcAttribute");
        addRule(RULESET, "NoUnsanitizedJSPExpression");
    }
}
