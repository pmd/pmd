/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;

/**
 * Tests to use XPath rules with PLSQL.
 */
public class PLSQLXPathRuleTest extends AbstractPLSQLParserTst {

    private final String source =
        "create or replace\n" + "package pkg_xpath_problem\n" + "AS\n" + "    PROCEDURE pkg_minimal\n" + "    IS\n"
            + "        a_variable VARCHAR2(1);\n" + "    BEGIN \n" + "        --PRAGMA INLINE(output,'YES');\n"
            + "        a_variable := 'Y' ;\n" + "    END ;\n" + "end pkg_xpath_problem;\n" + "/\n";

    public PLSQLXPathRuleTest() {
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1166/
     */
    @Test
    public void testXPathRule1() throws Exception {
        testOnVersion(XPathVersion.XPATH_1_0);
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1166/
     */
    @Test
    public void testXPathRule1Compatibility() throws Exception {
        testOnVersion(XPathVersion.XPATH_1_0_COMPATIBILITY);
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1166/
     */
    @Test
    public void testXPathRule2() throws Exception {
        testOnVersion(XPathVersion.XPATH_2_0);
    }


    private void testOnVersion(XPathVersion xpath10) throws Exception {
        XPathRule rule = new XPathRule(xpath10, "//PrimaryPrefix");
        rule.setLanguage(LanguageRegistry.getLanguage(PLSQLLanguageModule.NAME));
        rule.setMessage("Test Violation");

        Report report = plsql.executeRule(rule, source);
        Assert.assertEquals(2, report.getViolations().size());
    }


}
