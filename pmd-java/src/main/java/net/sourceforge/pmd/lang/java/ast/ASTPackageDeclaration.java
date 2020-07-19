/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTPackageDeclaration extends AbstractJavaAnnotatableNode {

    @InternalApi
    @Deprecated
    public ASTPackageDeclaration(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    public String getPackageNameImage() {
        return ((ASTName) getChild(this.getNumChildren() - 1)).getImage();
    }
}
