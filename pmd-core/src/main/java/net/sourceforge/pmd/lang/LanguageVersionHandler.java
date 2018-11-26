/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.io.Writer;
import java.util.List;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.AstProcessingStage;
import net.sourceforge.pmd.lang.dfa.DFAGraphRule;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;


/**
 * Interface for obtaining the classes necessary for checking source files of a
 * specific language.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public interface LanguageVersionHandler {


    /**
     * Get the XPathHandler.
     */
    XPathHandler getXPathHandler();


    /**
     * Returns the list of all supported optional processing stages.
     *
     * @return A list of all optional processing stages.
     */
    @Experimental
    List<? extends AstProcessingStage<?>> getProcessingStages();


    /**
     * Get the default ParserOptions.
     *
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
     * Get the RuleViolationFactory.
     */
    RuleViolationFactory getRuleViolationFactory();


    /**
     * Get the DumpFacade.
     *
     * @param writer The writer to dump to.
     *
     * @return VisitorStarter
     */
    // TODO should we deprecate? Not much use to it.
    // Plus if it's not implemented, then it does nothing to the writer which is unexpected.
    VisitorStarter getDumpFacade(Writer writer, String prefix, boolean recurse);


    /**
     * Get the DataFlowHandler.
     */
    @Deprecated
    DataFlowHandler getDataFlowHandler();


    /**
     * Get the DataFlowFacade.
     *
     * @return VisitorStarter
     */
    @Deprecated
    VisitorStarter getDataFlowFacade();


    /**
     * Get the SymbolFacade.
     *
     * @return VisitorStarter
     */
    @Deprecated
    VisitorStarter getSymbolFacade();


    /**
     * Get the SymbolFacade.
     *
     * @param classLoader A ClassLoader to use for resolving Types.
     *
     * @return VisitorStarter
     */
    @Deprecated
    VisitorStarter getSymbolFacade(ClassLoader classLoader);


    /**
     * Get the TypeResolutionFacade.
     *
     * @param classLoader A ClassLoader to use for resolving Types.
     *
     * @return VisitorStarter
     */
    @Deprecated
    VisitorStarter getTypeResolutionFacade(ClassLoader classLoader);


    /**
     * Gets the visitor that performs multifile data gathering.
     *
     * @return The visitor starter
     */
    @Deprecated
    VisitorStarter getMultifileFacade();


    /**
     * Gets the visitor that populates the qualified names of the
     * nodes.
     *
     * @param classLoader The classloader to use to resolve the types of type qualified names
     *
     * @return The visitor starter
     */
    @Deprecated
    VisitorStarter getQualifiedNameResolutionFacade(ClassLoader classLoader);


    @Deprecated
    DFAGraphRule getDFAGraphRule();
}
