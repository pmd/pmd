/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents the {@code default} clause of an {@linkplain ASTMethodDeclaration annotation method}.
 *
 * <pre class="grammar">
 *
 * DefaultValue ::= "default" {@link ASTMemberValue MemberValue}
 *
 * </pre>
 */
public final class ASTDefaultValue extends AbstractJavaNode {

    ASTDefaultValue(int id) {
        super(id);
    }

    /**
     * Returns the constant value nested in this node.
     */
    public ASTMemberValue getConstant() {
        return (ASTMemberValue) getChild(0);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
