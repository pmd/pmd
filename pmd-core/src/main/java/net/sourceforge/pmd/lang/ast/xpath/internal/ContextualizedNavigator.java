/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath.internal;

import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.ast.xpath.DocumentNavigator;

/**
 * Navigator that records attribute usages.
 */
public class ContextualizedNavigator extends DocumentNavigator {

    private final DeprecatedAttrLogger ctx;

    public ContextualizedNavigator(DeprecatedAttrLogger ctx) {
        this.ctx = ctx;
    }

    @Override
    public String getAttributeStringValue(Object arg0) {
        Attribute attr = (Attribute) arg0;
        ctx.recordUsageOf(attr);
        return attr.getStringValue();
    }
}
