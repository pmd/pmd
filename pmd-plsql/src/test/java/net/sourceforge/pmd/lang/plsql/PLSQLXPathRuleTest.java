/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;

/**
 * Tests to use XPath rules with PLSQL.
 */
class PLSQLXPathRuleTest extends AbstractPLSQLParserTst {

    private static final String SOURCE =
        "create or replace\n" + "package pkg_xpath_problem\n" + "AS\n" + "    PROCEDURE pkg_minimal\n" + "    IS\n"
            + "        a_variable VARCHAR2(1);\n" + "    BEGIN \n" + "        --PRAGMA INLINE(output,'YES');\n"
            + "        a_variable := 'Y' ;\n" + "    END ;\n" + "end pkg_xpath_problem;\n" + "/\n";

    /**
     * See https://sourceforge.net/p/pmd/bugs/1166/
     */
    @Test
    void testXPathRule1() {
        testOnVersion(XPathVersion.XPATH_1_0);
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1166/
     */
    @Test
    void testXPathRule1Compatibility() {
        testOnVersion(XPathVersion.XPATH_1_0_COMPATIBILITY);
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1166/
     */
    @Test
    void testXPathRule2() {
        testOnVersion(XPathVersion.XPATH_2_0);
    }


    private void testOnVersion(XPathVersion xpath10) {
        XPathRule rule = plsql.newXpathRule("//PrimaryPrefix", xpath10);
        Report report = plsql.executeRule(rule, SOURCE);
        assertEquals(2, report.getViolations().size());
    }

}
