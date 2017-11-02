/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.multithreading;

import net.sourceforge.pmd.testframework.SimpleAggregatorTst;

/**
 * Rule tests for the multithreading category
 */
public class MultithreadingRulesTest extends SimpleAggregatorTst {

    private static final String RULESET = "category/java/multithreading.xml";

    @Override
    public void setUp() {
        addRule(RULESET, "AvoidSynchronizedAtMethodLevel");
        addRule(RULESET, "AvoidThreadGroup");
        addRule(RULESET, "AvoidUsingVolatile");
        addRule(RULESET, "DontCallThreadRun");
        addRule(RULESET, "DoubleCheckedLocking");
        addRule(RULESET, "NonThreadSafeSingleton");
        addRule(RULESET, "UnsynchronizedStaticDateFormatter");
        addRule(RULESET, "UseConcurrentHashMap");
        addRule(RULESET, "UseNotifyAllInsteadOfNotify");
    }

    // Used by DontCallThreadRun test cases
    public static class TestThread extends Thread {
        @Override
        public void run() {
            System.out.println("test");
        }
    }
}
