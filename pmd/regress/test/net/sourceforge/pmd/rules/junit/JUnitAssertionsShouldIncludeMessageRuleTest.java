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
package test.net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.junit.JUnitAssertionsShouldIncludeMessageRule;
import test.net.sourceforge.pmd.rules.RuleTst;

public class JUnitAssertionsShouldIncludeMessageRuleTest extends RuleTst {

    public void testAssertEqualsOK() throws Throwable {
        runTestFromString(TEST1, 0, new JUnitAssertionsShouldIncludeMessageRule());
    }
    public void testAssertEqualsBad() throws Throwable {
        runTestFromString(TEST2, 1, new JUnitAssertionsShouldIncludeMessageRule());
    }
    public void testAssertTrueOK() throws Throwable {
        runTestFromString(TEST3, 0, new JUnitAssertionsShouldIncludeMessageRule());
    }
    public void testAssertTrueBad() throws Throwable {
        runTestFromString(TEST4, 1, new JUnitAssertionsShouldIncludeMessageRule());
    }
    public void testAssertNullOK() throws Throwable {
        runTestFromString(TEST5, 0, new JUnitAssertionsShouldIncludeMessageRule());
    }
    public void testAssertNullBad() throws Throwable {
        runTestFromString(TEST6, 1, new JUnitAssertionsShouldIncludeMessageRule());
    }
    public void testAssertSameOK() throws Throwable {
        runTestFromString(TEST7, 0, new JUnitAssertionsShouldIncludeMessageRule());
    }
    public void testAssertSameBad() throws Throwable {
        runTestFromString(TEST8, 1, new JUnitAssertionsShouldIncludeMessageRule());
    }
    public void testAssertNotNullOK() throws Throwable {
        runTestFromString(TEST9, 0, new JUnitAssertionsShouldIncludeMessageRule());
    }
    public void testAssertNotNullBad() throws Throwable {
        runTestFromString(TEST10, 1, new JUnitAssertionsShouldIncludeMessageRule());
    }
    public void testFindBug() throws Throwable {
        runTestFromString(TEST11, 0, new JUnitAssertionsShouldIncludeMessageRule());
    }

    private static final String TEST1 =
    "public class JUnitAssertionsShouldIncludeMessageRule1 {" + PMD.EOL +
    " public void test1() {" + PMD.EOL +
    "  assertEquals(\"1 == 1\", 1, 1);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class JUnitAssertionsShouldIncludeMessageRule2 {" + PMD.EOL +
    " public void test1() {" + PMD.EOL +
    "  assertEquals(1, 1);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class JUnitAssertionsShouldIncludeMessageRule3 {" + PMD.EOL +
    " public void test1() {" + PMD.EOL +
    "  assertTrue(\"foo\", \"foo\".equals(\"foo\"));" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class JUnitAssertionsShouldIncludeMessageRule4 {" + PMD.EOL +
    " public void test1() {" + PMD.EOL +
    "  assertTrue(\"foo\".equals(\"foo\"));" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class JUnitAssertionsShouldIncludeMessageRule5 {" + PMD.EOL +
    " public void test1() {" + PMD.EOL +
    "  assertNull(\"it's not null\", null);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public class JUnitAssertionsShouldIncludeMessageRule6 {" + PMD.EOL +
    " public void test1() {" + PMD.EOL +
    "  assertNull(null);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST7 =
    "public class JUnitAssertionsShouldIncludeMessageRule7 {" + PMD.EOL +
    " public void test1() {" + PMD.EOL +
    "  assertSame(\"not same!\", null, null);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST8 =
    "public class JUnitAssertionsShouldIncludeMessageRule8 {" + PMD.EOL +
    " public void test1() {" + PMD.EOL +
    "  assertSame(null, null);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST9 =
    "public class JUnitAssertionsShouldIncludeMessageRule9 {" + PMD.EOL +
    " public void test1() {" + PMD.EOL +
    "  assertNotNull(\"foo\", null);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST10 =
    "public class JUnitAssertionsShouldIncludeMessageRule10 {" + PMD.EOL +
    " public void test1() {" + PMD.EOL +
    "  assertNotNull(null);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST11 =
    "public class JUnitAssertionsShouldIncludeMessageRule11 {" + PMD.EOL +
    " public void test1() {" + PMD.EOL +
    "  this.test1(\"foo\");" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
