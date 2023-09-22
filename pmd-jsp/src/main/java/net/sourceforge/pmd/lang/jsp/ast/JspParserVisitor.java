/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Backwards-compatibility only.
 *
 * @deprecated Use {@link JspVisitor}
 */
@Deprecated
@DeprecatedUntil700
public interface JspParserVisitor extends JspVisitor<Object, Object> {

    @Override
    default Object visitNode(Node node, Object param) {
        node.children().forEach(c -> c.acceptVisitor(this, param));
        return param;
    }
}
