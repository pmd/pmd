package test.net.sourceforge.pmd.rules.codesize;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class CodesizeRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("codesize", "ExcessivePublicCount");
        addRule("codesize", "ExcessiveClassLength");
        addRule("codesize", "ExcessiveParameterList");
        addRule("codesize", "ExcessiveMethodLength");
        addRule("codesize", "NcssConstructorCount");
        addRule("codesize", "NcssMethodCount");
        addRule("codesize", "NcssTypeCount");
        addRule("codesize", "NPathComplexity");
        addRule("codesize", "TooManyFields");
        addRule("codesize", "TooManyMethods");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CodesizeRulesTest.class);
    }
}
