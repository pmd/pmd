/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathHandler;
import net.sourceforge.pmd.reporting.ViolationDecorator;
import net.sourceforge.pmd.reporting.ViolationSuppressor;
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
     * Returns the parser instance.
     */
    Parser getParser();

    /**
     * Returns the language-specific violation decorator.
     */
    default ViolationDecorator getViolationDecorator() {
        return ViolationDecorator.noop();
    }

    /**
     * Returns additional language-specific violation suppressors.
     * These take precedence over the default suppressors (eg nopmd comment),
     * but do not replace them.
     */
    default List<ViolationSuppressor> getExtraViolationSuppressors() {
        return Collections.emptyList();
    }


    /**
     * Returns the metrics provider for this language version,
     * or null if it has none.
     */
    default LanguageMetricsProvider getLanguageMetricsProvider() {
        return null;
    }


    /**
     * Returns the designer bindings for this language version.
     * Null is not an acceptable result, use {@link DefaultDesignerBindings#getInstance()}
     * instead.
     *
     * @since 6.20.0
     */
    default DesignerBindings getDesignerBindings() {
        return DefaultDesignerBindings.getInstance();
    }

}
