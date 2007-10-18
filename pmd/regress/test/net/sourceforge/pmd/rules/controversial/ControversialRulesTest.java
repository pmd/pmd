package test.net.sourceforge.pmd.rules.controversial;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import java.util.List;
import java.util.ArrayList;

public class ControversialRulesTest extends SimpleAggregatorTst {
    private List<Rule> rules = new ArrayList<Rule>();

    @Before
    public void setUp() {
        rules.add(findRule("controversial", "AssignmentInOperand"));
        rules.add(findRule("controversial", "AvoidFinalLocalVariable"));
        rules.add(findRule("controversial", "AvoidUsingNativeCode"));
        rules.add(findRule("controversial", "AvoidUsingShortType"));
        rules.add(findRule("controversial", "AvoidUsingVolatile"));
        rules.add(findRule("controversial", "AtLeastOneConstructor"));
        rules.add(findRule("controversial", "AvoidFinalLocalVariable"));
        rules.add(findRule("controversial", "BooleanInversion"));
        rules.add(findRule("controversial", "CallSuperInConstructor"));
        rules.add(findRule("controversial", "DataflowAnomalyAnalysis"));
        rules.add(findRule("controversial", "DefaultPackage"));
        rules.add(findRule("controversial", "DontImportSun"));
        rules.add(findRule("controversial", "NullAssignment"));
        rules.add(findRule("controversial", "OnlyOneReturn"));
        rules.add(findRule("controversial", "SuspiciousOctalEscape"));
        rules.add(findRule("controversial", "UnnecessaryConstructor"));
        rules.add(findRule("controversial", "UnnecessaryParentheses"));
        rules.add(findRule("controversial", "UnusedModifier"));
    }

    @Test
    public void testAll() {
        for (Rule r : rules) {
            runTests(r);
        }
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ControversialRulesTest.class);
    }
}
