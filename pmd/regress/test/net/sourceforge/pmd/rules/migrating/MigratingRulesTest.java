package test.net.sourceforge.pmd.rules.migrating;

import net.sourceforge.pmd.Rule;

import org.junit.Before;
import org.junit.Test;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

import java.util.List;
import java.util.ArrayList;

public class MigratingRulesTest extends SimpleAggregatorTst {
    private List<Rule> rules = new ArrayList<Rule>();

    @Before
    public void setUp() {
        rules.add(findRule("migrating", "AvoidAssertAsIdentifier"));
        rules.add(findRule("migrating", "AvoidEnumAsIdentifier"));
        rules.add(findRule("migrating", "ByteInstantiation"));
        rules.add(findRule("migrating", "IntegerInstantiation"));
        rules.add(findRule("migrating", "JUnit4SuitesShouldUseSuiteAnnotation"));
        rules.add(findRule("migrating", "JUnit4TestShouldUseAfterAnnotation"));
        rules.add(findRule("migrating", "JUnit4TestShouldUseBeforeAnnotation"));
        rules.add(findRule("migrating", "JUnit4TestShouldUseTestAnnotation"));
        rules.add(findRule("migrating", "JUnitUseExpected"));
        rules.add(findRule("migrating", "LongInstantiation"));
        rules.add(findRule("migrating", "ReplaceEnumerationWithIterator"));
        rules.add(findRule("migrating", "ReplaceHashtableWithMap"));
        rules.add(findRule("migrating", "ReplaceVectorWithList"));
        rules.add(findRule("migrating", "ShortInstantiation"));
    }

    @Test
    public void testAll() {
        for (Rule r : rules) {
            runTests(r);
        }
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(MigratingRulesTest.class);
    }
}
