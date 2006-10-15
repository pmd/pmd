/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

/**
 * Adding this test to validate current working code doesn't break I've been
 * trying to locate the article referenced. The below code stresses the NPath
 * rule, and according to its current style, runs 2 tests, one pass and one
 * fail.
 * 
 * @author Allan Caplan
 * 
 */
public class NpathComplexityTest extends SimpleAggregatorTst{

    private Rule rule;

    public void setUp() {
        rule = findRule("codesize", "NPathComplexity");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
                new TestDescriptor(TEST1, "ok", 0, rule),
                new TestDescriptor(TEST2, "failure case", 1, rule),
        });
    }

    private static final String TEST1 =
        "public class Foo {" + PMD.EOL +
        " public static void bar() {" + PMD.EOL +
        "  if (true) {List buz = new ArrayList();}" + PMD.EOL +
        " }" + PMD.EOL +
        "}";

    private static final String TEST2 =
        "public class Foo {" + PMD.EOL +
        " public static int bar() {" + PMD.EOL +
        "  try{" + PMD.EOL +
        "  if (true) {List buz = new ArrayList();}" + PMD.EOL +
        "  for(int i = 0; i < 19; i++) {List buz = new ArrayList();}" + PMD.EOL +
        "  int j = 0;" + PMD.EOL +
        "  if (true) {j = 10;}" + PMD.EOL +
        "  while (j++ < 20) {List buz = new ArrayList();}" + PMD.EOL +
        "  if (true) {j = 21;}" + PMD.EOL +
        "  if(false) {j = 0;}" + PMD.EOL +
        "  do {List buz = new ArrayList();} while (j++ < 30); " + PMD.EOL +
        "  } catch(Exception e){" + PMD.EOL +
        "  if (true) {e.printStackTrace();}" + PMD.EOL +
        " }" + PMD.EOL +
        "  if (true) {return 1;}" + PMD.EOL +
        "  else {return 2;}" + PMD.EOL +
        " }" + PMD.EOL +
        "}";
}

