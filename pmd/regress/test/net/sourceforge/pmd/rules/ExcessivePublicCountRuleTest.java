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
import net.sourceforge.pmd.rules.ExcessivePublicCountRule;
import test.net.sourceforge.pmd.testframework.RuleTst;

public class ExcessivePublicCountRuleTest extends RuleTst {

    private ExcessivePublicCountRule rule;

    public void setUp() {
        rule = new ExcessivePublicCountRule();
    }

    public void testSimpleOK() throws Throwable {
        rule.addProperty("minimum", "50");
        runTestFromString(TEST1, 0, rule);
    }

    public void testSimpleBad() throws Throwable {
        rule.addProperty("minimum", "2");
        runTestFromString(TEST2, 1, rule);
    }

    private static final String TEST1 =
    "public class ExcessivePublicCountRule1 {" + PMD.EOL +
    " public int foo;" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class ExcessivePublicCountRule2 {" + PMD.EOL +
    " public int foo;" + PMD.EOL +
    " public int bif;" + PMD.EOL +
    " public int baz;" + PMD.EOL +
    " public int bof;" + PMD.EOL +
    "}";

}
