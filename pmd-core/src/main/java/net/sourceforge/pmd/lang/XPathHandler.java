/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import org.jaxen.Navigator;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.ast.xpath.DefaultASTXPathHandler;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.lang.xpath.Initializer;

import net.sf.saxon.sxpath.IndependentContext;

/**
 * Handles the XPath-specific behaviour of a language.
 */
// TODO move to rule.xpath package
public interface XPathHandler {

    /**
     * @deprecated Use {@link #DEFAULT}.
     */
    @Deprecated
    XPathHandler DUMMY = new DefaultASTXPathHandler();

    /**
     * Default instance. Declares no additional XPath functions.
     */
    XPathHandler DEFAULT = new DefaultASTXPathHandler();


    /**
     * Creates a new XPath rule for the given version and expression.
     * Note: this isn't used by the ruleset factory for the moment,
     * XPath rules are created like normal rules. Programmatic usages
     * of {@link XPathRule} should be replaced with calls to this method.
     * The ruleset schema will get a new syntax for XPath rules in 7.0.0.
     *
     * @param version         Version of the XPath language
     * @param xpathExpression XPath expression
     *
     * @return A new rule
     *
     * @throws NullPointerException If any of the arguments is null
     */
    Rule newXPathRule(XPathVersion version, String xpathExpression);


    /**
     * Initialize. This is intended to be called by {@link Initializer} to
     * perform Language specific initialization.
     *
     * @deprecated Support for Jaxen will be removed come 7.0.0
     */
    @Deprecated
    void initialize();

    /**
     * Initialize. This is intended to be called by {@link Initializer} to
     * perform Language specific initialization for Saxon.
     *
     * @deprecated Internal API
     */
    @Deprecated
    void initialize(IndependentContext context);

    /**
     * Get a Jaxen Navigator for this Language. May return <code>null</code> if
     * there is no Jaxen Navigation for this language.
     *
     * @deprecated Support for Jaxen will be removed come 7.0.0
     */
    @Deprecated
    Navigator getNavigator();
}
