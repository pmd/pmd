/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

public final class ASTAnnotationTypeDeclaration extends AbstractAnyTypeDeclaration {


    ASTAnnotationTypeDeclaration(int id) {
        super(id);
    }

    ASTAnnotationTypeDeclaration(JavaParser p, int id) {
        super(p, id);
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
    public TypeKind getTypeKind() {
        return TypeKind.ANNOTATION;
    }


    @Override
    public List<ASTAnyTypeBodyDeclaration> getDeclarations() {
        return getFirstChildOfType(ASTAnnotationTypeBody.class)
            .findChildrenOfType(ASTAnyTypeBodyDeclaration.class);
    }
}
