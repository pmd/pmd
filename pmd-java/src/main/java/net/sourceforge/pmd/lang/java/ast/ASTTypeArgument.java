/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
/* Generated By:JJTree: Do not edit this line. ASTTypeArgument.java */

package net.sourceforge.pmd.lang.java.ast;

public class ASTTypeArgument extends AbstractJavaNode {
    public ASTTypeArgument(int id) {
        super(id);
    }

    public ASTTypeArgument(JavaParser p, int id) {
        super(p, id);
    }

    /**
     * Accept the visitor. *
     */
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
