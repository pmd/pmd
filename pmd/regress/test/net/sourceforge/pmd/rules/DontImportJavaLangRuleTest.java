/*
 * User: tom
 * Date: Jul 15, 2002
 * Time: 8:46:03 AM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.rules.DontImportJavaLangRule;

public class DontImportJavaLangRuleTest extends RuleTst {

    public DontImportJavaLangRuleTest(String name) {
        super(name);
    }

    public void test1() throws Throwable {
        Report report = process("DontImportJavaLang1.java", new DontImportJavaLangRule());
        assertEquals(1, report.size());
    }

    public void test2() throws Throwable {
        Report report = process("DontImportJavaLang2.java", new DontImportJavaLangRule());
        assertEquals(1, report.size());
    }

    public void test3() throws Throwable {
        Report report = process("DontImportJavaLang3.java", new DontImportJavaLangRule());
        assertTrue(report.isEmpty());
    }
}
