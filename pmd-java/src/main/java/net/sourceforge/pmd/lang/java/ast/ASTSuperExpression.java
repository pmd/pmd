/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * The "super" keyword. Technically not an expression but it's easier to analyse that way.
 *
 * <pre class="grammar">
 *
 * SuperExpression ::= "super"
 *                   | {@link ASTClassOrInterfaceType TypeName} "." "super"
 *
 * </pre>
 */
public final class ASTSuperExpression extends AbstractJavaExpr implements ASTPrimaryExpression, LeftRecursiveNode {
    ASTSuperExpression(int id) {
        super(id);
    }


    ASTSuperExpression(JavaParser p, int id) {
        super(p, id);
    }

    @Nullable
    public ASTClassOrInterfaceType getQualifier() {
        return getNumChildren() > 0 ? (ASTClassOrInterfaceType) getChild(0) : null;
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


}
