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

    public void recordOccurrence(NameOccurrence nameOccurrence) {
        if (occursInHigherScope(nameOccurrence, scopes.size()-1)) {
            return;
        }
        if (!getCurrentScope().contains(nameOccurrence) ) {
            return;
        }
        getCurrentScope().addOccurrence(nameOccurrence);
    }

    public void openScope() {
        scopes.add(new Scope());
    }

    public void leaveScope() {
        scopes.remove(scopes.size()-1);
    }

    private boolean occursInHigherScope(NameOccurrence nameOccurrence, int startingDepth) {
        if (!((Scope)scopes.get(startingDepth)).contains(nameOccurrence) && startingDepth>1) {
            return occursInHigherScope(nameOccurrence, startingDepth-1);
        }
        if (((Scope)scopes.get(startingDepth)).contains(nameOccurrence)) {
            ((Scope)scopes.get(startingDepth)).addOccurrence(nameOccurrence);
            return true;
        }
        return false;
    }
}
