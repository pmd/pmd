/*
 * User: tom
 * Date: Nov 20, 2002
 * Time: 1:59:51 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.ImportFromSamePackageRule;

public class ImportFromSamePackageRuleTest extends RuleTst {

    public void testSimple() throws Throwable {
        runTestFromFile("ImportFromSamePackage1.java", 1, new ImportFromSamePackageRule());
    }

    public void testImportingFromSubPackage() throws Throwable {
        runTestFromFile("ImportFromSamePackage2.java", 0, new ImportFromSamePackageRule());
    }

    public void testClassInDefaultPackageImportingFromOtherPackage() throws Throwable {
        runTestFromFile("ImportFromSamePackage3.java", 0, new ImportFromSamePackageRule());
    }

    public void testClassNotInDefaultPackageImportingFromDefaultPackage() throws Throwable {
        runTestFromFile("ImportFromSamePackage4.java", 0, new ImportFromSamePackageRule());
    }

    public void testClassInDefaultPackageImportingFromDefaultPackage() throws Throwable {
        runTestFromFile("ImportFromSamePackage5.java", 1, new ImportFromSamePackageRule());
    }
}
