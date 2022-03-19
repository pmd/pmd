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

    private static final String A_URI = "http://soap.sforce.com/2006/04/metadata";
    final XmlParsingHelper xml = XmlParsingHelper.XML;

    private Rule makeXPath(String expression) {
        return makeXPath(expression, "");
    }

    private Rule makeXPath(String expression, String nsUri) {
        DomXPathRule rule = new DomXPathRule(expression, nsUri);
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
    public void testRootNodeWildcardUri() {
        // https://github.com/pmd/pmd/issues/3413#issuecomment-1072614398
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
    public void testNamespaceDescendantWrongDefaultUri() {
        Report report = xml.executeRule(makeXPath("//a"),
                                        "<Flow xmlns='" + A_URI + "'><a/></Flow>");

        assertSize(report, 0);
    }

    @Test
    public void testNamespaceDescendantOkUri() {
        Report report = xml.executeRule(makeXPath("//a", A_URI),
                                        "<Flow xmlns='" + A_URI + "'><a/></Flow>");

        assertSize(report, 1);

        report = xml.executeRule(makeXPath("//*:a"),
                                 "<Flow xmlns='" + A_URI + "'><a/></Flow>");

        assertSize(report, 1);
    }

    @Test
    public void testNamespaceDescendantWildcardUri() {
        Report report = xml.executeRule(makeXPath("//*:a"),
                                        "<Flow xmlns='" + A_URI + "'><a/></Flow>");

        assertSize(report, 1);
    }

    @Test
    public void testNamespacePrefixDescendantWildcardUri() {
        Report report = xml.executeRule(makeXPath("//*:Flow"),
                                        "<my:Flow xmlns:my='" + A_URI + "'><a/></my:Flow>");

        assertSize(report, 1);
    }

    @Test
    public void testNamespacePrefixDescendantOkUri() {
        Report report = xml.executeRule(makeXPath("//Flow", A_URI),
                                        "<my:Flow xmlns:my='" + A_URI + "'><a/></my:Flow>");

        assertSize(report, 1);
    }

    @Test
    public void testNamespacePrefixDescendantWrongUri() {
        Report report = xml.executeRule(makeXPath("//Flow", "wrongURI"),
                                        "<my:Flow xmlns:my='" + A_URI + "'><a/></my:Flow>");

        assertSize(report, 0);
    }

    @Test
    public void testLocationFuns() {
        Rule rule = makeXPath("//Flow[pmd:beginLine(.) != pmd:endLine(.)]");
        Report report = xml.executeRule(rule, "<Flow><a/></Flow>");
        assertSize(report, 0);
        report = xml.executeRule(rule, "<Flow>\n<a/>\n</Flow>");
        assertSize(report, 1);
    }

}
