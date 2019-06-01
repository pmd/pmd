/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents an identity test between two values or more values.
 * This has a precedence greater than {@link ASTAndExpression},
 * and lower than {@link ASTInstanceOfExpression} and {@link ASTRelationalExpression}.
 *
 * <pre class="grammar">
 *
 * EqualityExpression ::=  {@linkplain ASTEqualityExpression EqualityExpression}  ( ( "==" | "!=" ) {@linkplain ASTInstanceOfExpression InstanceOfExpression}  )+
 *
 * </pre>
 *
 * <p>Note that the children of this node are not necessarily {@link ASTInstanceOfExpression},
 * rather, they are expressions with an operator precedence greater or equal to InstanceOfExpression.
 *
 * <p>The first child may be another EqualityExpression only
 * if its operator is different. For example, if parentheses represent
 * nesting:
 * <table summary="Nesting examples">
 * <tr><th></th><th>Parses as</th></tr>
 *     <tr><td>{@code 1 == 2 == 3}</td><td>{@code (1 == 2 == 3)}</td></tr>
 *     <tr><td>{@code 1 == 2 != 3}</td><td>{@code ((1 == 2) != 3)}</td></tr>
 *     <tr><td>{@code 1 == 2 != 3 + 4}</td><td>{@code ((1 == 2) != (3 + 4))}</td></tr>
 *     <tr><td>{@code 1 == 2 != 3 != 4}</td><td>{@code ((1 == 2) != 3 != 4)}</td></tr>
 *     <tr><td>{@code 1 == 2 != 3 != 4 == 5}</td><td>{@code (((1 == 2) != 3 != 4) == 5)}</td></tr>
 * </table>
 */
public final class ASTEqualityExpression extends AbstractLrBinaryExpr implements ASTExpression {
    ASTEqualityExpression(int id) {
        super(id);
    }

    ASTEqualityExpression(JavaParser p, int id) {
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
