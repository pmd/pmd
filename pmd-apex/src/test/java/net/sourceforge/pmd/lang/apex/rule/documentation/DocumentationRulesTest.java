/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.documentation;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class DocumentationRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/apex/documentation.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "ApexDoc");
    }
}
