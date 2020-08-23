/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * @deprecated Use {@link EcmascriptVisitor}
 */
@Deprecated
@DeprecatedUntil700
public interface EcmascriptParserVisitor extends EcmascriptVisitor<Object, Object> {

    @Override
    default Object visitNode(Node node, Object param) {
        node.children().forEach(c -> c.acceptVisitor(this, param));
        return param;
    }

    /**
     * @deprecated Use {@link #visitJsNode(EcmascriptNode, Object)}
     */
    @Deprecated
    default Object visit(EcmascriptNode<?> node, Object data) {
        for (EcmascriptNode<?> child : node.children()) {
            child.acceptVisitor(this, data);
        }
        return data;
    }
}
