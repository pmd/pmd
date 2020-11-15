/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import static java.util.Collections.emptyList;

import java.util.List;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.AstProcessingStage;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.lang.rule.impl.DefaultRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;
import net.sourceforge.pmd.util.designerbindings.DesignerBindings;
import net.sourceforge.pmd.util.designerbindings.DesignerBindings.DefaultDesignerBindings;


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
    default XPathHandler getXPathHandler() {
        return XPathHandler.noFunctionDefinitions();
    }


    /**
     * Returns the list of all supported optional processing stages.
     *
     * @return A list of all optional processing stages.
     */
    @Experimental
    default List<? extends AstProcessingStage<?>> getProcessingStages() {
        return emptyList();
    }


    /**
     * Get the default ParserOptions.
     *
     * @return ParserOptions
     */
    default ParserOptions getDefaultParserOptions() {
        return new ParserOptions();
    }


    /**
     * Get the Parser.
     *
     * @return Parser
     */
    Parser getParser(ParserOptions parserOptions);


    default Parser getParser() {
        return getParser(getDefaultParserOptions());
    }


    /**
     * Get the RuleViolationFactory.
     */
    default RuleViolationFactory getRuleViolationFactory() {
        return DefaultRuleViolationFactory.defaultInstance();
    }


    /**
     * Returns the metrics provider for this language version,
     * or null if it has none.
     *
     * Note: this is experimental, ie unstable until 7.0.0, after
     * which it will probably be promoted to a stable API. For
     * instance the return type will probably be changed to an Optional.
     */
    @Experimental
    default LanguageMetricsProvider<?, ?> getLanguageMetricsProvider() {
        return null;
    }


    /**
     * Returns the designer bindings for this language version.
     * Null is not an acceptable result, use {@link DefaultDesignerBindings#getInstance()}
     * instead.
     *
     * @since 6.20.0
     */
    @Experimental
    default DesignerBindings getDesignerBindings() {
        return DefaultDesignerBindings.getInstance();
    }

}
