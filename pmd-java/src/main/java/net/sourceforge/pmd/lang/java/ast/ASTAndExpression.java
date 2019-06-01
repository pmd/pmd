/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a non-shortcut boolean AND-expression.
 * This has a precedence greater than {@link ASTExclusiveOrExpression},
 * and lower than {@link ASTEqualityExpression}.
 *
 * <p>Note that the children of this node are not necessarily {@link ASTEqualityExpression},
 * rather, they are expressions with an operator precedence greater or equal to EqualityExpression.
 *
 *
 * <pre class="grammar">
 *
 * AndExpression ::=  {@linkplain ASTEqualityExpression EqualityExpression} ( "&" {@linkplain ASTEqualityExpression EqualityExpression} )+
 *
 * </pre>
 */
public final class ASTAndExpression extends AbstractJavaTypeNode implements ASTExpression {

    ASTAndExpression(int id) {
        super(id);
    }

    ASTAndExpression(JavaParser p, int id) {
        super(p, id);
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
