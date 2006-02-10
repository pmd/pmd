package test.net.sourceforge.pmd.rules.strictexception;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class ExceptionAsFlowControlTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws Exception {
        rule = findRule("strictexception", "ExceptionAsFlowControl");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "failure case", 1, rule),
            new TestDescriptor(TEST2, "normal throw catch", 0, rule),
            new TestDescriptor(TEST3, "BUG 996007", 0, rule),
            new TestDescriptor(TEST4, "NPE", 0, rule)
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  try {" + PMD.EOL +
            "   try {" + PMD.EOL +
            "   } catch (Exception e) {" + PMD.EOL +
            "    throw new WrapperException(e);" + PMD.EOL +
            "    // this is essentially a GOTO to the WrapperException catch block" + PMD.EOL +
            "   }" + PMD.EOL +
            "  } catch (WrapperException e) {" + PMD.EOL +
            "   // do some more stuff " + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  try {} catch (Exception e) {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  try {} catch (IOException e) {" + PMD.EOL +
            "  if (foo!=null) " + PMD.EOL +
            "       throw new IOException(foo.getResponseMessage()); " + PMD.EOL +
            "  else  " + PMD.EOL +
            "       throw e; " + PMD.EOL +
            "  " + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  switch(foo) {" + PMD.EOL +
            "   default:" + PMD.EOL +
            "    throw new IllegalArgumentException();" + PMD.EOL +
            "  }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

}
