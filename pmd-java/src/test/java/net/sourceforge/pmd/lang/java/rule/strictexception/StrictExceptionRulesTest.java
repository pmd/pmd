/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.strictexception;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

public class StrictExceptionRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "java-strictexception";

    @Override
    public void setUp() {
        addRule(RULESET, "AvoidCatchingGenericException");
        addRule(RULESET, "AvoidCatchingNPE");
        addRule(RULESET, "AvoidCatchingThrowable");
        addRule(RULESET, "AvoidLosingExceptionInformation");
        addRule(RULESET, "AvoidRethrowingException");
        addRule(RULESET, "AvoidThrowingNewInstanceOfSameException");
        addRule(RULESET, "AvoidThrowingNullPointerException");
        addRule(RULESET, "AvoidThrowingRawExceptionTypes");
        addRule(RULESET, "DoNotExtendJavaLangError");
        addRule(RULESET, "DoNotExtendJavaLangThrowable");
        addRule(RULESET, "ExceptionAsFlowControl");
        addRule(RULESET, "SignatureDeclareThrowsException");
        addRule(RULESET, "DoNotThrowExceptionInFinally");
    }
}
