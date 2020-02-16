/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents a {@code for}-loop, or a foreach loop.
 *
 * <pre>
 *
 * ForStatement ::= "for" "(" {@linkplain ASTLocalVariableDeclaration LocalVariableDeclaration} ":" {@linkplain ASTExpression Expression} ")" {@linkplain ASTStatement Statement}
 *                | "for" "(" {@linkplain ASTForInit ForInit}? ";" {@linkplain ASTExpression Expression}? ";" {@linkplain ASTForUpdate ForUpdate}? ")" {@linkplain ASTStatement Statement}
 *
 * </pre>
 */
// TODO this should be split into two different nodes, otherwise
// we can't enrich the API without returning null half the time
public class ASTForStatement extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public ASTForStatement(int id) {
        super(id);
    }


    @InternalApi
    @Deprecated
    public ASTForStatement(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the node that represents the guard of this loop.
     * This may be any expression of type boolean.
     *
     * <p>If this node represents a foreach loop, or if there is
     * no specified guard, then returns null.
     *
     * @deprecated Use {@link #getCondition()}
     */
    @Deprecated
    public ASTExpression getGuardExpressionNode() {
        return getCondition();
    }


    /**
     * Returns the node that represents the guard of this loop.
     * This may be any expression of type boolean.
     *
     * <p>If this node represents a foreach loop, or if there is
     * no specified guard, then returns null.
     */
    public ASTExpression getCondition() {
        if (isForeach()) {
            return null;
        }
        return getFirstChildOfType(ASTExpression.class);
    }


    /**
     * Returns true if this node represents a foreach loop.
     */
    public boolean isForeach() {
        return getChild(0) instanceof ASTLocalVariableDeclaration;
    }


    /**
     * Returns the statement that represents the body of this
     * loop.
     */
    public ASTStatement getBody() {
        return (ASTStatement) getChild(getNumChildren() - 1);
    }

}
