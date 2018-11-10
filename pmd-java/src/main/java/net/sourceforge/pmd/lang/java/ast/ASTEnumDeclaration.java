/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
/* Generated By:JJTree: Do not edit this line. ASTEnumDeclaration.java */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

public class ASTEnumDeclaration extends AbstractAnyTypeDeclaration {


    public ASTEnumDeclaration(int id) {
        super(id);
    }

    public ASTEnumDeclaration(JavaParser p, int id) {
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
        return TypeKind.ENUM;
    }


    @Override
    public List<ASTAnyTypeBodyDeclaration> getDeclarations() {
        return getFirstChildOfType(ASTEnumBody.class)
            .findChildrenOfType(ASTAnyTypeBodyDeclaration.class);
    }
}
