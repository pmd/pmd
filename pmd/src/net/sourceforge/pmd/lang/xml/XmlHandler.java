/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.xml;

import java.io.Writer;

import net.sf.saxon.sxpath.IndependentContext;
import net.sourceforge.pmd.lang.AbstractLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.VisitorStarter;
import net.sourceforge.pmd.lang.XPathHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.DocumentNavigator;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.lang.xml.ast.DumpFacade;
import net.sourceforge.pmd.lang.xml.ast.XmlNode;
import net.sourceforge.pmd.lang.xml.rule.XmlRuleViolationFactory;

import org.jaxen.Navigator;

/**
 * Implementation of LanguageVersionHandler for the XML.
 */
public class XmlHandler extends AbstractLanguageVersionHandler {

    @Override
    public XPathHandler getXPathHandler() {
	return new XPathHandler() {
	    public void initialize() {
	    }

	    public void initialize(IndependentContext context) {
	    }

	    public Navigator getNavigator() {
		return new DocumentNavigator();
	    }
	};
    }

    public RuleViolationFactory getRuleViolationFactory() {
	return XmlRuleViolationFactory.INSTANCE;
    }

    @Override
    public ParserOptions getDefaultParserOptions() {
	return new XmlParserOptions();
    }

    public Parser getParser(ParserOptions parserOptions) {
	return new XmlParser(parserOptions);
    }

    @Override
    public VisitorStarter getDumpFacade(final Writer writer, final String prefix, final boolean recurse) {
	return new VisitorStarter() {
	    public void start(Node rootNode) {
		new DumpFacade().initializeWith(writer, prefix, recurse, (XmlNode) rootNode);
	    }
	};
    }
}
