/*
 * User: tom
 * Date: Sep 30, 2002
 * Time: 11:09:24 AM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.*;
import net.sourceforge.pmd.RuleContext;

public class SymbolTableBuilder extends JavaParserVisitorAdapter {

    private SymbolTable symbolTable = new SymbolTable();

    public void initializeWith(ASTCompilationUnit node) {
        node.jjtAccept(this, null);
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    /**
     *  these AST types trigger a new LocalScope
     */
    public Object visit(ASTBlock node, Object data){return openLocalScope(node);}
    public Object visit(ASTConstructorDeclaration node, Object data){return openLocalScope(node);}
    public Object visit(ASTMethodDeclaration node, Object data){return openLocalScope(node);}
    public Object visit(ASTFieldDeclaration node, Object data){return openLocalScope(node);}
    public Object visit(ASTTryStatement node, Object data){return openLocalScope(node);}
    public Object visit(ASTForStatement node, Object data){return openLocalScope(node);}
    public Object visit(ASTIfStatement node, Object data){return openLocalScope(node);}

    /**
     * Collect NameDeclarations
     */
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (node.jjtGetParent().jjtGetParent() instanceof ASTLocalVariableDeclaration) {
            node.setScope(symbolTable.getCurrentScope());
            symbolTable.addDeclaration(new NameDeclaration(node, Kind.LOCAL_VARIABLE));
        }
        return super.visit(node, data);
    }

    /**
     * Collect NameOccurrences
     */
    public Object visit(ASTName node, Object data) {
        if (node.jjtGetParent() instanceof ASTPrimaryPrefix) {
            symbolTable.lookup(new NameOccurrence(node));
        }
        return super.visit(node, data);
    }

    private Object openLocalScope(SimpleNode node) {
        symbolTable.openScope(new LocalScope());
        super.visit(node, null);
        symbolTable.leaveScope();
        return null;
    }

}
