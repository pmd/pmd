/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.io.Writer;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.dfa.DFAGraphRule;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.util.designerbindings.DesignerBindings;
import net.sourceforge.pmd.util.designerbindings.DesignerBindings.DefaultDesignerBindings;

/**
 * Interface for obtaining the classes necessary for checking source files of a
 * specific language.
 *
 * Note: "façade" getters like {@link #getSymbolFacade()} will be removed with 7.0.0
 * and replaced with a more extensible mechanism. They're now deprecated. See also
 * https://github.com/pmd/pmd/pull/1426
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public interface LanguageVersionHandler {

    /**
     * Get the DataFlowHandler.
     * @deprecated This is internal API
     */
    @Deprecated
    @InternalApi
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
     * Get the DataFlowFacade.
     *
     * @return VisitorStarter
     * @deprecated see note in the class description
     */
    @Deprecated
    VisitorStarter getDataFlowFacade();

    /**
     * Get the SymbolFacade.
     *
     * @return VisitorStarter
     * @deprecated see note in the class description
     */
    @Deprecated
    VisitorStarter getSymbolFacade();

    /**
     * Get the SymbolFacade.
     *
     * @param classLoader
     *            A ClassLoader to use for resolving Types.
     * @return VisitorStarter
     * @deprecated see note in the class description
     */
    @Deprecated
    VisitorStarter getSymbolFacade(ClassLoader classLoader);

    /**
     * Get the TypeResolutionFacade.
     *
     * @param classLoader
     *            A ClassLoader to use for resolving Types.
     * @return VisitorStarter
     * @deprecated see note in the class description
     */
    @Deprecated
    VisitorStarter getTypeResolutionFacade(ClassLoader classLoader);

    /**
     * Get the DumpFacade.
     *
     * @param writer
     *            The writer to dump to.
     * @return VisitorStarter
     * @deprecated The dump façade is not that useful and will be completely scrapped with PMD 7.0.0
     */
    @Deprecated
    VisitorStarter getDumpFacade(Writer writer, String prefix, boolean recurse);


    /**
     * Gets the visitor that performs multifile data gathering.
     *
     * @return The visitor starter
     * @deprecated see note in the class description
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
     * @deprecated see note in the class description
     */
    @Deprecated
    VisitorStarter getQualifiedNameResolutionFacade(ClassLoader classLoader);


    /**
     * @deprecated This is internal API
     */
    @Deprecated
    @InternalApi
    DFAGraphRule getDFAGraphRule();


    /**
     * Returns the metrics provider for this language version,
     * or null if it has none.
     *
     * Note: this is experimental, ie unstable until 7.0.0, after
     * which it will probably be promoted to a stable API. For
     * instance the return type will probably be changed to an Optional.
     */
    @Experimental
    LanguageMetricsProvider<?, ?> getLanguageMetricsProvider();


    /**
     * Returns the designer bindings for this language version.
     * Null is not an acceptable result, use {@link DefaultDesignerBindings#getInstance()}
     * instead.
     *
     * @since 6.20.0
     */
    @Experimental
    DesignerBindings getDesignerBindings();

}
