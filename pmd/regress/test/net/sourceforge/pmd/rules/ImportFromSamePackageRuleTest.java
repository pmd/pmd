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
import net.sourceforge.pmd.rules.ImportFromSamePackageRule;

public class ImportFromSamePackageRuleTest extends SimpleAggregatorTst {

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "simple failure", 1, new ImportFromSamePackageRule()),
           new TestDescriptor(TEST2, "class in default package importing from sub package", 0, new ImportFromSamePackageRule()),
           new TestDescriptor(TEST3, "class in default package importing from other package", 0, new ImportFromSamePackageRule()),
           new TestDescriptor(TEST4, "class not in default package importing from default package", 0, new ImportFromSamePackageRule()),
           new TestDescriptor(TEST5, "class in default package importing from default package", 1, new ImportFromSamePackageRule()),
           new TestDescriptor(TEST6, "importing from some package", 0, new ImportFromSamePackageRule()),
       });
    }

    private static final String TEST1 =
    "package foo;" + PMD.EOL +
    "import foo.Bar;" + PMD.EOL +
    "public class ImportFromSamePackage1{}";

    private static final String TEST2 =
    "package foo;" + PMD.EOL +
    "import foo.buz.Bar;" + PMD.EOL +
    "public class ImportFromSamePackage2{}";

    private static final String TEST3 =
    "import java.util.*;" + PMD.EOL +
    "public class ImportFromSamePackage3{}";

    private static final String TEST4 =
    "package bar;" + PMD.EOL +
    "import Foo;" + PMD.EOL +
    "public class ImportFromSamePackage4{}";

    private static final String TEST5 =
    "import Foo;" + PMD.EOL +
    "public class ImportFromSamePackage5{}";

    private static final String TEST6 =
    "package foo.bar;" + PMD.EOL +
    "import foo.bar.baz.*;" + PMD.EOL +
    "public class ImportFromSamePackage6{}";

}
