package test.net.sourceforge.pmd.rules;

public class SimpleAggregatorTst extends RuleTst {

    public void runTests(TestDescriptor[] tests) {
        for (int i=0; i<tests.length; i++) {
            try {
                runTestFromString(tests[i].code, tests[i].numberOfProblemsExpected, tests[i].rule);
            } catch (Throwable t) {
                t.printStackTrace();
                throw new RuntimeException("Test \"" + tests[i].description + "\" failed");
            }
        }
    }
}
