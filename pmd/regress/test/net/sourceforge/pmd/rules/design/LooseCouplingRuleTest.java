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
import net.sourceforge.pmd.rules.design.LooseCouplingRule;
import test.net.sourceforge.pmd.rules.SimpleAggregatorTst;
import test.net.sourceforge.pmd.rules.TestDescriptor;

public class LooseCouplingRuleTest extends SimpleAggregatorTst {

    private LooseCouplingRule rule;

    public void setUp() {
        rule = new LooseCouplingRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "", 1, rule),
           new TestDescriptor(TEST2, "", 0, rule),
           new TestDescriptor(TEST3, "", 0, rule),
           new TestDescriptor(TEST4, "", 0, rule),
           new TestDescriptor(TEST5, "", 1, rule),
           new TestDescriptor(TEST6, "", 2, rule),
           new TestDescriptor(TEST7, "", 2, rule),
           new TestDescriptor(TEST8, "", 1, rule),
           new TestDescriptor(TEST9, "Vector could be List", 1, rule),
       });
    }

    private static final String TEST1 =
    "import java.util.*;" + PMD.EOL +
    "public class Foo {" + PMD.EOL +
    " public HashSet getFoo() {" + PMD.EOL +
    "  return new HashSet();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "import java.util.*;" + PMD.EOL +
    "public class Foo {" + PMD.EOL +
    " public Map getFoo() {" + PMD.EOL +
    "  return new HashMap();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " public void foo() {}" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "import java.util.*;" + PMD.EOL +
    "public class Foo {" + PMD.EOL +
    " private Set fooSet = new HashSet(); // OK" + PMD.EOL +
    " public Set getFoo() {" + PMD.EOL +
    "  return fooSet;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "import java.util.*;" + PMD.EOL +
    "public class Foo {" + PMD.EOL +
    " private HashSet fooSet = new HashSet(); // NOT OK" + PMD.EOL +
    " public Set getFoo() {" + PMD.EOL +
    "  return fooSet;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "import java.util.*;" + PMD.EOL +
    "public class Foo {" + PMD.EOL +
    " private HashSet fooSet = new HashSet(); // NOT OK" + PMD.EOL +
    " public HashSet getFoo() { // NOT OK" + PMD.EOL +
    "  return fooSet;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST7 =
    "import java.util.*;" + PMD.EOL +
    "public class Foo {" + PMD.EOL +
    " private HashSet fooSet = new HashSet();" + PMD.EOL +
    " private HashMap fooMap = new HashMap();" + PMD.EOL +
    "}";

    private static final String TEST8 =
    "import java.util.*;" + PMD.EOL +
    "public class Foo {" + PMD.EOL +
    " public void foo(HashMap bar) {}" + PMD.EOL +
    "}";

    private static final String TEST9 =
    "import java.util.*;" + PMD.EOL +
    "public class Foo {" + PMD.EOL +
    " public void foo(Vector bar) {}" + PMD.EOL +
    "}";

}
