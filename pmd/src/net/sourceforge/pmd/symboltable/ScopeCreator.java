/*
 * User: tom
 * Date: Oct 10, 2002
 * Time: 8:03:36 AM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.*;

public class ScopeCreator extends JavaParserVisitorAdapter {

    private SymbolTable table = new SymbolTable();
    private ScopeFactory sf = new ScopeFactory();

    public Object visit(ASTCompilationUnit node, Object data){openScope(node);return data;}
    public Object visit(ASTUnmodifiedClassDeclaration node, Object data){openScope(node);return data;}
    public Object visit(ASTBlock node, Object data){openScope(node);return data;}
    public Object visit(ASTConstructorDeclaration node, Object data){openScope(node);return data;}
    public Object visit(ASTMethodDeclaration node, Object data){openScope(node);return data;}
    public Object visit(ASTTryStatement node, Object data){openScope(node);return data;}
    public Object visit(ASTForStatement node, Object data){openScope(node);return data;}
    public Object visit(ASTIfStatement node, Object data){openScope(node);return data;}

    private void openScope(SimpleNode node) {
        Scope scope = sf.createScope(node);
        table.push(scope);
        node.setScope(table.peek());
        super.visit(node, null);
        table.pop();
    }
}
