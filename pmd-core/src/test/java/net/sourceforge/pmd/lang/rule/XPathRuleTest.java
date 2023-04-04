/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import static net.sourceforge.pmd.PmdCoreTestUtils.setDummyLanguage;
import static net.sourceforge.pmd.ReportTestUtil.getReportForRuleApply;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import net.sourceforge.pmd.DummyParsingHelper;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.ast.DummyNodeWithDeprecatedAttribute;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.document.TextRegion;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class XPathRuleTest {

    @RegisterExtension
    private final DummyParsingHelper helper = new DummyParsingHelper();

    @Test
    void testAttributeDeprecation10() throws Exception {
        testDeprecation(XPathVersion.XPATH_1_0);
    }

    @Test
    void testAttributeDeprecation20() throws Exception {
        testDeprecation(XPathVersion.XPATH_2_0);
    }


    void testDeprecation(XPathVersion version) throws Exception {
        XPathRule xpr = makeRule(version, "SomeRule");

        DummyNode firstNode = newNode();

        String log = SystemLambda.tapSystemErrAndOut(() -> {
            // with another rule forked from the same one (in multithreaded processor)
            Report report = getReportForRuleApply(xpr, firstNode);
            assertEquals(1, report.getViolations().size());
        });
        assertThat(log, Matchers.containsString("Use of deprecated attribute 'dummyNode/@Size' by XPath rule 'SomeRule'"));
        assertThat(log, Matchers.containsString("Use of deprecated attribute 'dummyNode/@Name' by XPath rule 'SomeRule', please use @Image instead"));


        log = SystemLambda.tapSystemErrAndOut(() -> {
            // with another node
            Report report = getReportForRuleApply(xpr, newNode());
            assertEquals(1, report.getViolations().size());
        });
        assertEquals("", log); // no additional warnings


        log = SystemLambda.tapSystemErrAndOut(() -> {
            // with another rule forked from the same one (in multithreaded processor)
            Report report = getReportForRuleApply(xpr.deepCopy(), newNode());
            assertEquals(1, report.getViolations().size());
        });
        assertEquals("", log); // no additional warnings

        // with another rule on the same node, new warnings
        XPathRule otherRule = makeRule(version, "OtherRule");
        otherRule.setRuleSetName("rset.xml");

        log = SystemLambda.tapSystemErrAndOut(() -> {
            Report report = getReportForRuleApply(otherRule, firstNode);
            assertEquals(1, report.getViolations().size());
        });
        assertThat(log, Matchers.containsString("Use of deprecated attribute 'dummyNode/@Size' by XPath rule 'OtherRule' (in ruleset 'rset.xml')"));
        assertThat(log, Matchers.containsString("Use of deprecated attribute 'dummyNode/@Name' by XPath rule 'OtherRule' (in ruleset 'rset.xml'), please use @Image instead"));
    }

    XPathRule makeRule(XPathVersion version, String name) {
        XPathRule xpr = new XPathRule(version, "//dummyNode[@Size >= 2 and @Name='foo']");
        xpr.setName(name);
        setDummyLanguage(xpr);
        xpr.setMessage("gotcha");
        return xpr;
    }


    XPathRule makeXPath(String xpathExpr) {
        XPathRule xpr = new XPathRule(XPathVersion.XPATH_2_0, xpathExpr);
        setDummyLanguage(xpr);
        xpr.setName("name");
        xpr.setMessage("gotcha");
        return xpr;
    }

    @Test
    void testFileNameInXpath() {
        Report report = executeRule(makeXPath("//*[pmd:fileName() = 'Foo.cls']"),
                                    newRoot("src/Foo.cls"));

        assertThat(report.getViolations(), hasSize(1));
    }

    @Test
    void testBeginLine() {
        Report report = executeRule(makeXPath("//*[pmd:startLine(.)=1]"),
                                    newRoot("src/Foo.cls"));

        assertThat(report.getViolations(), hasSize(1));
    }

    @Test
    void testBeginCol() {
        Report report = executeRule(makeXPath("//*[pmd:startColumn(.)=1]"),
                                    newRoot("src/Foo.cls"));

        assertThat(report.getViolations(), hasSize(1));
    }

    @Test
    void testEndLine() {
        Report report = executeRule(makeXPath("//*[pmd:endLine(.)=1]"),
                                    newRoot("src/Foo.cls"));

        assertThat(report.getViolations(), hasSize(1));
    }

    @Test
    void testEndColumn() {
        Report report = executeRule(makeXPath("//*[pmd:endColumn(.)>1]"),
                                    newRoot("src/Foo.cls"));

        assertThat(report.getViolations(), hasSize(1));
    }

    Report executeRule(net.sourceforge.pmd.Rule rule, DummyNode node) {
        return getReportForRuleApply(rule, node);
    }


    DummyRootNode newNode() {
        DummyRootNode root = newRoot(TextFile.UNKNOWN_FILENAME);
        DummyNode dummy = new DummyNodeWithDeprecatedAttribute();
        root.addChild(dummy, 0);
        dummy.setRegion(TextRegion.fromOffsetLength(0, 1));
        return root;
    }

    public DummyRootNode newRoot(String fileName) {
        return helper.parse("dummy code", fileName);
    }


}
