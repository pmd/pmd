/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTResourceSpecification extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public ASTResourceSpecification(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTResourceSpecification(JavaParser p, int id) {
        super(p, id);
    }

    /** Accept the visitor. **/
    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
/*
 * JavaCC - OriginalChecksum=d495bcf34ff0f86f77e48f66b9c52e4d (do not edit this
 * line)
 */
