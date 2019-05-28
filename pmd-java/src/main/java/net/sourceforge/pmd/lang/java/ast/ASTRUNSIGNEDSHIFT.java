/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * @deprecated Will be removed in 7.0.0. Use {@link ASTShiftExpression#getOperator()}
 */
@Deprecated
public class ASTRUNSIGNEDSHIFT extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public ASTRUNSIGNEDSHIFT(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTRUNSIGNEDSHIFT(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
