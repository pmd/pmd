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
import net.sourceforge.pmd.rules.SimplifyBooleanReturnsRule;

public class SimplifyBooleanReturnsRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class SimplifyBooleanReturns1 {" + PMD.EOL +
    " public void foo() {   " + PMD.EOL +
    "  if (true) {" + PMD.EOL +
    "   return true;" + PMD.EOL +
    "  } else {" + PMD.EOL +
    "  return false;" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class SimplifyBooleanReturns2 {" + PMD.EOL +
    " public boolean foo() {        " + PMD.EOL +
    "  if (true) " + PMD.EOL +
    "   return true;" + PMD.EOL +
    "   else " + PMD.EOL +
    "  return false;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class SimplifyBooleanReturns3 {" + PMD.EOL +
    " public Object foo() { " + PMD.EOL +
    "  if (!true) {" + PMD.EOL +
    "   return null;" + PMD.EOL +
    "  } else  {}" + PMD.EOL +
    "  return null;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    public void test1() throws Throwable {
        runTestFromString(TEST1, 1, new SimplifyBooleanReturnsRule());
    }
    public void test2() throws Throwable {
        runTestFromString(TEST2, 1, new SimplifyBooleanReturnsRule());
    }
    public void test3() throws Throwable {
        runTestFromString(TEST3, 0, new SimplifyBooleanReturnsRule());
    }
}
