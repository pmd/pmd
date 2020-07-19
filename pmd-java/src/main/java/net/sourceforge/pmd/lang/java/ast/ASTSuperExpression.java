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
public final class ASTSuperExpression extends AbstractJavaExpr implements ASTPrimaryExpression {
    ASTSuperExpression(int id) {
        super(id);
    }


    @Nullable
    public ASTClassOrInterfaceType getQualifier() {
        return getNumChildren() > 0 ? (ASTClassOrInterfaceType) getChild(0) : null;
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
