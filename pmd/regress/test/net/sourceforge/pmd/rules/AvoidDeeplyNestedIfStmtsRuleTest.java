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
import net.sourceforge.pmd.rules.AvoidDeeplyNestedIfStmtsRule;

public class AvoidDeeplyNestedIfStmtsRuleTest extends RuleTst {

    public static final String TEST1 =
    "public class AvoidDeeplyNestedIfStmtsRule1 {" + PMD.EOL +
    " public void bar() { " + PMD.EOL +
    "  int x=2; " + PMD.EOL +
    "  int y=3; " + PMD.EOL +
    "  int z=4; " + PMD.EOL +
    "  if (x>y) { " + PMD.EOL +
    "   if (y>z) { " + PMD.EOL +
    "    if (z==x) { " + PMD.EOL +
    "     // this is officially out of control now " + PMD.EOL +
    "    } " + PMD.EOL +
    "   } " + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public static final String TEST2 =
    "public class AvoidDeeplyNestedIfStmtsRule2 {" + PMD.EOL +
    " public void bar() { " + PMD.EOL +
    "  if (true) {" + PMD.EOL +
    "  } else if (true) {" + PMD.EOL +
    "  } else if (true) {" + PMD.EOL +
    "  } else {" + PMD.EOL +
    "    // this ain't good code, but it shouldn't trigger this rule" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private Rule rule;

    public void setUp() {
        rule = new AvoidDeeplyNestedIfStmtsRule();
        rule.addProperty("problemDepth", "3");
    }

    public void test1() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }

    public void test2() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }
}
