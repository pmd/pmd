/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class JumbledIncrementerRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty(
            "xpath",
            "//ForStatement[ForUpdate/StatementExpressionList/StatementExpression/PrimaryExpression/PrimaryPrefix/Name/@Image = ancestor::ForStatement/ForInit//VariableDeclaratorId/@Image]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "", 1, rule),
           new TestDescriptor(TEST2, "", 0, rule),
           new TestDescriptor(TEST3, "", 0, rule),
           new TestDescriptor(TEST4, "using outer loop incrementor as array index is OK", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " public void foo() { " + PMD.EOL +
    "  for (int i = 0; i < 10; i++) { " + PMD.EOL +
    "   for (int k = 0; k < 20; i++) { " + PMD.EOL +
    "    int x = 2; " + PMD.EOL +
    "   } " + PMD.EOL +
    "  } " + PMD.EOL +
    " } " + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " public void foo() { " + PMD.EOL +
    "  for (int i = 0; i < 10; i++) { " + PMD.EOL +
    "   for (int k = 0; k < 20; k++) { " + PMD.EOL +
    "    int x = 2; " + PMD.EOL +
    "   } " + PMD.EOL +
    "  } " + PMD.EOL +
    " } " + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " public void foo() { " + PMD.EOL +
    "  for (int i=0; i<5; ) {" + PMD.EOL +
    "   i++;" + PMD.EOL +
    "  }" + PMD.EOL +
    "  for (int i=0;;) {" + PMD.EOL +
    "   if (i<5) {" + PMD.EOL +
    "    break;" + PMD.EOL +
    "   }" + PMD.EOL +
    "   i++;" + PMD.EOL +
    "  }" + PMD.EOL +
    "  for (;;) {" + PMD.EOL +
    "   int x =5;" + PMD.EOL +
    "  }" + PMD.EOL +
    "  for (int i=0; i<5;i++) ;" + PMD.EOL +
    "  for (int i=0; i<5;i++) " + PMD.EOL +
    "   foo();" + PMD.EOL +
    " } " + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " public void foo() { " + PMD.EOL +
    "  for (int i = 0; i < 10; i++) { " + PMD.EOL +
    "   for (int k = 0; k < 20; j[i]++) { " + PMD.EOL +
    "    int x = 2; " + PMD.EOL +
    "   } " + PMD.EOL +
    "  } " + PMD.EOL +
    " } " + PMD.EOL +
    "}";

}
