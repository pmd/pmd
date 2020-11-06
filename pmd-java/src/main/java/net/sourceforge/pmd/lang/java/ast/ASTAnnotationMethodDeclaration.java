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

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public MethodLikeKind getKind() {
        return MethodLikeKind.METHOD;
    }
}
