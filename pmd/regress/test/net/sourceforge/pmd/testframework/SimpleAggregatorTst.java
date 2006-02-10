/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.testframework;

import net.sourceforge.pmd.SourceType;

public class SimpleAggregatorTst extends RuleTst {

    public void runTests(TestDescriptor[] tests) {
        runTests(tests, DEFAULT_SOURCE_TYPE);
    }

    /**
     * Run a set of tests.
     *
     * @param tests
     */
    public void runTests(TestDescriptor[] tests, SourceType sourceType) {
        for (int i = 0; i < tests.length; i++) {
            try {
                runTestFromString(tests[i].code, tests[i].numberOfProblemsExpected,
                        tests[i].rule, sourceType);
            } catch (Throwable t) {
                t.printStackTrace();
                throw new RuntimeException("Test \"" + tests[i].description
                        + "\" failed");
            }
        }
    }

}
