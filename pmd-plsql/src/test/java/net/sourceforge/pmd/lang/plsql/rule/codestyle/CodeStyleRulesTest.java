/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.codestyle;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class CodeStyleRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/plsql/codestyle.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "CodeFormat");
        addRule(RULESET, "MisplacedPragma");
        addRule(RULESET, "ForLoopNaming");
    }
}
