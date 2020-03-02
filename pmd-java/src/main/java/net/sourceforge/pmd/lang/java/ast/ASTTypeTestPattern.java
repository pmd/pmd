/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * A type test pattern (JDK 14 preview feature). This can be found on
 * the right-hand side of an {@link ASTInfixExpression InstanceOfExpression},
 * in a {@link ASTPatternExpression PatternExpression}.
 *
 * <pre class="grammar">
 *
 * TypeTestPattern ::= {@linkplain ASTType Type} {@link ASTVariableDeclaratorId VariableDeclaratorId}
 *
 * </pre>
 */
@Experimental
public final class ASTTypeTestPattern extends AbstractJavaNode implements ASTPattern {


    ASTTypeTestPattern(int id) {
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

    /**
     * Gets the type against which the expression is tested.
     */
    public ASTType getTypeNode() {
        return (ASTType) getChild(0);
    }

    /** Returns the declared variable. */
    public ASTVariableDeclaratorId getVarId() {
        return (ASTVariableDeclaratorId) getChild(1);
    }

}
