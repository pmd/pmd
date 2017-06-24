/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * @author Clément Fournier
 */
public class JavaParserVisitorReducedAdapter extends JavaParserVisitorAdapter {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        return visit((ASTAnyTypeDeclaration) node, data);
    }

    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
        return visit((ASTAnyTypeDeclaration) node, data);
    }

    public Object visit(ASTEnumDeclaration node, Object data) {
        return visit((ASTAnyTypeDeclaration) node, data);
    }

    public Object visit(ASTAnyTypeDeclaration node, Object data) {
        return visit((JavaNode) node, data);
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        return visit((ASTMethodOrConstructorDeclaration) node, data);
    }

    public Object visit(ASTConstructorDeclaration node, Object data) {
        return visit((ASTMethodOrConstructorDeclaration) node, data);
    }

    public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
        return visit((JavaNode) node, data);
    }


}
