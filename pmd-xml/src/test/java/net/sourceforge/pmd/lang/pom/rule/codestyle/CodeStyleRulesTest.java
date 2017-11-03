/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.pom.rule.codestyle;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class CodeStyleRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/pom/codestyle.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "ProjectVersionAsDependencyVersion");
    }
}
