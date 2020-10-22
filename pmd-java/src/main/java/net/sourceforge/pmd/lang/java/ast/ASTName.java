/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;

public class ASTName extends AbstractJavaTypeNode {

    private NameDeclaration nd;


    /**
     * Constructor for a synthetic node.
     * @param image Image of the new node
     */
    @InternalApi
    @Deprecated
    public ASTName(String image, AbstractJavaNode parent) {
        super(JavaParserImplTreeConstants.JJTNAME);
        setImage(image);
        setParent(parent);
    }

    ASTName(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public void setNameDeclaration(NameDeclaration nd) {
        this.nd = nd;
    }

    public NameDeclaration getNameDeclaration() {
        return this.nd;
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
