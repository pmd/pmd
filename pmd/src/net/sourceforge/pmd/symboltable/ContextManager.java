/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 1:33:38 PM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;

import java.util.List;
import java.util.ArrayList;

public class ContextManager {

    private List scopes;

    public ContextManager(List scopes) {
        this.scopes = scopes;
    }

    public Scope getCurrentScope() {
        return (Scope)scopes.get(scopes.size()-1);
    }

    public void openScope(Scope scope) {
        scopes.add(scope);
    }

    public void leaveScope() {
        scopes.remove(scopes.size()-1);
    }

}
