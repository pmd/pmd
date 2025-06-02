/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a wildcard type. Those can only occur when nested in an
 * {@link ASTTypeArguments} node.
 *
 * <pre class="grammar">
 *
 * WildcardType ::= "?" ( ("extends" | "super") {@link ASTReferenceType ReferenceType} )?
 *
 * </pre>
 */
public final class ASTWildcardType extends AbstractJavaTypeNode implements ASTReferenceType {

    private boolean isLowerBound;

    ASTWildcardType(int id) {
        super(id);
    }

    void setLowerBound(boolean lowerBound) {
        isLowerBound = lowerBound;
    }

    /**
     * Return true if this is an upper type bound, e.g.
     * {@code <? extends Integer>}, or the unbounded
     * wildcard {@code <?>}.
     */
    public boolean isUpperBound() {
        return !isLowerBound;
    }


    /**
     * Returns true if this is a lower type bound, e.g.
     * in {@code <? super Node>}.
     */
    public boolean isLowerBound() {
        return isLowerBound;
    }


    /**
     * Returns the type node representing the bound, e.g.
     * the {@code Node} in {@code <? super Node>}, or null in
     * the unbounded wildcard {@code <?>}.
     */
    public @Nullable ASTReferenceType getTypeBoundNode() {
        return firstChild(ASTReferenceType.class);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
