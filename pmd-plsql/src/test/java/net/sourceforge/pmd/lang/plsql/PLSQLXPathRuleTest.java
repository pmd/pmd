/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.rule.xpath.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.reporting.Report;

/**
 * Tests to use XPath rules with PLSQL.
 */
class PLSQLXPathRuleTest extends AbstractPLSQLParserTst {

    private static final String SOURCE =
        """
        create or replace
        package pkg_xpath_problem
        AS
            PROCEDURE pkg_minimal
            IS
                a_variable VARCHAR2(1);
            BEGIN\s
                --PRAGMA INLINE(output,'YES');
                a_variable := 'Y' ;
            END ;
        end pkg_xpath_problem;
        /
        """;

    @Test
    void testXPathRule() {
        testOnVersion(XPathVersion.DEFAULT);
    }


    private void testOnVersion(XPathVersion xpath10) {
        XPathRule rule = plsql.newXpathRule("//PrimaryPrefix", xpath10);
        Report report = plsql.executeRule(rule, SOURCE);
        assertEquals(2, report.getViolations().size());
    }

}
