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
import net.sourceforge.pmd.rules.UnnecessaryConversionTemporaryRule;

public class UnnecessaryTemporariesRuleTest extends RuleTst {

    private static final String TEST1 =
    " public class UnnecessaryTemporary1 {" + PMD.EOL +
    "     void method (int x) {" + PMD.EOL +
    "        new Integer(x).toString(); " + PMD.EOL +
    "        new Long(x).toString(); " + PMD.EOL +
    "        new Float(x).toString(); " + PMD.EOL +
    "        new Byte((byte)x).toString(); " + PMD.EOL +
    "        new Double(x).toString(); " + PMD.EOL +
    "        new Short((short)x).toString(); " + PMD.EOL +
    "     }" + PMD.EOL +
    " }";

    public void testSimple() throws Throwable {
        runTestFromString(TEST1, 6, new UnnecessaryConversionTemporaryRule());
    }
}
