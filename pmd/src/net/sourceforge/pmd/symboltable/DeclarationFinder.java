/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;

public class DeclarationFinder extends JavaParserVisitorAdapter {

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        node.getScope().addDeclaration(new VariableNameDeclaration(node));
        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclarator node, Object data) {
        node.getScope().getEnclosingClassScope().addDeclaration(new MethodNameDeclaration(node));
        return super.visit(node, data);
    }
}
