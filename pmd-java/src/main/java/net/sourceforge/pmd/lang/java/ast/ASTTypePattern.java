/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A type pattern (JDK16). This can be found on
 * the right-hand side of an {@link ASTInfixExpression InstanceOfExpression},
 * in a {@link ASTPatternExpression PatternExpression}.
 *
 * <pre class="grammar">
 *
 * TypePattern ::= ( "final" | {@linkplain ASTAnnotation Annotation} )* {@linkplain ASTType Type} {@link ASTVariableId VariableId}
 *
 * </pre>
 *
 * @see <a href="https://openjdk.org/jeps/394">JEP 394: Pattern Matching for instanceof</a> (Java 16)
*/
public final class ASTTypePattern extends AbstractJavaPattern implements ModifierOwner {

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
    public @NonNull ASTVariableId getVarId() {
        return Objects.requireNonNull(firstChild(ASTVariableId.class));
    }
}
