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

    // scopes is a List of Lists
    private List scopes = new ArrayList();

    public SymbolTable() {
        scopes.add(new LocalScope());
    }

    public Scope getTail() {
        return (Scope)scopes.get(scopes.size()-1);
    }

    public int depth() {
        return scopes.size();
    }

    public void add(Scope scope) {
        scope.setParent(getTail());
        scopes.add(scope);
    }

    public void removeTail() {
        scopes.remove(getTail());
    }

}
