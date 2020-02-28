/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import net.sourceforge.pmd.annotation.Experimental;

@Experimental
public class ASTRecordDeclaration extends AbstractAnyTypeDeclaration {
    ASTRecordDeclaration(int id) {
        super(id);
    }

    ASTRecordDeclaration(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public TypeKind getTypeKind() {
        return null;
    }

    @Override
    public List<ASTAnyTypeBodyDeclaration> getDeclarations() {
        // TODO Auto-generated method stub
        return null;
    }
}
