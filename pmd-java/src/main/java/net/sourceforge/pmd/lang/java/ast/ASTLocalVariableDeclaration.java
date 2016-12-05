/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
/* Generated By:JJTree: Do not edit this line. ASTLocalVariableDeclaration.java */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.Rule;

public class ASTLocalVariableDeclaration extends AbstractJavaAccessNode implements Dimensionable, CanSuppressWarnings {

    public ASTLocalVariableDeclaration(int id) {
        super(id);
    }

    public ASTLocalVariableDeclaration(JavaParser p, int id) {
        super(p, id);
    }

    /**
     * Accept the visitor. *
     */
    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean hasSuppressWarningsAnnotationFor(Rule rule) {
        for (int i = 0; i < jjtGetNumChildren(); i++) {
            if (jjtGetChild(i) instanceof ASTAnnotation) {
                ASTAnnotation a = (ASTAnnotation) jjtGetChild(i);
                if (a.suppresses(rule)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isArray() {
        return checkType() + checkDecl() > 0;
    }

    public int getArrayDepth() {
        return checkType() + checkDecl();
    }

    public ASTType getTypeNode() {
        for (int i = 0; i < jjtGetNumChildren(); i++) {
            if (jjtGetChild(i) instanceof ASTType) {
                return (ASTType) jjtGetChild(i);
            }
        }
        throw new IllegalStateException("ASTType not found");
    }

    private int checkType() {
        return getTypeNode().getArrayDepth();
    }

    private ASTVariableDeclaratorId getDecl() {
        return (ASTVariableDeclaratorId) jjtGetChild(jjtGetNumChildren() - 1).jjtGetChild(0);
    }

    private int checkDecl() {
        return getDecl().getArrayDepth();
    }

    /**
     * Gets the variable name of this field. This method searches the first
     * VariableDeclartorId node and returns it's image or <code>null</code> if
     * the child node is not found.
     *
     * @return a String representing the name of the variable
     */
    public String getVariableName() {
        ASTVariableDeclaratorId decl = getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        if (decl != null) {
            return decl.getImage();
        }
        return null;
    }
}
