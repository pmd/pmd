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
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.design.LongParameterListRule;
import test.net.sourceforge.pmd.rules.RuleTst;

public class LongParameterListRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class LongParameterList0 {" + PMD.EOL +
    "    public void foo() {}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class LongParameterList1 {" + PMD.EOL +
    "    public void foo(int p01, int p02, int p03, int p04, int p05," + PMD.EOL +
    "                   int p06, int p07, int p08, int p09, int p10 ) { }" + PMD.EOL +
    "    public void bar(int p01, int p02, int p03, int p04, int p05 ) { }" + PMD.EOL +
    "}";

    public LongParameterListRule getIUT() {
        LongParameterListRule IUT = new LongParameterListRule();
        IUT.addProperty("minimum", "9");
        return IUT;
    }

    public void testShortMethod() throws Throwable {
        runTestFromString(TEST1, 0, getIUT());
    }

    public void testOneLongMethod() throws Throwable {
        runTestFromString(TEST2, 1, getIUT());
    }
}
