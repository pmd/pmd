/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Backwards-compatibility only.
 *
 * @deprecated Use {@link JavaVisitor}
 */
@Deprecated
@DeprecatedUntil700
public interface JavaParserVisitor extends JavaVisitor<Object, Object> {

    @Override
    default Object visitNode(Node node, Object param) {
        for (Node child: node.children()) {
            child.acceptVisitor(this, param);
        }
        return param;
    }

    // REMOVE ME
    // deprecated stuff kept for compatibility with existing visitors, not matched by anything

    @Deprecated
    default Object visit(ASTExpression node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTLiteral node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTType node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTReferenceType node, Object data) {
        return null;
    }

    default Object visit(ASTStatement node, Object data) {
        return null;
    }

    @Deprecated
    default Object visit(ASTPrimaryExpression node, Object data) {
        return null;
    }



}
