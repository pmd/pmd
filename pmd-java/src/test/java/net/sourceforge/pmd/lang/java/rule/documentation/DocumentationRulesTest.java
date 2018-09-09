/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Rule tests for the documentation category
 */
public abstract class DocumentationRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/java/documentation.xml";

    @Override
    public void setUp() {
        addRule(RULESET, getClass().getSimpleName().replaceFirst("Test$", ""));
    }
}
