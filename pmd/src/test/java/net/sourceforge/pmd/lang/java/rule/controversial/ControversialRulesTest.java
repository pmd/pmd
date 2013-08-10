package net.sourceforge.pmd.lang.java.rule.controversial;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import org.junit.Before;


public class ControversialRulesTest extends SimpleAggregatorTst {
    
    private static final String RULESET = "java-controversial";

    @Before
    public void setUp() {
        addRule(RULESET, "AssignmentInOperand");
        addRule(RULESET, "AvoidLiteralsInIfCondition");
        addRule(RULESET, "AvoidPrefixingMethodParameters");
        addRule(RULESET, "AvoidUsingNativeCode");
        addRule(RULESET, "AvoidUsingShortType");
        addRule(RULESET, "AvoidUsingVolatile");
        addRule(RULESET, "AtLeastOneConstructor");
        addRule(RULESET, "AvoidFinalLocalVariable");
        addRule(RULESET, "BooleanInversion");
        addRule(RULESET, "CallSuperInConstructor");
        addRule(RULESET, "DataflowAnomalyAnalysis");
        addRule(RULESET, "DefaultPackage");
        addRule(RULESET, "DontImportSun");
        addRule(RULESET, "DoNotCallGarbageCollectionExplicitly");
        addRule(RULESET, "NullAssignment");
        addRule(RULESET, "OnlyOneReturn");
        addRule(RULESET, "OneDeclarationPerLine");
        addRule(RULESET, "SuspiciousOctalEscape");
        addRule(RULESET, "UnnecessaryConstructor");
        addRule(RULESET, "UnnecessaryParentheses");
        addRule(RULESET, "UseConcurrentHashMap");
        addRule(RULESET, "UseObjectForClearerAPI");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ControversialRulesTest.class);
    }
}
