/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.Set;

import net.sourceforge.pmd.util.CollectionUtil;

import net.sf.saxon.lib.ExtensionFunctionDefinition;

/**
 * Default XPath functions provided by pmd-core.
 */
public final class DefaultXPathFunctions {

    private DefaultXPathFunctions() {
        // utility class
    }

    public static Set<ExtensionFunctionDefinition> getDefaultFunctions() {
        return CollectionUtil.setOf(FileNameXPathFunction.INSTANCE);
    }
}
