/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

public class BasicScopeFactory implements ScopeFactory {

    private Set scopeEvaluators = new HashSet();

    public BasicScopeFactory() {
        scopeEvaluators.add(new GlobalScopeEvaluator());
        scopeEvaluators.add(new ClassScopeEvaluator());
        scopeEvaluators.add(new MethodScopeEvaluator());
        scopeEvaluators.add(new LocalScopeEvaluator());
    }

    public void openScope(Stack scopes, SimpleNode node) {
        for (Iterator i = scopeEvaluators.iterator(); i.hasNext();) {
            ScopeEvaluator ev = (ScopeEvaluator)i.next();
            if  (ev.isScopeCreatedBy(node)) {
                Scope scope = ev.getScopeFor(node);
                if (!(scope instanceof GlobalScope)) {
                    scope.setParent((Scope) scopes.peek());
                }
                scopes.add(scope);
                node.setScope((Scope)scopes.peek());
                break;
            }
        }
    }

}
