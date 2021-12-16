/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.rule.xpath.internal.DefaultXPathFunctions;
import net.sourceforge.pmd.util.CollectionUtil;

import net.sf.saxon.lib.ExtensionFunctionDefinition;


/**
 * Interface for performing Language specific XPath handling, such as
 * initialization and navigation.
 */
public interface XPathHandler {

    /**
     * Returns the set of extension functions for this language module.
     * These are the additional functions available in XPath queries.
     */
    Set<ExtensionFunctionDefinition> getRegisteredExtensionFunctions();


    static XPathHandler noFunctionDefinitions() {
        return () -> DefaultXPathFunctions.getDefaultFunctions();
    }

    /**
     * Returns a default XPath handler.
     */
    static XPathHandler getHandlerForFunctionDefs(ExtensionFunctionDefinition first, ExtensionFunctionDefinition... defs) {
        Set<ExtensionFunctionDefinition> set = new HashSet<>(CollectionUtil.setOf(first, defs));
        set.addAll(DefaultXPathFunctions.getDefaultFunctions());

        return () -> Collections.unmodifiableSet(set);
    }
}
