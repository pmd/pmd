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
import net.sourceforge.pmd.rules.design.LongMethodRule;
import test.net.sourceforge.pmd.rules.RuleTst;

public class LongMethodRuleTest extends RuleTst {

    private LongMethodRule getIUT() {
        LongMethodRule IUT = new LongMethodRule();
        IUT.addProperty("minimum", "10");
        return IUT;
    }

    public void testShortMethod() throws Throwable {
        runTestFromString(TEST1, 0, getIUT());
    }
    public void testReallyLongMethod() throws Throwable {
        runTestFromString(TEST2, 1, getIUT());
    }
    public void testReallyLongMethodWithLongerRange() throws Throwable {
        LongMethodRule IUT = getIUT();
        IUT.addProperty("minimum", "20");
        runTestFromString(TEST2, 0, IUT);
    }
    public void testNotQuiteLongMethod() throws Throwable {
        runTestFromString(TEST3, 0, getIUT());
    }
    public void testLongMethod() throws Throwable {
        runTestFromString(TEST4, 1, getIUT());
    }

    private static final String TEST1 =
    "public class LongMethod1 {" + PMD.EOL +
    "    public static void main(String args[]) {" + PMD.EOL +
    "	System.err.println(\"This is short.\");" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class LongMethod2 {" + PMD.EOL +
    "    public static void main(String args[]) {" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "	System.err.println(\"This is long.\");" + PMD.EOL +
    "    } // 11 lines - violation" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class LongMethod2 {" + PMD.EOL +
    "    public static void main(String args[]) {" + PMD.EOL +
    "	System.err.println(\"This is long, but not a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, but not a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, but not a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, but not a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, but not a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, but not a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, but not a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, but not a violation\");" + PMD.EOL +
    "    } // 9 lines - Not a violation" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class LongMethod2 {" + PMD.EOL +
    "    public static void main(String args[]) {" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "	System.err.println(\"This is long, and is a violation\");" + PMD.EOL +
    "    } // > 10 lines - Not a violation" + PMD.EOL +
    "}";
}

