/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import org.jaxen.Navigator;

import net.sourceforge.pmd.lang.xpath.Initializer;

import net.sf.saxon.sxpath.IndependentContext;

/**
 * Interface for performing Language specific XPath handling, such as
 * initialization and navigation.
 */
public interface XPathHandler {

    XPathHandler DUMMY = new XPathHandler() {
        @Override
        public void initialize() {
        }

        @Override
        public void initialize(IndependentContext context) {
        }

        @Override
        public Navigator getNavigator() {
            return null;
        }
    };

    /**
     * Initialize. This is intended to be called by {@link Initializer} to
     * perform Language specific initialization.
     */
    void initialize();

    /**
     * Initialize. This is intended to be called by {@link Initializer} to
     * perform Language specific initialization for Saxon.
     */
    void initialize(IndependentContext context);

    /**
     * Get a Jaxen Navigator for this Language. May return <code>null</code> if
     * there is no Jaxen Navigation for this language.
     */
    Navigator getNavigator();
}
