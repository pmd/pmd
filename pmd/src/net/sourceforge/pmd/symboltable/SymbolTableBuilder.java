/*
 * User: tom
 * Date: Sep 30, 2002
 * Time: 11:09:24 AM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.*;
import net.sourceforge.pmd.RuleContext;

import java.util.Set;
import java.util.HashSet;

public class SymbolTableBuilder extends JavaParserVisitorAdapter {

    private SymbolTable symbolTable = new SymbolTable();
    private Set localScopeTriggers = new HashSet();

    public void initializeWith(ASTCompilationUnit node) {
        localScopeTriggers.add(ASTBlock.class.toString());
        localScopeTriggers.add(ASTConstructorDeclaration.class.toString());
        localScopeTriggers.add(ASTMethodDeclaration.class.toString());
        localScopeTriggers.add(ASTFieldDeclaration.class.toString());
        localScopeTriggers.add(ASTTryStatement.class.toString());
        localScopeTriggers.add(ASTForStatement.class.toString());
        localScopeTriggers.add(ASTIfStatement.class.toString());
        node.jjtAccept(this, null);
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public Object visit(ASTBlock node, Object data){openScope(node);return data;}
    public Object visit(ASTConstructorDeclaration node, Object data){openScope(node);return data;}
    public Object visit(ASTMethodDeclaration node, Object data){openScope(node);return data;}
    public Object visit(ASTFieldDeclaration node, Object data){openScope(node);return data;}
    public Object visit(ASTTryStatement node, Object data){openScope(node);return data;}
    public Object visit(ASTForStatement node, Object data){openScope(node);return data;}
    public Object visit(ASTIfStatement node, Object data){openScope(node);return data;}

    /**
     * Collect LocalScope NameDeclarations
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

    private void openScope(SimpleNode node) {
        if (localScopeTriggers.contains(node.getClass().toString())) {
            Scope scope = new LocalScope();
            symbolTable.openScope(scope);
            super.visit(node, null);
            symbolTable.leaveScope();
        } else {
            super.visit(node, null);
        }
    }

}
