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
import net.sourceforge.pmd.rules.design.LongClassRule;
import test.net.sourceforge.pmd.rules.RuleTst;

public class LongClassRuleTest extends RuleTst {

    public LongClassRule getIUT() {
        LongClassRule IUT = new LongClassRule();
        IUT.addProperty("minimum", "10");
        return IUT;
    }

    public void testShortClass() throws Throwable {
        runTestFromString(TEST0, 0, getIUT());
    }

    public void testLongClass() throws Throwable {
        runTestFromString(TEST1, 1, getIUT());
    }

    public void testLongClassWithLongerTest() throws Throwable {
        LongClassRule IUT = getIUT();
        IUT.addProperty("minimum", "2000");
        runTestFromString(TEST1, 0, IUT);
    }

    private static final String TEST0 =
    "public class LongMethod1 {" + PMD.EOL +
    "    public static void main(String args[]) {" + PMD.EOL +
    "	System.err.println(\"This is short.\");" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST1 =
    "public class LongClass1" + PMD.EOL +
    "{" + PMD.EOL +
    "    public void method0() {" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "	System.err.println(\"This is a long class.\");" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";
}

