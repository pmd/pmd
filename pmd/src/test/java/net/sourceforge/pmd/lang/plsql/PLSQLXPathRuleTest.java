/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql;

import java.util.Arrays;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
import net.sourceforge.pmd.lang.rule.XPathRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests to use XPath rules with PLSQL.
 */
public class PLSQLXPathRuleTest extends AbstractPLSQLParserTst {

    private ASTInput node = parsePLSQL("create or replace\n" + 
            "package pkg_xpath_problem\n" + 
            "AS\n" + 
            "    PROCEDURE pkg_minimal\n" + 
            "    IS\n" + 
            "        a_variable VARCHAR2(1);\n" + 
            "    BEGIN \n" + 
            "        --PRAGMA INLINE(output,'YES');\n" + 
            "        a_variable := 'Y' ;\n" + 
            "    END ;\n" + 
            "end pkg_xpath_problem;\n" + 
            "/\n" + 
            "");

    private RuleContext ctx = new RuleContext();

    @Before
    public void setup() {
        ctx.setLanguageVersion(LanguageVersion.PLSQL);
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1166/
     */
    @Test
    public void testXPathRule1() {
        XPathRule rule = createRule("1.0");

        rule.apply(Arrays.asList(node), ctx);
        Assert.assertEquals(2, ctx.getReport().treeSize());
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1166/
     */
    @Test
    public void testXPathRule1Compatibility() {
        XPathRule rule = createRule("1.0 compatibility");

        rule.apply(Arrays.asList(node), ctx);
        Assert.assertEquals(2, ctx.getReport().treeSize());
    }

    /**
     * See https://sourceforge.net/p/pmd/bugs/1166/
     */
    @Test
    public void testXPathRule2() {
        XPathRule rule = createRule("2.0");

        rule.apply(Arrays.asList(node), ctx);
        Assert.assertEquals(2, ctx.getReport().treeSize());
    }

    private XPathRule createRule(String version) {
        XPathRule rule = new XPathRule("//PrimaryExpression");
        rule.setLanguage(Language.PLSQL);
        rule.setVersion(version);
        rule.setMessage("Test Violation");
        return rule;
    }

}
