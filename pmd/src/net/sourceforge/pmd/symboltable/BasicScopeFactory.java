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
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.HashMap;
import java.util.Map;

public class BasicScopeFactory implements ScopeFactory {

    private Set localTriggers = new HashSet();
    private Set methodTriggers = new HashSet();
    private Set classTriggers = new HashSet();
    private Set globalTriggers = new HashSet();

    private Stack scopes = new Stack();

    public BasicScopeFactory() {
        initScopeTriggers();
    }

    public void openScope(ScopeCreationVisitor vis, SimpleNode node) {
        // special case - anonymous inner class - blahh
        if (node instanceof ASTClassBodyDeclaration) {
            anonymousInnerClassCheck(node, vis);
        } else {
            checkForScope(node, vis);
        }
    }

    private void checkForScope(SimpleNode node, ScopeCreationVisitor vis) {
        Scope scope = null;
        if (localTriggers.contains(node.getClass())) {
            scope = new LocalScope();
        } else if (methodTriggers.contains(node.getClass())) {
            scope = new MethodScope();
        } else if (classTriggers.contains(node.getClass())) {
            scope = new ClassScope(node.getImage());
        } else if (globalTriggers.contains(node.getClass())) {
            scope = new GlobalScope();
        }
        processScope(vis, scope, node);
    }

    private void anonymousInnerClassCheck(SimpleNode node, ScopeCreationVisitor vis) {
        if (node.jjtGetParent() instanceof ASTClassBody
         && node.jjtGetParent().jjtGetParent() instanceof ASTAllocationExpression) {
            processScope(vis, new ClassScope(), node);
        } else {
            vis.cont(node);
        }
    }

    public Scope getCurrentScope() {
        return (Scope)scopes.peek();
    }

    private void processScope(ScopeCreationVisitor vis, Scope scope, SimpleNode node) {
        push(scope);
        node.setScope((Scope)scopes.peek());
        vis.cont(node);
        scopes.pop();
    }

    private void push(Scope scope) {
        if (!(scope instanceof GlobalScope)) {
            scope.setParent((Scope) scopes.peek());
        }
        scopes.add(scope);
    }

    private void initScopeTriggers() {
        localTriggers.add(ASTBlock.class);
        localTriggers.add(ASTTryStatement.class);
        localTriggers.add(ASTForStatement.class);
        localTriggers.add(ASTSwitchStatement.class);
        localTriggers.add(ASTIfStatement.class);
        methodTriggers.add(ASTConstructorDeclaration.class);
        methodTriggers.add(ASTMethodDeclaration.class);
        classTriggers.add(ASTUnmodifiedClassDeclaration.class);
        classTriggers.add(ASTUnmodifiedInterfaceDeclaration.class);
        globalTriggers.add(ASTCompilationUnit.class);
    }
}
