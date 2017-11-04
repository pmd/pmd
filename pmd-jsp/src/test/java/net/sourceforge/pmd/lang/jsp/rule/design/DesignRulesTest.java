/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.rule.design;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class DesignRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/jsp/design.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "NoInlineScript");
        addRule(RULESET, "NoInlineStyleInformation");
        addRule(RULESET, "NoLongScripts");
        addRule(RULESET, "NoScriptlets");
    }
}
