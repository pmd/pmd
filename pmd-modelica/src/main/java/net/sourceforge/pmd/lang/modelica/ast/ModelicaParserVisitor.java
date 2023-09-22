/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Backwards-compatibility only.
 *
 * @deprecated Use {@link ModelicaVisitor}
 */
@Deprecated
@DeprecatedUntil700
public interface ModelicaParserVisitor extends ModelicaVisitor<Object, Object> {

    @Override
    default Object visitNode(Node node, Object param) {
        node.children().forEach(c -> c.acceptVisitor(this, param));
        return param;
    }

}
