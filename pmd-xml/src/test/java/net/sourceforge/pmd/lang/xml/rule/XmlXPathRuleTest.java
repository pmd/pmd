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

}
