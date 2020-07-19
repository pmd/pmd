/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents a non-shortcut boolean OR-expression. This has a precedence
 * greater than {@link ASTConditionalAndExpression}, and lower than
 * {@link ASTExclusiveOrExpression}.
 *
 * <p>Note that the children of this node are not necessarily {@link ASTExclusiveOrExpression},
 * rather, they are expressions with an operator precedence greater or equal to ExclusiveOrExpression.
 *
 *
 * <pre>
 *
 * InclusiveOrExpression ::=  {@linkplain ASTExclusiveOrExpression ExclusiveOrExpression} ( "|" {@linkplain ASTExclusiveOrExpression ExclusiveOrExpression} )+
 *
 * </pre>
 */
public class ASTInclusiveOrExpression extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTInclusiveOrExpression(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
