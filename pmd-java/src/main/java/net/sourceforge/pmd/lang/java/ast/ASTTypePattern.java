/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * A type pattern (JDK16). This can be found on
 * the right-hand side of an {@link ASTInfixExpression InstanceOfExpression},
 * in a {@link ASTPatternExpression PatternExpression}.
 *
 * <pre class="grammar">
 *
 * TypePattern ::= ( "final" | {@linkplain ASTAnnotation Annotation} )* {@linkplain ASTType Type} {@link ASTVariableDeclaratorId VariableDeclaratorId}
 *
 * </pre>
 *
 * @see <a href="https://openjdk.java.net/jeps/394">JEP 394: Pattern Matching for instanceof</a>
*/
public final class ASTTypePattern extends AbstractJavaNode implements ASTPattern, AccessNode {

    private int parenDepth;

    ASTTypePattern(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Gets the type against which the expression is tested.
     */
    public @NonNull ASTType getTypeNode() {
        return Objects.requireNonNull(firstChild(ASTType.class));
    }

    /** Returns the declared variable. */
    public @NonNull ASTVariableDeclaratorId getVarId() {
        return Objects.requireNonNull(firstChild(ASTVariableDeclaratorId.class));
    }

    void bumpParenDepth() {
        parenDepth++;
    }

    @Override
    @Experimental
    public int getParenthesisDepth() {
        return parenDepth;
    }
}
