/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.pom.rule.errorprone;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class ErrorProneRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/pom/errorprone.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "InvalidDependencyTypes");
        addRule(RULESET, "ProjectVersionAsDependencyVersion");
    }
}
