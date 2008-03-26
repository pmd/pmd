package net.sourceforge.pmd.sourcetypehandlers;

import net.sourceforge.pmd.parsers.JspParser;
import net.sourceforge.pmd.parsers.Parser;
import net.sourceforge.pmd.symboltable.JspSymbolFacade;

/**
 * Implementation of SourceTypeHandler for the JSP parser.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class JspTypeHandler implements SourceTypeHandler {

    public Parser getParser() {
	return new JspParser();
    }

    public VisitorStarter getDataFlowFacade() {
	return VisitorStarter.dummy;
    }

    public VisitorStarter getSymbolFacade() {
	return new JspSymbolFacade();
    }

    public VisitorStarter getTypeResolutionFacade(ClassLoader classLoader) {
	return VisitorStarter.dummy;
    }
}
