/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.comments;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class CommentRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-comments";

    @Override
    public void setUp() {
        addRule(RULESET, "CommentRequired");
        addRule(RULESET, "CommentSize");
        addRule(RULESET, "CommentContent");
    }
}
