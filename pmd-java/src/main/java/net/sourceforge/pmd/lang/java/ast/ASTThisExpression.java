/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * The "this" expression. Related to the {@link ASTSuperExpression "super"} pseudo-expression.
 *
 * <pre class="grammar">
 *
 * ThisExpression ::= "this"
 *                  | {@link ASTClassOrInterfaceType TypeName} "." "this"
 *
 * </pre>
 */
public final class ASTThisExpression extends AbstractJavaExpr implements ASTPrimaryExpression {

    ASTThisExpression(int id) {
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
