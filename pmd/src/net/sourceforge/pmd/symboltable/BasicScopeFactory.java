package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTAllocationExpression;
import net.sourceforge.pmd.ast.ASTClassBody;
import net.sourceforge.pmd.ast.ASTClassBodyDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

public class BasicScopeFactory implements ScopeFactory {

    private Stack scopes = new Stack();
    private Set scopeEvaluators = new HashSet();

    public BasicScopeFactory() {
        scopeEvaluators.add(new GlobalScopeEvaluator());
        scopeEvaluators.add(new ClassScopeEvaluator());
        scopeEvaluators.add(new MethodScopeEvaluator());
        scopeEvaluators.add(new LocalScopeEvaluator());
    }

    public void openScope(ScopeCreationVisitor vis, SimpleNode node) {
        if (node instanceof ASTClassBodyDeclaration) {
            anonymousInnerClassCheck((ASTClassBodyDeclaration)node, vis);
        } else {
            createScope(node, vis);
        }
    }

    public Scope getCurrentScope() {
        return (Scope)scopes.peek();
    }

    private void createScope(SimpleNode node, ScopeCreationVisitor vis) {
        for (Iterator i = scopeEvaluators.iterator(); i.hasNext();) {
            ScopeEvaluator ev = (ScopeEvaluator)i.next();
            if  (ev.IsScopeCreatedBy(node)) {
                processScope(vis, ev.getScopeFor(node), node);
                break;
            }
        }
    }

    private void anonymousInnerClassCheck(ASTClassBodyDeclaration node, ScopeCreationVisitor vis) {
        if (node.isAnonymousInnerClass()) {
            processScope(vis, new ClassScope(), node);
        } else {
            vis.cont(node);
        }
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
}
