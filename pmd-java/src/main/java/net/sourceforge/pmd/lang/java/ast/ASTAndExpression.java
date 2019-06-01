/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents a non-shortcut boolean AND-expression.
 * This has a precedence greater than {@link ASTExclusiveOrExpression},
 * and lower than {@link ASTEqualityExpression}.
 *
 * <p>Note that the children of this node are not necessarily {@link ASTEqualityExpression},
 * rather, they are expressions with an operator precedence greater or equal to EqualityExpression.
 *
 *
 * <pre>
 *
 * AndExpression ::=  {@linkplain ASTEqualityExpression EqualityExpression} ( "&" {@linkplain ASTEqualityExpression EqualityExpression} )+
 *
 * </pre>
 */
public class ASTAndExpression extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTAndExpression(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTAndExpression(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
