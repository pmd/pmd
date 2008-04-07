package net.sourceforge.pmd.lang.cpp;

import java.io.Writer;

import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.VisitorStarter;
import net.sourceforge.pmd.lang.XPathFunctionRegister;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

/**
 * Implementation of LanguageVersionHandler for the C++ Language.
 */
public class CppHandler implements LanguageVersionHandler {

    public XPathFunctionRegister getXPathFunctionRegister() {
	return XPathFunctionRegister.DUMMY;
    }

    public RuleViolationFactory getRuleViolationFactory() {
	throw new UnsupportedOperationException("getRuleViolationFactory() is not supported for C++");
    }

    public Parser getParser() {
	return new CppParser();
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
	return VisitorStarter.DUMMY;
    }
}
