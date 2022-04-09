/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

public final class ASTClassOrInterfaceBodyDeclaration extends AbstractTypeBodyDeclaration implements JavaNode {

    ASTClassOrInterfaceBodyDeclaration(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        throw new UnsupportedOperationException("Node was removed from grammar");
    }


    public boolean isAnonymousInnerClass() {
        return getParent().getParent() instanceof ASTAllocationExpression;
    }

    public boolean isEnumChild() {
        return getParent().getParent() instanceof ASTEnumConstant;
    }
}
