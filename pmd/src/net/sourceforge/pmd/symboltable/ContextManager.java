/*
 * User: tom
 * Date: Oct 2, 2002
 * Time: 2:57:11 PM
 */
package net.sourceforge.pmd.symboltable;

public class ContextManager {

    private SymbolTable scopes;

    public ContextManager(SymbolTable scopes) {
        this.scopes = scopes;
    }

    public Scope getCurrentScope() {
        return scopes.peek();
    }

    public void openScope(Scope scope) {
        scopes.push(scope);
    }

    public void leaveScope() {
        scopes.pop();
    }

}
