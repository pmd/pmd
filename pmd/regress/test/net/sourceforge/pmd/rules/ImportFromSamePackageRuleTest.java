package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.cpd.CPD;
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
    "package foo;" + CPD.EOL +
    "import foo.Bar;" + CPD.EOL +
    "public class ImportFromSamePackage1{}";

    private static final String TEST2 =
    "package foo;" + CPD.EOL +
    "import foo.buz.Bar;" + CPD.EOL +
    "public class ImportFromSamePackage2{}";

    private static final String TEST3 =
    "import java.util.*;" + CPD.EOL +
    "public class ImportFromSamePackage3{}";

    private static final String TEST4 =
    "package bar;" + CPD.EOL +
    "import Foo;" + CPD.EOL +
    "public class ImportFromSamePackage4{}";

    private static final String TEST5 =
    "import Foo;" + CPD.EOL +
    "public class ImportFromSamePackage5{}";

    private static final String TEST6 =
    "package foo.bar;" + CPD.EOL +
    "import foo.bar.baz.*;" + CPD.EOL +
    "public class ImportFromSamePackage6{}";

}
