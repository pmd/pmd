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
import net.sourceforge.pmd.rules.CouplingBetweenObjectsRule;

public class CouplingBetweenObjectsRuleTest extends RuleTst {

    private static final String TEST1 =
    "import java.util.*;" + PMD.EOL +
    "" + PMD.EOL +
    "public class CouplingBetweenObjects1 {" + PMD.EOL +
    " public List foo() {return null;}" + PMD.EOL +
    " public ArrayList foo() {return null;}" + PMD.EOL +
    " public Vector foo() {return null;}" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class CouplingBetweenObjects2 {" + PMD.EOL +
    "}";


    private Rule rule;

    public void setUp() {
        rule = new CouplingBetweenObjectsRule();
        rule.addProperty("threshold", "2");
    }

    public void testSimpleBad() throws Throwable {
        runTestFromString(TEST1, 1, rule);
    }

    public void testSimpleOK() throws Throwable {
        runTestFromString(TEST2, 0, rule);
    }
}
