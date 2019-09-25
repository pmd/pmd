/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

public final class ASTClassOrInterfaceBodyDeclaration extends AbstractTypeBodyDeclaration implements ASTAnyTypeBodyDeclaration {

    ASTClassOrInterfaceBodyDeclaration(int id) {
        super(id);
    }

    ASTClassOrInterfaceBodyDeclaration(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public boolean isFindBoundary() {
        return isAnonymousInnerClass();
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    public boolean isAnonymousInnerClass() {
        return jjtGetParent().jjtGetParent() instanceof ASTAllocationExpression;
    }

    public boolean isEnumChild() {
        return jjtGetParent().jjtGetParent() instanceof ASTEnumConstant;
    }
}
