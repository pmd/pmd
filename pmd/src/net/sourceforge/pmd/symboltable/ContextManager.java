/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 1:33:38 PM
 */
package net.sourceforge.pmd.symboltable;

import java.util.List;
import java.util.ArrayList;

public class ContextManager {

    private List scopes = new ArrayList();

    public Scope getCurrentScope() {
        return (Scope)scopes.get(scopes.size()-1);
    }

    public void lookup(NameOccurrence nameOccurrence) {
        lookup(nameOccurrence, scopes.size()-1);
    }

    public void openScope() {
        scopes.add(new Scope());
    }

    public void leaveScope() {
        scopes.remove(scopes.size()-1);
    }

    private void lookup(NameOccurrence nameOccurrence, int startingDepth) {
        Scope scope = (Scope)scopes.get(startingDepth);
        if (!scope.contains(nameOccurrence) && startingDepth>1) {
            lookup(nameOccurrence, startingDepth-1);
        }
        if (scope.contains(nameOccurrence)) {
            scope.addOccurrence(nameOccurrence);
        }
    }
}
