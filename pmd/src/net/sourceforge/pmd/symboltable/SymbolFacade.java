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
import java.util.List;

public class SymbolFacade extends JavaParserVisitorAdapter {

    private Set localScopeTriggers = new HashSet();
    private Set classScopeTriggers = new HashSet();
    private Set globalScopeTriggers = new HashSet();

    private ContextManager contextManager;
    private LookupController lookupController;

    public SymbolFacade() {
        initScopeTriggers();
        SymbolTable symbolTable = new SymbolTable();
        contextManager = new ContextManagerImpl(symbolTable);
        lookupController = new LookupController(symbolTable);
    }

    private void initScopeTriggers() {
        localScopeTriggers.add(ASTBlock.class);
        localScopeTriggers.add(ASTConstructorDeclaration.class);
        localScopeTriggers.add(ASTMethodDeclaration.class);
        localScopeTriggers.add(ASTFieldDeclaration.class);
        localScopeTriggers.add(ASTTryStatement.class);
        localScopeTriggers.add(ASTForStatement.class);
        localScopeTriggers.add(ASTIfStatement.class);

        classScopeTriggers.add(ASTClassBody.class);

        globalScopeTriggers.add(ASTCompilationUnit.class);
    }

    public void initializeWith(ASTCompilationUnit node) {
        node.jjtAccept(this, null);
    }

    public Object visit(ASTCompilationUnit node, Object data){openScope(node);return data;}
    public Object visit(ASTClassBody node, Object data){openScope(node);return data;}
    public Object visit(ASTBlock node, Object data){openScope(node);return data;}
    public Object visit(ASTConstructorDeclaration node, Object data){openScope(node);return data;}
    public Object visit(ASTMethodDeclaration node, Object data){openScope(node);return data;}
    public Object visit(ASTFieldDeclaration node, Object data){openScope(node);return data;}
    public Object visit(ASTTryStatement node, Object data){openScope(node);return data;}
    public Object visit(ASTForStatement node, Object data){openScope(node);return data;}
    public Object visit(ASTIfStatement node, Object data){openScope(node);return data;}

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        node.setScope(contextManager.getCurrentScope());
        contextManager.getCurrentScope().addDeclaration(new NameDeclaration(node));
        return super.visit(node, data);
    }

    /**
     * Collect NameOccurrences
     */
    public Object visit(ASTName node, Object data) {
        if (node.jjtGetParent() instanceof ASTPrimaryPrefix) {
            lookupController.lookup(new NameOccurrence(node));
        }
        return super.visit(node, data);
    }

    private void openScope(SimpleNode node) {
        if (localScopeTriggers.contains(node.getClass())) {
            contextManager.openScope(new LocalScope());
            super.visit(node, null);
            contextManager.leaveScope();
        } else if (classScopeTriggers.contains(node.getClass())) {
            contextManager.openScope(new ClassScope());
            super.visit(node, null);
            contextManager.leaveScope();
        } else if (globalScopeTriggers.contains(node.getClass())) {
            contextManager.openScope(new GlobalScope());
            super.visit(node, null);
            contextManager.leaveScope();
        } else {
            super.visit(node, null);
        }
    }

}
