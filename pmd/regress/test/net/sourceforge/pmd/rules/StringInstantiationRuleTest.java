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

public class StringInstantiationRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//AllocationExpression[Name/@Image='String'][count(.//Expression) < 2][not(ArrayDimsAndInits)]");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "new 'new String's", 2, rule),
           new TestDescriptor(TEST2, "new String array", 0, rule),
           new TestDescriptor(TEST3, "using multiple parameter constructor", 0, rule),
           new TestDescriptor(TEST4, "using 4 parameter constructor", 0, rule)
       });
    }

    private static final String TEST1 =
    "public class StringInstantiation1 {" + PMD.EOL +
    " private String bar = new String(\"bar\");" + PMD.EOL +
    " private String baz = new String();" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class StringInstantiation2 {" + PMD.EOL +
    " private String[] bar = new String[5];" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class StringInstantiation3 {" + PMD.EOL +
    " public void foo() {" + PMD.EOL +
    "  byte[] bytes = new byte[50];" + PMD.EOL +
    "  String bar = new String(bytes, 0, bytes.length);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class StringInstantiation4 {" + PMD.EOL +
    " public void foo() {" + PMD.EOL +
    "  byte[] bytes = new byte[50];" + PMD.EOL +
    "  String bar = new String(bytes, 0, bytes.length, \"some-encoding\");" + PMD.EOL +
    " }" + PMD.EOL +
    "}";


}
