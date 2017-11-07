/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.rule.codestyle;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class CodeStyleRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/jsp/codestyle.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "DuplicateJspImports");
    }
}
