/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Visitor adapter with convenient visit methods to e.g. treat contructors and methods the same.
 *
 * @author Clément Fournier
 */
public class JavaParserVisitorReducedAdapter extends JavaParserVisitorAdapter {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        return visit((ASTAnyTypeDeclaration) node, data);
    }


    @Override
    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
        return visit((ASTAnyTypeDeclaration) node, data);
    }


    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        return visit((ASTAnyTypeDeclaration) node, data);
    }


    @Override
    public Object visit(ASTRecordDeclaration node, Object data) {
        return visit((ASTAnyTypeDeclaration) node, data);
    }


    public Object visit(ASTAnyTypeDeclaration node, Object data) {
        return visit((JavaNode) node, data);
    }


    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        return visit((ASTMethodOrConstructorDeclaration) node, data);
    }


    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        return visit((ASTMethodOrConstructorDeclaration) node, data);
    }


    public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
        return visit((MethodLikeNode) node, data);
    }


    @Override
    public Object visit(ASTLambdaExpression node, Object data) {
        return visit((MethodLikeNode) node, data);
    }


    public Object visit(MethodLikeNode node, Object data) {
        return visit((JavaNode) node, data);
    }


}
