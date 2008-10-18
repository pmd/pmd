package test.net.sourceforge.pmd.lang.java.rule.codesize;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class CodesizeRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-codesize";

    @Before
    public void setUp() {
        addRule(RULESET, "ExcessivePublicCount");
        addRule(RULESET, "ExcessiveClassLength");
        addRule(RULESET, "ExcessiveParameterList");
        addRule(RULESET, "ExcessiveMethodLength");
        addRule(RULESET, "NcssConstructorCount");
        addRule(RULESET, "NcssMethodCount");
        addRule(RULESET, "NcssTypeCount");
        addRule(RULESET, "NPathComplexity");
        addRule(RULESET, "TooManyFields");
        addRule(RULESET, "TooManyMethods");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CodesizeRulesTest.class);
    }
}
