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
import net.sourceforge.pmd.rules.DuplicateImportsRule;

public class DuplicateImportsRuleTest extends SimpleAggregatorTst {

    private DuplicateImportsRule rule;

    public void setUp() {
        rule = new DuplicateImportsRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple failure", 1, rule),
           new TestDescriptor(TEST2, "", 1, rule),
           new TestDescriptor(TEST3, "", 1, rule),
           new TestDescriptor(TEST4, "", 0, rule),
       });
    }

    private static final String TEST1 =
    "import java.io.File;" + PMD.EOL +
    "import java.util.*;" + PMD.EOL +
    "import java.io.File;" + PMD.EOL +
    "public class DuplicateImports {}";

    private static final String TEST2 =
    "import java.io.*;" + PMD.EOL +
    "import java.io.*;" + PMD.EOL +
    "public class DuplicateImports2 {}";

    private static final String TEST3 =
    "import java.util.*;" + PMD.EOL +
    "import java.net.*;" + PMD.EOL +
    "import java.io.*;" + PMD.EOL +
    "import java.io.File;" + PMD.EOL +
    "public class DuplicateImports3 {}";

    private static final String TEST4 =
    "import javax.servlet.*;" + PMD.EOL +
    "import javax.servlet.http.*;" + PMD.EOL +
    "public class DuplicateImports4 {}";

}
