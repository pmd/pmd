/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Internal API.
 *
 * <p>Acts as a bridge between outer parts of PMD and the restricted access
 * internal API of this package.
 *
 * <p><b>None of this is published API, and compatibility can be broken anytime!</b>
 * Use this only at your own risk.
 *
 * @apiNote Internal API
 */
@InternalApi
public final class InternalApiBridge {
    private InternalApiBridge() {}

    public static String replacementIfDeprecated(Attribute attribute) {
        return attribute.replacementIfDeprecated();
    }

    public static XPathRule newXPathRule() {
        return new XPathRule();
    }
}
