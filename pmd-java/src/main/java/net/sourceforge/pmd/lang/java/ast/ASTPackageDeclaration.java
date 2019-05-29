/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

public class ASTPackageDeclaration extends AbstractJavaNode implements Annotatable {
    public ASTPackageDeclaration(int id) {
        super(id);
    }

    ASTPackageDeclaration(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public List<ASTAnnotation> getDeclaredAnnotations() {
        return findChildrenOfType(ASTAnnotation.class);
    }

    /**
     * Accept the visitor. *
     */
    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    public String getPackageNameImage() {
        return ((ASTName) jjtGetChild(this.jjtGetNumChildren() - 1)).getImage();
    }
}
