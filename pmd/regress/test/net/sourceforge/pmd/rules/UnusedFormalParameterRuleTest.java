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
import net.sourceforge.pmd.rules.UnusedFormalParameterRule;

public class UnusedFormalParameterRuleTest extends SimpleAggregatorTst {

    private UnusedFormalParameterRule rule;

    public void setUp() {
        rule = new UnusedFormalParameterRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "one parameter", 1, rule),
           new TestDescriptor(TEST2, "fully qualified parameter", 0, rule),
           new TestDescriptor(TEST3, "one parameter with a method call", 0, rule),
           new TestDescriptor(TEST4, "interface", 0, rule)
       });
    }

    private static final String TEST1 =
    "class UnusedFormalParam1 {" + PMD.EOL +
    "    private void testMethod(String param1) {" + PMD.EOL +
    "        //System.out.println(param1);" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "class UnusedFormalParam2 {" + PMD.EOL +
    "    private void foo (String s) " + PMD.EOL +
    "    {String str = s.toString();}" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "class UnusedFormalParam3 {" + PMD.EOL +
    "    private void t1(String s) {" + PMD.EOL +
    "        s.toString();" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public interface Foo {" + PMD.EOL +
    " void foo(String bar);" + PMD.EOL +
    "}";
}
