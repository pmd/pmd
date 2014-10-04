/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.migrating;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class MigratingRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-migrating";

    @Override
    public void setUp() {
        addRule(RULESET, "AvoidAssertAsIdentifier");
        addRule(RULESET, "AvoidEnumAsIdentifier");
        addRule(RULESET, "ByteInstantiation");
        addRule(RULESET, "IntegerInstantiation");
        addRule(RULESET, "JUnit4SuitesShouldUseSuiteAnnotation");
        addRule(RULESET, "JUnit4TestShouldUseAfterAnnotation");
        addRule(RULESET, "JUnit4TestShouldUseBeforeAnnotation");
        addRule(RULESET, "JUnit4TestShouldUseTestAnnotation");
        addRule(RULESET, "JUnitUseExpected");
        addRule(RULESET, "LongInstantiation");
        addRule(RULESET, "ReplaceEnumerationWithIterator");
        addRule(RULESET, "ReplaceHashtableWithMap");
        addRule(RULESET, "ReplaceVectorWithList");
        addRule(RULESET, "ShortInstantiation");
    }
}
