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

public class ReturnFromFinallyBlockTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//TryStatement[@Finally='true']/Block[position() = last()]//ReturnStatement");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "throw exception but return from finally", 1, rule),
           new TestDescriptor(TEST2, "lots of returns", 1, rule),
           new TestDescriptor(TEST3, "ok", 0, rule),
       });
    }
    private static final String TEST1 =
    "public class ReturnFromFinallyBlock1 {" + PMD.EOL +
    " public String bugga() {" + PMD.EOL +
    "  try {" + PMD.EOL +
    "   throw new Exception( \"My Exception\" );" + PMD.EOL +
    "  } catch (Exception e) {" + PMD.EOL +
    "   throw e;" + PMD.EOL +
    "  } finally {" + PMD.EOL +
    "   return \"A. O. K.\"; // Very bad." + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class ReturnFromFinallyBlock2 {" + PMD.EOL +
    " public String getBar() {" + PMD.EOL +
    "  try {" + PMD.EOL +
    "   return \"buz\";" + PMD.EOL +
    "  } catch (Exception e) {" + PMD.EOL +
    "   return \"biz\";" + PMD.EOL +
    "  } finally {" + PMD.EOL +
    "   return \"fiddle!\"; // bad!" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class ReturnFromFinallyBlock3 {" + PMD.EOL +
    " public String getBar() {" + PMD.EOL +
    "  try {" + PMD.EOL +
    "   return \"buz\";" + PMD.EOL +
    "  } finally {" + PMD.EOL +
    "  }" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
