/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTClassBodyDeclaration;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTForStatement;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTSwitchStatement;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTUnmodifiedClassDeclaration;
import net.sourceforge.pmd.ast.ASTUnmodifiedInterfaceDeclaration;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.Stack;

/**
 * Visitor for scope creation.
 * Visits all nodes of an AST and creates scope objects for nodes representing
 * syntactic entities which may contain declarations. For example, a block 
 * may contain variable definitions (which are declarations) and 
 * therefore needs a scope object where these declarations can be associated, 
 * whereas an expression can't contain declarations and therefore doesn't need
 * a scope object.
 * With the exception of global scopes, each scope object is linked to its
 * parent scope, which is the scope object of the next embedding syntactic 
 * entity that has a scope. 
 */
public class BasicScopeCreationVisitor extends JavaParserVisitorAdapter {

    /**
     * A stack of scopes reflecting the scope hierarchy when a node is visited.
     * This is used to set the parents of the created scopes correctly.
     */
    private Stack scopes = new Stack();

    /**
     * Sets the scope of a node and adjustes the scope stack accordingly.
     * The scope on top of the stack is set as the parent of the given scope,
     * which is then also stored on the scope stack.
     * @param scope the scope for the node.
     * @param node the AST node for which the scope is to be set.
     * @throws java.util.EmptyStackException if the scope stack is empty.
     */
    private void addScopeWithParent(Scope scope, SimpleNode node) {
        scope.setParent((Scope) scopes.peek());
        scopes.add(scope);
        node.setScope(scope);
    }
    
    /**
     * Creates a new local scope for an AST node.
     * The scope on top of the stack is set as the parent of the new scope,
     * which is then also stored on the scope stack.
     * @param node the AST node for which the scope has to be created.
     * @throws java.util.EmptyStackException if the scope stack is empty.
     */
    private void createLocalScope(SimpleNode node) {
        addScopeWithParent(new LocalScope(), node);
    }

    /**
     * Creates a new method scope for an AST node.
     * The scope on top of the stack is set as the parent of the new scope,
     * which is then also stored on the scope stack.
     * @param node the AST node for which the scope has to be created.
     * @throws java.util.EmptyStackException if the scope stack is empty.
     */
    private void createMethodScope(SimpleNode node) {
        addScopeWithParent(new MethodScope(), node);
    }

    /**
     * Creates a new class scope for an AST node.
     * The scope on top of the stack is set as the parent of the new scope,
     * which is then also stored on the scope stack.
     * @param node the AST node for which the scope has to be created.
     * @throws java.util.EmptyStackException if the scope stack is empty.
     */
    private void createClassScope(SimpleNode node) {
       if (node instanceof ASTClassBodyDeclaration){
           addScopeWithParent(new ClassScope(), node);
       }
       else {
           addScopeWithParent(new ClassScope(node.getImage()), node); 
       }
    }

    /**
     * Creates a new global scope for an AST node.
     * The new scope is stored on the scope stack.
     * @param node the AST node for which the scope has to be created.
     */
    private void createGlobalScope(SimpleNode node) {
       Scope scope = new GlobalScope();
       scopes.add(scope);
       node.setScope(scope);
    }
    
    public BasicScopeCreationVisitor() {
    }
    
    public Object visit(ASTCompilationUnit node, Object data) {
        createGlobalScope(node);
        cont(node);
        return data;
    }

    public Object visit(ASTUnmodifiedClassDeclaration node, Object data) {
        createClassScope(node);
        cont(node);
        return data;
    }

    public Object visit(ASTClassBodyDeclaration node, Object data) {
        if (node.isAnonymousInnerClass()) {
            createClassScope(node);
            cont(node);
        } else {
            super.visit(node, data);
        }
        return data;
    }

    public Object visit(ASTUnmodifiedInterfaceDeclaration node, Object data) {
        createClassScope(node);
        cont(node);
        return data;
    }

    public Object visit(ASTBlock node, Object data) {
        createLocalScope(node);
        cont(node);
        return data;
    }

    public Object visit(ASTConstructorDeclaration node, Object data) {
        createMethodScope(node);
        cont(node);
        return data;
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        createMethodScope(node);
        cont(node);
        return data;
    }

    public Object visit(ASTTryStatement node, Object data) {
        createLocalScope(node);
        cont(node);
        return data;
    }

    public Object visit(ASTForStatement node, Object data) {
        createLocalScope(node);
        cont(node);
        return data;
    }

    public Object visit(ASTIfStatement node, Object data) {
        createLocalScope(node);
        cont(node);
        return data;
    }

    public Object visit(ASTSwitchStatement node, Object data) {
        createLocalScope(node);
        cont(node);
        return data;
    }

    private void cont(SimpleNode node) {
        super.visit(node, null);
        scopes.pop();
    }
}
