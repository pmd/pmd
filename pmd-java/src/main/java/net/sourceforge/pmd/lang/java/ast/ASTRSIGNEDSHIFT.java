/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * @deprecated Will be removed in 7.0.0. Use {@link ASTShiftExpression#getOperator()}
 */
@Deprecated
public final class ASTRSIGNEDSHIFT extends AbstractJavaNode {

    ASTRSIGNEDSHIFT(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
