/*
 * User: tom
 * Date: Oct 10, 2002
 * Time: 8:02:39 AM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;

public class DeclarationFinder extends JavaParserVisitorAdapter {
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        node.getScope().addDeclaration(new NameDeclaration(node));
        return super.visit(node, data);
    }
}
