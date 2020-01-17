/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents a type test on an object. This has a precedence greater than {@link ASTEqualityExpression},
 * and lower than {@link ASTShiftExpression}. This has the same precedence as a {@link ASTRelationalExpression}.
 *
 * <p>Note that the children of this node are not necessarily {@link ASTRelationalExpression},
 * rather, they are expressions with an operator precedence greater or equal to RelationalExpression.
 *
 *
 * <pre>
 *
 * InstanceOfExpression ::=  {@linkplain ASTShiftExpression ShiftExpression} "instanceof" {@linkplain ASTType Type}
 *
 * </pre>
 */
public class ASTInstanceOfExpression extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTInstanceOfExpression(int id) {
        super(id);
    }


    @InternalApi
    @Deprecated
    public ASTInstanceOfExpression(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    /**
     * Gets the type against which the expression is tested.
     */
    public ASTType getTypeNode() {
        return (ASTType) getChild(1);
    }

}
