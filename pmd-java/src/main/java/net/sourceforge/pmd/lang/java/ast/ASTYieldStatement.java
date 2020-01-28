/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A {@code yield} statement in a {@linkplain ASTSwitchExpression switch expression}.
 *
 * <pre class="grammar">
 *
 * YieldStatement ::= "yield" {@link ASTExpression} ";"
 *
 * </pre>
 */
public class ASTYieldStatement extends AbstractStatement {

    ASTYieldStatement(int id) {
        super(id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        String result = super.getImage();
        if (result == null && hasDescendantOfType(ASTName.class)) {
            result = getFirstDescendantOfType(ASTName.class).getImage();
        }
        return result;
    }


    /** Returns the yielded expression. */
    public ASTExpression getExpr() {
        return (ASTExpression) getChild(0);
    }

}
