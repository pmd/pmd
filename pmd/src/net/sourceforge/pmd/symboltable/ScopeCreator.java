package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTClassBody;
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

public class ScopeCreator extends JavaParserVisitorAdapter {

    private ScopeFactory sf = new ScopeFactory();
    private Stack scopes = new Stack();

    public Object visit(ASTCompilationUnit node, Object data) {
        openScope(node);
        return data;
    }

    public Object visit(ASTUnmodifiedClassDeclaration node, Object data) {
        openScope(node);
        return data;
    }

    public Object visit(ASTClassBodyDeclaration node, Object data) {
        openScope(node);
        return data;
    }

    public Object visit(ASTUnmodifiedInterfaceDeclaration node, Object data) {
        openScope(node);
        return data;
    }

    public Object visit(ASTBlock node, Object data) {
        openScope(node);
        return data;
    }

    public Object visit(ASTConstructorDeclaration node, Object data) {
        openScope(node);
        return data;
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        openScope(node);
        return data;
    }

    public Object visit(ASTTryStatement node, Object data) {
        openScope(node);
        return data;
    }

    public Object visit(ASTForStatement node, Object data) {
        openScope(node);
        return data;
    }

    public Object visit(ASTIfStatement node, Object data) {
        openScope(node);
        return data;
    }

    public Object visit(ASTSwitchStatement node, Object data) {
        openScope(node);
        return data;
    }

    private void push(Scope scope) {
        if (scopes.empty()) {
            if (!(scope instanceof GlobalScope)) {
                throw new RuntimeException("First scope should be a GlobalScope");
            }
        } else {
            scope.setParent((Scope) scopes.peek());
        }
        scopes.add(scope);
    }

    private void openScope(SimpleNode node) {
        // special case - anonymous inner class - blahh
        if (node instanceof ASTClassBodyDeclaration) {
            if (node.jjtGetParent() instanceof ASTClassBody && node.jjtGetParent().jjtGetParent() instanceof ASTAllocationExpression) {
                Scope scope = new ClassScope();
                processScope(scope, node);
            } else {
                super.visit(node, null);
            }
        } else {
            Scope scope = sf.createScope(node);
            processScope(scope, node);
        }
    }

    private void processScope(Scope scope, SimpleNode node) {
        push(scope);
        node.setScope((Scope) scopes.peek());
        super.visit(node, null);
        scopes.pop();
    }
}
