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
import net.sourceforge.pmd.rules.StringToStringRule;

public class StringToStringRuleTest extends SimpleAggregatorTst {

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "local var", 1, new StringToStringRule()),
           new TestDescriptor(TEST2, "parameter", 1, new StringToStringRule()),
           new TestDescriptor(TEST3, "field", 1, new StringToStringRule()),
           new TestDescriptor(TEST4, "primitive", 0, new StringToStringRule()),
           new TestDescriptor(TEST5, "multiple similar params", 0, new StringToStringRule()),
           new TestDescriptor(TEST6, "string array", 1, new StringToStringRule())
       });
    }

   private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " private String baz() {" + PMD.EOL +
    "  String bar = \"howdy\";" + PMD.EOL +
    "  return bar.toString();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " private String baz(String bar) {" + PMD.EOL +
    "  return bar.toString();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " private String baz;" + PMD.EOL +
    " private int getBaz() {" + PMD.EOL +
    "  return baz.toString();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " private int baz;" + PMD.EOL +
    " private int getBaz() {" + PMD.EOL +
    "  return baz;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    " private String getBaz(String foo, StringBuffer buffer) {" + PMD.EOL +
    "  return buffer.toString();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public class Foo {" + PMD.EOL +
    " private String getBaz() {" + PMD.EOL +
    "  String[] foo = {\"hi\"};" + PMD.EOL +
    "  return foo[0].toString();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
