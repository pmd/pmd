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
import net.sourceforge.pmd.rules.UnusedImportsRule;

public class UnusedImportsRuleTest extends SimpleAggregatorTst {

    private UnusedImportsRule rule;

    public void setUp() {
        rule = new UnusedImportsRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple unused single type import", 1, rule),
           new TestDescriptor(TEST2, "one used single type import", 0, rule),
           new TestDescriptor(TEST3, "2 unused single-type imports", 2, rule),
           new TestDescriptor(TEST4, "1 used single type import", 0, rule)
       });
    }

    private static final String TEST1 =
    "import java.io.File;" + PMD.EOL +
    "public class UnusedImports1 {}";

    private static final String TEST2 =
    "import java.io.File;" + PMD.EOL +
    "public class UnusedImports2 {" + PMD.EOL +
    " private File file;" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "import java.io.File;" + PMD.EOL +
    "import java.util.List;" + PMD.EOL +
    "public class UnusedImports3 {" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "import java.security.AccessController;" + PMD.EOL +
    "public class UnusedImports4 {" + PMD.EOL +
    " public void foo() {" + PMD.EOL +
    "  AccessController.doPrivileged(null);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";


}
