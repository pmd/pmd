/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a single pair of member name to value in an annotation.
 * This node also represents the shorthand syntax, see {@link #isShorthand()}.
 *
 * <pre class="grammar">
 *
 * MemberValuePair ::= &lt;IDENTIFIER&gt; "=" {@linkplain ASTMemberValue MemberValue}
 *
 * ValueShorthand  ::= {@linkplain ASTMemberValue MemberValue}
 *
 * </pre>
 */
public final class ASTMemberValuePair extends AbstractJavaNode {

    /** The name of the 'value' attribute. */
    public static final String VALUE_ATTR = "value";

    private boolean isShorthand;

    ASTMemberValuePair(int id) {
        super(id);
    }

    /**
     * Returns the name of the member set by this pair.
     * This returns {@code "value"} if this is a shorthand declaration.
     */
    public String getName() {
        return getImage();
    }

    /**
     * Returns true if this is a shorthand for the {@code value} attribute.
     * For example, {@code @A("v")} has exactly the same structure as
     * {@code @A(value = "v")}, except this attribute returns true for
     * the first one only.
     */
    public boolean isShorthand() {
        return isShorthand;
    }

    /**
     * Returns the value of the member set by this pair.
     */
    public ASTMemberValue getValue() {
        return (ASTMemberValue) getChild(0);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    void setShorthand() {
        this.isShorthand = true;
    }
}
