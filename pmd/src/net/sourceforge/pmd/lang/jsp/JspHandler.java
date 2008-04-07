package net.sourceforge.pmd.lang.jsp;

import java.io.Writer;

import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.VisitorStarter;
import net.sourceforge.pmd.lang.XPathFunctionRegister;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.jsp.ast.DumpFacade;
import net.sourceforge.pmd.lang.jsp.ast.JspNode;
import net.sourceforge.pmd.lang.jsp.rule.JspRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.symboltable.JspSymbolFacade;

/**
 * Implementation of LanguageVersionHandler for the JSP parser.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class JspHandler implements LanguageVersionHandler {

    public XPathFunctionRegister getXPathFunctionRegister() {
	return XPathFunctionRegister.DUMMY;
    }

    public RuleViolationFactory getRuleViolationFactory() {
	return JspRuleViolationFactory.INSTANCE;
    }

    public Parser getParser() {
	return new JspParser();
    }

    public VisitorStarter getDataFlowFacade() {
	return VisitorStarter.DUMMY;
    }

    public VisitorStarter getSymbolFacade() {
	return new JspSymbolFacade();
    }

    public VisitorStarter getTypeResolutionFacade(ClassLoader classLoader) {
	return VisitorStarter.DUMMY;
    }

    public VisitorStarter getDumpFacade(final Writer writer, final String prefix, final boolean recurse) {
	return new VisitorStarter() {
	    public void start(Node rootNode) {
		new DumpFacade().initializeWith(writer, prefix, recurse, (JspNode) rootNode);
	    }
	};
    }
}
