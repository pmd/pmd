/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;

@DeprecatedUntil700
@Deprecated
public interface ApexParserVisitor extends ApexVisitor<Object, Object> {

    @Override
    default Object visitApexNode(ApexNode<?> node, Object data) {
        return visit(node, data); // calls the overload below for compatibility
    }

    @Deprecated
    default Object visit(ApexNode<?> node, Object data) {
        return visitApexNode(node, data);
    }

}
