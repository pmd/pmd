/*
 * User: tom
 * Date: Sep 30, 2002
 * Time: 11:09:24 AM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.*;
import net.sourceforge.pmd.RuleContext;

public class SymbolTableBuilder extends JavaParserVisitorAdapter {

    private SymbolTable table;
    private SymbolTable tablePtr;

    public void initializeWith(ASTCompilationUnit node) {
        node.jjtAccept(this, null);
    }

    public SymbolTable getSymbolTable() {
        return table;
    }

    /**
     * Skip interfaces because they don't have local variables.
     */
    public Object visit(ASTInterfaceDeclaration node, Object data) {
        return data;
    }

    // these AST types trigger a new scope
    public Object visit(ASTBlock node, Object data){return openScope(node);}
    public Object visit(ASTConstructorDeclaration node, Object data){return openScope(node);}
    public Object visit(ASTMethodDeclaration node, Object data){return openScope(node);}
    public Object visit(ASTFieldDeclaration node, Object data){return openScope(node);}
    public Object visit(ASTTryStatement node, Object data){return openScope(node);}
    public Object visit(ASTForStatement node, Object data){return openScope(node);}
    // these AST types trigger a new scope

    /**
     * This collects the symbols for later reference
     */
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (node.jjtGetParent().jjtGetParent() instanceof ASTLocalVariableDeclaration) {
            tablePtr.add(new Symbol(node.getImage(), node.getBeginLine()));
            SimpleNode simple = (SimpleNode)node;
            simple.setSymbolTable(tablePtr);
        }
        return super.visit(node, data);
    }

    /**
     * This records usage of a symbol
     */
    public Object visit(ASTName node, Object data) {
        if (node.jjtGetParent() instanceof ASTPrimaryPrefix) {
            tablePtr.recordPossibleUsageOf(new Symbol(getEndName(node.getImage()), node.getBeginLine()), node);
        }
        return super.visit(node, data);
    }

    private Object openScope(SimpleNode node) {
        table = new SymbolTable(table);
        tablePtr = table;
        super.visit(node, null);
        tablePtr = table.getParent();
        return null;
    }

    private String getEndName(String name) {
        return (name.indexOf('.') == -1) ? name : name.substring(0, name.indexOf('.'));
    }

}
