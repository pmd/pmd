/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.rule.xpath.internal.DefaultXPathFunctions;
import net.sourceforge.pmd.util.CollectionUtil;


/**
 * Interface for performing Language specific XPath handling, such as
 * initialization and navigation.
 */
public interface XPathHandler {

    /**
     * Returns the set of extension functions for this language module.
     * These are the additional functions available in XPath queries.
     */
    Set<XPathFunctionDefinition> getRegisteredExtensionFunctions();


    static XPathHandler noFunctionDefinitions() {
        return DefaultXPathFunctions::getDefaultFunctions;
    }

    /**
     * Returns a default XPath handler.
     */
    static XPathHandler getHandlerForFunctionDefs(XPathFunctionDefinition first, XPathFunctionDefinition... defs) {
        Set<XPathFunctionDefinition> set = new HashSet<>(CollectionUtil.setOf(first, defs));
        set.addAll(DefaultXPathFunctions.getDefaultFunctions());

        return () -> Collections.unmodifiableSet(set);
    }

    /**
     * Return a new XPath handler combining all available functions from this and another handler.
     * @param other The handler whose functions to merge with this one.
     * @return A new handler exposing all functions from both XPath handlers.
     * @since 7.13.0
     */
    default XPathHandler combine(XPathHandler other) {
        Set<XPathFunctionDefinition> set = new HashSet<>(getRegisteredExtensionFunctions());
        set.addAll(other.getRegisteredExtensionFunctions());

        return () -> Collections.unmodifiableSet(set);
    }
}
