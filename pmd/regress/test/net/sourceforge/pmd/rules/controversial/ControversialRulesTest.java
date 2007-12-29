package test.net.sourceforge.pmd.rules.controversial;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class ControversialRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("controversial", "AssignmentInOperand");
        addRule("controversial", "AvoidFinalLocalVariable");
        addRule("controversial", "AvoidUsingNativeCode");
        addRule("controversial", "AvoidUsingShortType");
        addRule("controversial", "AvoidUsingVolatile");
        addRule("controversial", "AtLeastOneConstructor");
        addRule("controversial", "AvoidFinalLocalVariable");
        addRule("controversial", "BooleanInversion");
        addRule("controversial", "CallSuperInConstructor");
        addRule("controversial", "DataflowAnomalyAnalysis");
        addRule("controversial", "DefaultPackage");
        addRule("controversial", "DontImportSun");
        addRule("controversial", "DoNotCallGarbageCollectionExplicitly");
        addRule("controversial", "NullAssignment");
        addRule("controversial", "OnlyOneReturn");
        addRule("controversial", "SuspiciousOctalEscape");
        addRule("controversial", "UnnecessaryConstructor");
        addRule("controversial", "UnnecessaryParentheses");
        addRule("controversial", "UnusedModifier");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ControversialRulesTest.class);
    }
}
