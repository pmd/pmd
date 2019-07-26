/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents the {@code default} value of an {@linkplain ASTMethodDeclaration annotation method}.
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

    ASTDefaultValue(JavaParser p, int id) {
        super(p, id);
    }

    /**
     * Returns the constant value nested in this node.
     */
    public ASTMemberValue getConstant() {
        return (ASTMemberValue) jjtGetChild(0);
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
