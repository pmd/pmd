/*
 * User: tom
 * Date: Jun 19, 2002
 * Time: 9:31:16 AM
 */
package net.sourceforge.pmd.symboltable;

import java.util.*;

public class SymbolTable {

    // this is really only used during the initial construction
    // of the symbol table.  After that the AST provides part of the traversal mechanism,
    // and the individual scopes provide the other part
    private Stack scopes = new Stack();

    public Scope peek() {
        return (Scope)scopes.peek();
    }

    public int depth() {
        return scopes.size();
    }

    public void push(Scope scope) {
        if (scopes.empty()) {
            if (!(scope instanceof GlobalScope)) {
                throw new RuntimeException("First scope should be a GlobalScope");
            }
        } else {
            scope.setParent(peek());
        }
        scopes.add(scope);
    }

    public void pop() {
        scopes.pop();
    }

}
