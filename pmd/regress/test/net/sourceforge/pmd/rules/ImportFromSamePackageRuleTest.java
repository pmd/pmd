package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.ImportFromSamePackageRule;
import net.sourceforge.pmd.cpd.CPD;

public class ImportFromSamePackageRuleTest extends RuleTst {

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

    public void testSimple() throws Throwable {
        runTestFromString(TEST1, 1, new ImportFromSamePackageRule());
    }
    public void testDefaultPackageImportingFromSubPackage() throws Throwable {
        runTestFromString(TEST2, 0, new ImportFromSamePackageRule());
    }
    public void testClassInDefaultPackageImportingFromOtherPackage() throws Throwable {
        runTestFromString(TEST3, 0, new ImportFromSamePackageRule());
    }
    public void testClassNotInDefaultPackageImportingFromDefaultPackage() throws Throwable {
        runTestFromString(TEST4, 0, new ImportFromSamePackageRule());
    }
    public void testClassInDefaultPackageImportingFromDefaultPackage() throws Throwable {
        runTestFromString(TEST5, 1, new ImportFromSamePackageRule());
    }
    public void testImportingFromSubPackage() throws Throwable {
        runTestFromString(TEST6, 0, new ImportFromSamePackageRule());
    }
}
