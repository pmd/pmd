/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;

@DeprecatedUntil700
@Deprecated
public interface ApexParserVisitor extends ApexVisitor<Object, Object> {


    default Object visit(ApexNode<?> node, Object data) {
        return visitApexNode(node, data);
    }

}
