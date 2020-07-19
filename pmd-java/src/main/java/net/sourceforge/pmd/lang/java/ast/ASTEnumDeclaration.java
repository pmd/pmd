/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.NodeStream;

public class ASTEnumDeclaration extends AbstractAnyTypeDeclaration {


    @InternalApi
    @Deprecated
    public ASTEnumDeclaration(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    @Override
    public TypeKind getTypeKind() {
        return TypeKind.ENUM;
    }


    @Override
    public NodeStream<ASTAnyTypeBodyDeclaration> getDeclarations() {
        return children(ASTEnumBody.class).children(ASTAnyTypeBodyDeclaration.class);
    }
}
