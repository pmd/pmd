/*
 * User: tom
 * Date: Sep 30, 2002
 * Time: 11:09:24 AM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.*;


public class SymbolFacade extends JavaParserVisitorAdapter {

    private ContextManager contextManager;
    private LookupController lookupController;
    private ScopeFactory sf = new ScopeFactory();

    public SymbolFacade() {
        SymbolTable symbolTable = new SymbolTable();
        contextManager = new ContextManagerImpl(symbolTable);
        lookupController = new LookupController(symbolTable);
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
        NameDeclaration nameDeclaration = new NameDeclaration(node);
        nameDeclaration.setExceptionBlockParameter(node.jjtGetParent().jjtGetParent() instanceof ASTTryStatement);
        contextManager.getCurrentScope().addDeclaration(nameDeclaration);
        return super.visit(node, data);
    }

    public Object visit(ASTName node, Object data) {
        if (node.jjtGetParent() instanceof ASTPrimaryPrefix) {
            lookupController.lookup(new NameOccurrence(node));
        }
        return super.visit(node, data);
    }

    private void openScope(SimpleNode node) {
        Scope scope = sf.createScope(node);
        if (scope instanceof NullScope) {
            super.visit(node, null);
        } else {
            //System.out.println("opening scope " + scope.getClass().getName().substring(scope.getClass().getName().lastIndexOf('.')+1) + " created as a result of hitting a " + node.getClass().getName().substring(node.getClass().getName().lastIndexOf('.')+1));
            contextManager.openScope(scope);
            super.visit(node, null);
            //System.out.println("leaving scope " + scope.getClass().getName().substring(scope.getClass().getName().lastIndexOf('.')+1) + " created as a result of hitting a " + node.getClass().getName().substring(node.getClass().getName().lastIndexOf('.')+1));
            contextManager.leaveScope();
        }
    }

}
