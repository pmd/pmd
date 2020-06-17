/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents a boolean AND-expression. This has a precedence greater than {@link ASTConditionalOrExpression},
 * and lower than {@link ASTInclusiveOrExpression}.
 *
 * <p>Note that the children of this node are not necessarily {@link ASTInclusiveOrExpression},
 * rather, they are expressions with an operator precedence greater or equal to InclusiveOrExpression.
 *
 *
 * <pre>
 *
 * ConditionalAndExpression ::=  {@linkplain ASTInclusiveOrExpression InclusiveOrExpression} ( "&&" {@linkplain ASTInclusiveOrExpression InclusiveOrExpression} )+
 *
 * </pre>
 */
public class ASTConditionalAndExpression extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTConditionalAndExpression(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
