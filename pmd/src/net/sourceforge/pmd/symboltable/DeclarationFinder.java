/*
 * User: tom
 * Date: Oct 10, 2002
 * Time: 8:02:39 AM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;

public class DeclarationFinder extends JavaParserVisitorAdapter {

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        NameDeclaration decl = new NameDeclaration(node);
        node.getScope().addVariableDeclaration(decl);
        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclarator node, Object data) {
        MethodNameDeclaration decl = new MethodNameDeclaration(node);
        node.getScope().getEnclosingClassScope().addMethodDeclaration(decl);
        return super.visit(node, data);
    }
}
