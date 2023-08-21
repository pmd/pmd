/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A {@code yield} statement in a {@linkplain ASTSwitchExpression switch expression}.
 *
 * <pre class="grammar">
 *
 * YieldStatement ::= "yield" {@link ASTExpression} ";"
 *
 * </pre>
 */
public class ASTYieldStatement extends AbstractStatement {

    ASTYieldStatement(int id) {
        super(id);
    }



    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /** Returns the yielded expression. */
    public ASTExpression getExpr() {
        return (ASTExpression) getChild(0);
    }


    /**
     * Returns the switch expression to which this statement yields a
     * value.
     */
    @NonNull
    public ASTSwitchExpression getYieldTarget() {
        return Objects.requireNonNull(ancestors(ASTSwitchExpression.class).first(),
                                      "Yield statements should only be parsable inside switch expressions");
    }


}
