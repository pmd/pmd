/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang;

import java.io.Writer;

import net.sourceforge.pmd.lang.dfa.DFAGraphRule;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

/**
 * Interface for obtaining the classes necessary for checking source files
 * of a specific language.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public interface LanguageVersionHandler {

    /**
     * Get the DataFlowHandler.
     */
    DataFlowHandler getDataFlowHandler();
    
    /**
     * Get the XPathHandler.
     */
    XPathHandler getXPathHandler();

    /**
     * Get the RuleViolationFactory.
     */
    RuleViolationFactory getRuleViolationFactory();
    
    /**
     * Get the default ParserOptions.
     * @return ParserOptions
     */
    ParserOptions getDefaultParserOptions();

    /**
     * Get the Parser.
     *
     * @return Parser
     */
    Parser getParser(ParserOptions parserOptions);

    /**
     * Get the DataFlowFacade.
     *
     * @return VisitorStarter
     */
    VisitorStarter getDataFlowFacade();

    /**
     * Get the SymbolFacade.
     *
     * @return VisitorStarter 
     */
    VisitorStarter getSymbolFacade();

    /**
     * Get the SymbolFacade.
     * @param classLoader A ClassLoader to use for resolving Types.
     * @return VisitorStarter
     */
    VisitorStarter getSymbolFacade(ClassLoader classLoader);

    /**
     * Get the TypeResolutionFacade.
     *
     * @param classLoader A ClassLoader to use for resolving Types.
     * @return VisitorStarter 
     */
    VisitorStarter getTypeResolutionFacade(ClassLoader classLoader);

    /**
     * Get the DumpFacade.
     *
     * @param writer The writer to dump to.
     * @return VisitorStarter 
     */
    VisitorStarter getDumpFacade(Writer writer, String prefix, boolean recurse);

    DFAGraphRule getDFAGraphRule();
}
