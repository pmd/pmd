package test.net.sourceforge.pmd.lang.java.rule.comments;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

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
