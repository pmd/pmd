package test.net.sourceforge.pmd.rules.strictexception;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import java.util.List;
import java.util.ArrayList;

public class StrictExceptionRulesTest extends SimpleAggregatorTst {
    private List<Rule> rules = new ArrayList<Rule>();

    @Before
    public void setUp() {
        rules.add(findRule("strictexception", "AvoidCatchingNPE"));
        rules.add(findRule("strictexception", "AvoidCatchingThrowable"));
        rules.add(findRule("strictexception", "AvoidRethrowingException"));
        rules.add(findRule("strictexception", "AvoidThrowingNullPointerException"));
        rules.add(findRule("strictexception", "AvoidThrowingRawExceptionTypes"));
        rules.add(findRule("strictexception", "DoNotExtendJavaLangError"));
        rules.add(findRule("strictexception", "ExceptionAsFlowControl"));
        rules.add(findRule("strictexception", "SignatureDeclareThrowsException"));
    }

    @Test
    public void testAll() {
        for (Rule r : rules) {
            runTests(r);
        }
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(StrictExceptionRulesTest.class);
    }
}
