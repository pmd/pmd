/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTAnnotationMethodDeclaration extends AbstractMethodLikeNode {

    @InternalApi
    @Deprecated
    public ASTAnnotationMethodDeclaration(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTAnnotationMethodDeclaration(JavaParser p, int id) {
        super(p, id);
    }

    /** Accept the visitor. **/
    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public MethodLikeKind getKind() {
        return MethodLikeKind.METHOD;
    }
}
/*
 * JavaCC - OriginalChecksum=f6dd440446f8aa5c9c191ae760080ee0 (do not edit this
 * line)
 */
