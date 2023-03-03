/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.ast.Node;

@DeprecatedUntil700
@Deprecated
public interface ApexParserVisitor extends ApexVisitor<Object, Object> {

    @Override
    default Object visitNode(Node node, Object param) {
        for (Node child : node.children()) {
            child.acceptVisitor(this, param);
        }
        return param;
    }

    @Deprecated
    default Object visit(ApexNode<?> node, Object data) {
        return visitNode(node, data);
    }

    @Override
    default Object visitApexNode(ApexNode<?> node, Object data) {
        return visit(node, data); // calls the overload above for compatibility
    }

}
