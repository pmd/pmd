/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents a boolean OR-expression. This has a precedence greater than {@link ASTConditionalExpression},
 * and lower than {@link ASTConditionalAndExpression}.
 *
 * <p>Note that the children of this node are not necessarily {@link ASTConditionalAndExpression},
 * rather, they are expressions with an operator precedence greater or equal to ConditionalAndExpression.
 *
 *
 * <pre>
 *
 * ConditionalOrExpression ::=  {@linkplain ASTConditionalAndExpression ConditionalAndExpression} ( "||" {@linkplain ASTConditionalAndExpression ConditionalAndExpression} )+
 *
 * </pre>
 */
public class ASTConditionalOrExpression extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTConditionalOrExpression(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
