package net.sourceforge.pmd.lang.java.rule.comments;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class CommentRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-comments";

    @Before
    public void setUp() {
    	addRule(RULESET, "CommentRequired");
        addRule(RULESET, "CommentSize");
        addRule(RULESET, "CommentContent");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CommentRulesTest.class);
    }
}
