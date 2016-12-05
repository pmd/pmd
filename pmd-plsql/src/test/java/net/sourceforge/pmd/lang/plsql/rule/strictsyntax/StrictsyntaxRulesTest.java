/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.strictsyntax;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class StrictsyntaxRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "plsql-strictsyntax";

    @Override
    public void setUp() {
        addRule(RULESET, "MisplacedPragma");
    }
}
