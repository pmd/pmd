/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.XPathHandler;
import net.sourceforge.pmd.lang.ast.xpath.DefaultASTXPathHandler;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.lang.xml.rule.XmlRuleViolationFactory;

/**
 * Implementation of LanguageVersionHandler for the XML.
 */
public class XmlHandler extends AbstractPmdLanguageVersionHandler {

    @Override
    public XPathHandler getXPathHandler() {
        return new DefaultASTXPathHandler();
    }

    @Override
    public RuleViolationFactory getRuleViolationFactory() {
        return XmlRuleViolationFactory.INSTANCE;
    }

    @Override
    public ParserOptions getDefaultParserOptions() {
        return new XmlParserOptions();
    }

    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new XmlParser(parserOptions);
    }

}
