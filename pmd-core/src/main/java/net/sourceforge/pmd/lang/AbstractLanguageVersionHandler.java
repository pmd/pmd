/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.io.Writer;

import net.sourceforge.pmd.lang.dfa.DFAGraphRule;

/**
 * This is a generic implementation of the LanguageVersionHandler interface.
 *
 * @see LanguageVersionHandler
 */
public abstract class AbstractLanguageVersionHandler implements LanguageVersionHandler {

    @Override
    public DataFlowHandler getDataFlowHandler() {
        return DataFlowHandler.DUMMY;
    }

    @Override
    public XPathHandler getXPathHandler() {
        return XPathHandler.DUMMY;
    }

    @Override
    public ParserOptions getDefaultParserOptions() {
        return new ParserOptions();
    }

    @Override
    public VisitorStarter getDataFlowFacade() {
        return VisitorStarter.DUMMY;
    }

    @Override
    public VisitorStarter getSymbolFacade() {
        return VisitorStarter.DUMMY;
    }

    @Override
    public VisitorStarter getSymbolFacade(ClassLoader classLoader) {
        return VisitorStarter.DUMMY;
    }

    @Override
    public VisitorStarter getTypeResolutionFacade(ClassLoader classLoader) {
        return VisitorStarter.DUMMY;
    }

    @Override
    public VisitorStarter getDumpFacade(final Writer writer, final String prefix, final boolean recurse) {
        return VisitorStarter.DUMMY;
    }

    @Override
    public VisitorStarter getMultifileFacade() {
        return VisitorStarter.DUMMY;
    }

    @Override
    public DFAGraphRule getDFAGraphRule() {
        return null;
    }
}
