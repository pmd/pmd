/*
 * User: tom
 * Date: Jun 19, 2002
 * Time: 9:31:16 AM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.*;

public class SymbolTable {

    private List scopes = new ArrayList();

    public SymbolTable() {
        scopes.add(new LocalScope());
    }

    public Scope peek() {
        return (Scope)scopes.get(scopes.size()-1);
    }

    public int depth() {
        return scopes.size();
    }

    public void push(Scope scope) {
        scope.setParent(peek());
        scopes.add(scope);
    }

    public void pop() {
        scopes.remove(peek());
    }

}
