/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript;

import java.io.Writer;

import net.sf.saxon.sxpath.IndependentContext;
import net.sourceforge.pmd.lang.DataFlowHandler;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.VisitorStarter;
import net.sourceforge.pmd.lang.XPathHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.AbstractASTXPathHandler;
import net.sourceforge.pmd.lang.ecmascript.ast.DumpFacade;
import net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptNode;
import net.sourceforge.pmd.lang.ecmascript.rule.EcmascriptRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

/**
 * Implementation of LanguageVersionHandler for the ECMAScript Version 3.
 */
public class Ecmascript3Handler implements LanguageVersionHandler {

    public DataFlowHandler getDataFlowHandler() {
	return DataFlowHandler.DUMMY;
    }

    public XPathHandler getXPathHandler() {
	return new AbstractASTXPathHandler() {
	    public void initialize() {
	    }

	    public void initialize(IndependentContext context) {
	    }
	};
    }

    public RuleViolationFactory getRuleViolationFactory() {
	return EcmascriptRuleViolationFactory.INSTANCE;
    }

    public Parser getParser() {
	return new Ecmascript3Parser();
    }

    public VisitorStarter getDataFlowFacade() {
	return VisitorStarter.DUMMY;
    }

    public VisitorStarter getSymbolFacade() {
	return VisitorStarter.DUMMY;
    }

    public VisitorStarter getTypeResolutionFacade(ClassLoader classLoader) {
	return VisitorStarter.DUMMY;
    }

    public VisitorStarter getDumpFacade(final Writer writer, final String prefix, final boolean recurse) {
	return new VisitorStarter() {
	    public void start(Node rootNode) {
		new DumpFacade().initializeWith(writer, prefix, recurse, (EcmascriptNode) rootNode);
	    }
	};
    }
}
