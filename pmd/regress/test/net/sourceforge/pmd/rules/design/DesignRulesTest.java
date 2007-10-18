package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import java.util.List;
import java.util.ArrayList;

public class DesignRulesTest extends SimpleAggregatorTst {
    private List<Rule> rules = new ArrayList<Rule>();

    @Before
    public void setUp() {
        rules.add(findRule("design", "AbstractClassWithoutAbstractMethod"));
        rules.add(findRule("design", "AccessorClassGeneration"));
        rules.add(findRule("design", "AssignmentToNonFinalStatic"));
        rules.add(findRule("design", "AvoidConstantsInterface"));
        rules.add(findRule("design", "AvoidDeeplyNestedIfStmts"));
        rules.add(findRule("design", "AvoidInstanceofChecksInCatchClause"));
        rules.add(findRule("design", "AvoidProtectedFieldInFinalClass"));
        rules.add(findRule("design", "AvoidReassigningParameters"));
        rules.add(findRule("design", "AvoidSynchronizedAtMethodLevel"));
        rules.add(findRule("design", "BadComparison"));
        rules.add(findRule("design", "ClassWithOnlyPrivateConstructorsShouldBeFinal"));
        rules.add(findRule("design", "CloseResource"));
        rules.add(findRule("design", "CompareObjectsWithEquals"));
        rules.add(findRule("design", "DefaultLabelNotLastInSwitchStmt"));
        rules.add(findRule("design", "EmptyMethodInAbstractClassShouldBeAbstract"));
        rules.add(findRule("design", "EqualsNull"));
        rules.add(findRule("design", "FinalFieldCouldBeStatic"));
        rules.add(findRule("design", "IdempotentOperations"));
        rules.add(findRule("design", "ImmutableField"));
        rules.add(findRule("design", "InstantiationToGetClass"));
        rules.add(findRule("design", "MissingBreakInSwitch"));
        rules.add(findRule("design", "MissingStaticMethodInNonInstantiatableClass"));
        rules.add(findRule("design", "NonCaseLabelInSwitchStatement"));
        rules.add(findRule("design", "NonStaticInitializer"));
        rules.add(findRule("design", "NonThreadSafeSingleton"));
        rules.add(findRule("design", "OptimizableToArrayCall"));
        rules.add(findRule("design", "PositionLiteralsFirstInComparisons"));
        rules.add(findRule("design", "PreserveStackTrace"));
        rules.add(findRule("design", "SimpleDateFormatNeedsLocale"));
        rules.add(findRule("design", "SimplifyBooleanExpressions"));
        rules.add(findRule("design", "SimplifyBooleanReturns"));
        rules.add(findRule("design", "SimplifyConditional"));
        rules.add(findRule("design", "SingularField"));
        rules.add(findRule("design", "SwitchDensity"));
        rules.add(findRule("design", "SwitchStmtsShouldHaveDefault"));
        rules.add(findRule("design", "UncommentedEmptyMethod"));
        rules.add(findRule("design", "UnnecessaryLocalBeforeReturn"));
        rules.add(findRule("design", "UnsynchronizedStaticDateFormatter"));
        rules.add(findRule("design", "UseCollectionIsEmpty"));
        rules.add(findRule("design", "UseLocaleWithCaseConversions"));
        rules.add(findRule("design", "UseNotifyAllInsteadOfNotify"));
        rules.add(findRule("design", "UseSingleton"));
    }

    @Test
    public void testAll() {
        for (Rule r : rules) {
            runTests(r);
        }
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(DesignRulesTest.class);
    }
}
