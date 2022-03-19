/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.rule;

import static net.sourceforge.pmd.lang.ast.test.TestUtilsKt.assertSize;

import org.junit.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.xml.XmlLanguageModule;
import net.sourceforge.pmd.lang.xml.XmlParsingHelper;

public class XmlXPathRuleTest {

    final XmlParsingHelper xml = XmlParsingHelper.XML;

    private Rule makeXPath(String expression) {
        DomXPathRule rule = new DomXPathRule(expression);
        rule.setLanguage(LanguageRegistry.getLanguage(XmlLanguageModule.NAME));
        rule.setMessage("XPath Rule Failed");
        return rule;
    }


    @Test
    public void testFileNameInXpath() {
        Report report = xml.executeRule(makeXPath("//b[pmd:fileName() = 'Foo.xml']"),
                                        "<a><b></b></a>",
                                        "src/Foo.xml");

        assertSize(report, 1);
    }

    @Test
    public void testTextFunctionInXpath() {
        // https://github.com/pmd/pmd/issues/915
        Report report = xml.executeRule(makeXPath("//app[text()[1]='app2']"),
                                        "<a><app>app2</app></a>");

        assertSize(report, 1);
    }

    @Test
    public void testRootNode() {
        // https://github.com/pmd/pmd/issues/3413#issuecomment-1072614398
        // Note that the test is *:Flow, because Saxon DOM is namespace sensitive, and the xmlns
        // attribute is interpreted as the ns of the document
        Report report = xml.executeRule(makeXPath("/*:Flow"),
                                        "<Flow xmlns=\"http://soap.sforce.com/2006/04/metadata\">\n"
                                        + "</Flow>");

        assertSize(report, 1);
    }

    @Test
    public void testNoNamespaceRoot() {
        Report report = xml.executeRule(makeXPath("/Flow"),
                                        "<Flow>\n"
                                        + "</Flow>");

        assertSize(report, 1);
    }

    @Test
    public void testNamespaceDescendant() {
        Report report = xml.executeRule(makeXPath("//a"),
                                        "<Flow xmlns='http://soap.sforce.com/2006/04/metadata'><a/></Flow>");

        assertSize(report, 1);
    }

}
