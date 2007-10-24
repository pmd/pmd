package test.net.sourceforge.pmd.rules.migrating;

import org.junit.Before;

import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class MigratingRulesTest extends SimpleAggregatorTst {

    @Before
    public void setUp() {
        addRule("migrating", "AvoidAssertAsIdentifier");
        addRule("migrating", "AvoidEnumAsIdentifier");
        addRule("migrating", "ByteInstantiation");
        addRule("migrating", "IntegerInstantiation");
        addRule("migrating", "JUnit4SuitesShouldUseSuiteAnnotation");
        addRule("migrating", "JUnit4TestShouldUseAfterAnnotation");
        addRule("migrating", "JUnit4TestShouldUseBeforeAnnotation");
        addRule("migrating", "JUnit4TestShouldUseTestAnnotation");
        addRule("migrating", "JUnitUseExpected");
        addRule("migrating", "LongInstantiation");
        addRule("migrating", "ReplaceEnumerationWithIterator");
        addRule("migrating", "ReplaceHashtableWithMap");
        addRule("migrating", "ReplaceVectorWithList");
        addRule("migrating", "ShortInstantiation");
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(MigratingRulesTest.class);
    }
}
