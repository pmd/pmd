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
 * Serves as a sort of adaptor between the AST nodes and the symbol table scopes
 */
public class BasicScopeCreationVisitor extends JavaParserVisitorAdapter {

    private ScopeFactory sf;
    private Stack scopes = new Stack();

    public BasicScopeCreationVisitor(ScopeFactory sf) {
        this.sf = sf;
    }

    public Object visit(ASTCompilationUnit node, Object data) {
        sf.openScope(scopes, node);
        cont(node);
        return data;
    }

    public Object visit(ASTUnmodifiedClassDeclaration node, Object data) {
        sf.openScope(scopes, node);
        cont(node);
        return data;
    }

    public Object visit(ASTClassBodyDeclaration node, Object data) {
        if (node.isAnonymousInnerClass()) {
            sf.openScope(scopes, node);
            cont(node);
        } else {
            super.visit(node, data);
        }
        return data;
    }

    public Object visit(ASTUnmodifiedInterfaceDeclaration node, Object data) {
        sf.openScope(scopes, node);
        cont(node);
        return data;
    }

    public Object visit(ASTBlock node, Object data) {
        sf.openScope(scopes, node);
        cont(node);
        return data;
    }

    public Object visit(ASTConstructorDeclaration node, Object data) {
        sf.openScope(scopes, node);
        cont(node);
        return data;
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        sf.openScope(scopes, node);
        cont(node);
        return data;
    }

    public Object visit(ASTTryStatement node, Object data) {
        sf.openScope(scopes, node);
        cont(node);
        return data;
    }

    public Object visit(ASTForStatement node, Object data) {
        sf.openScope(scopes, node);
        cont(node);
        return data;
    }

    public Object visit(ASTIfStatement node, Object data) {
        sf.openScope(scopes, node);
        cont(node);
        return data;
    }

    public Object visit(ASTSwitchStatement node, Object data) {
        sf.openScope(scopes, node);
        cont(node);
        return data;
    }

    private void cont(SimpleNode node) {
        super.visit(node, null);
        scopes.pop();
    }
}
