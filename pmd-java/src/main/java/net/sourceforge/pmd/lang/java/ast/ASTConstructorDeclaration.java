/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
/* Generated By:JJTree: Do not edit this line. ASTConstructorDeclaration.java */

package net.sourceforge.pmd.lang.java.ast;


public class ASTConstructorDeclaration extends AbstractMethodOrConstructorDeclaration {

    private boolean containsComment;

    public ASTConstructorDeclaration(int id) {
        super(id);
    }

    public ASTConstructorDeclaration(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public MethodLikeKind getKind() {
        return MethodLikeKind.CONSTRUCTOR;
    }

    /**
     * Accept the visitor. *
     */
    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Deprecated
    public boolean containsComment() {
        return this.containsComment;
    }

    @Deprecated
    public void setContainsComment() {
        this.containsComment = true;
    }

    /**
     * @deprecated to be removed with PMD 7.0.0 - use getFormalParameters() instead
     */
    @Deprecated
    public ASTFormalParameters getParameters() {
        return getFormalParameters();
    }

    public int getParameterCount() {
        return getFormalParameters().getParameterCount();
    }

    //@Override // enable this with PMD 7.0.0 - see interface ASTMethodOrConstructorDeclaration
    public ASTFormalParameters getFormalParameters() {
        return getFirstChildOfType(ASTFormalParameters.class);
    }
}
