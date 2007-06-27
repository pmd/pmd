package test.net.sourceforge.pmd.rules.codesize;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import java.util.List;
import java.util.ArrayList;

public class CodesizeRulesTest extends SimpleAggregatorTst {
    private List<Rule> rules = new ArrayList<Rule>();

    @Before
    public void setUp() {
        rules.add(findRule("codesize", "ExcessivePublicCount"));
        rules.add(findRule("codesize", "ExcessiveClassLength"));
        rules.add(findRule("codesize", "ExcessiveParameterList"));
        rules.add(findRule("codesize", "ExcessiveMethodLength"));
        rules.add(findRule("codesize", "NcssConstructorCount"));
        rules.add(findRule("codesize", "NcssMethodCount"));
        rules.add(findRule("codesize", "NcssTypeCount"));
        rules.add(findRule("codesize", "NPathComplexity"));
        rules.add(findRule("codesize", "TooManyFields"));
    }

    @Test
    public void testAll() {
        for (Rule r : rules) {
            runTests(r);
        }
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CodesizeRulesTest.class);
    }
}
