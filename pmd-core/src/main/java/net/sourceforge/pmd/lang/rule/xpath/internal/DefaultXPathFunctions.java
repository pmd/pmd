/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.Set;

import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionDefinition;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Default XPath functions provided by pmd-core.
 */
public final class DefaultXPathFunctions {

    private static final Set<XPathFunctionDefinition> DEFAULTS =
        CollectionUtil.immutableSetOf(
            FileNameXPathFunction.INSTANCE,
            CoordinateXPathFunction.START_LINE,
            CoordinateXPathFunction.START_COLUMN,
            CoordinateXPathFunction.END_LINE,
            CoordinateXPathFunction.END_COLUMN
        );

    private DefaultXPathFunctions() {
        // utility class
    }

    public static Set<XPathFunctionDefinition> getDefaultFunctions() {
        return DEFAULTS;
    }
}
