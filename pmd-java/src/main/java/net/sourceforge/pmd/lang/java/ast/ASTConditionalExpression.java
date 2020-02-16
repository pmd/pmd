/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;


/**
 * Represents a conditional expression, aka ternary expression. This operation has
 * a greater precedence as {@linkplain ASTExpression assignment expressions},
 * and lower as {@link ASTConditionalOrExpression}.
 *
 * <p>Note that the children of this node are not necessarily {@link ASTConditionalOrExpression},
 * rather, they are expressions with an operator precedence greater or equal to ConditionalOrExpression.
 *
 * <pre>
 *
 * ConditionalExpression ::= {@linkplain ASTConditionalOrExpression ConditionalOrExpression} "?"  {@linkplain ASTExpression Expression} ":" {@linkplain ASTConditionalExpression ConditionalExpression}
 *
 * </pre>
 */
public class ASTConditionalExpression extends AbstractJavaTypeNode {


    @InternalApi
    @Deprecated
    public ASTConditionalExpression(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTConditionalExpression(JavaParser p, int id) {
        super(p, id);
    }


    /**
     * @deprecated To be removed in 7.0.0
     */
    @InternalApi
    @Deprecated
    public void setTernary() {
        // noop
    }


    /**
     * This method always returns true.
     *
     * @deprecated To be removed in 7.0.0
     */
    @Deprecated
    public boolean isTernary() {
        return true;
    }


    /**
     * Returns the node that represents the guard of this conditional.
     * That is the expression before the '?'.
     *
     * @deprecated Use {@link #getCondition()}
     */
    @Deprecated
    public Node getGuardExpressionNode() {
        return getChild(0);
    }

    /**
     * Returns the node that represents the guard of this conditional.
     * That is the expression before the '?'.
     */
    public Node getCondition() {
        return getChild(0);
    }


    /**
     * Returns the node that represents the expression that will be evaluated
     * if the guard evaluates to true.
     */
    public ASTExpression getTrueAlternative() {
        return (ASTExpression) getChild(1);
    }


    /**
     * Returns the node that represents the expression that will be evaluated
     * if the guard evaluates to false.
     */
    public Node getFalseAlternative() {
        return getChild(2);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
