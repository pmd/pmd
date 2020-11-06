/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * Represents a shift expression on a numeric value. This has a precedence greater than {@link
 * ASTRelationalExpression},
 * and lower than {@link ASTAdditiveExpression}.
 *
 * <p>Note that the children of this node are not necessarily {@link ASTAdditiveExpression},
 * rather, they are expressions with an operator precedence greater or equal to AdditiveExpression.
 *
 *
 * <pre>
 *
 * ShiftExpression ::=  {@linkplain ASTAdditiveExpression AdditiveExpression} ( ( "<<"  | {@linkplain ASTRSIGNEDSHIFT RSIGNEDSHIFT} | {@linkplain ASTRUNSIGNEDSHIFT RUNSIGNEDSHIFT} ) {@linkplain ASTAdditiveExpression AdditiveExpression} )+
 *
 * </pre>
 */
// TODO we could merge the productions for ASTRSIGNEDSHIFT and ASTRUNSIGNEDSHIFT into this node using a #void production that sets the image of the parent
public class ASTShiftExpression extends AbstractJavaTypeNode {

    @InternalApi
    @Deprecated
    public ASTShiftExpression(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTShiftExpression(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the image of the operator, i.e. "<<", ">>", or ">>>".
     */
    public String getOperator() {
        return getImage();
    }

}
