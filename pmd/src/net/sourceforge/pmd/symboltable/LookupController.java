/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 3:34:17 PM
 */
package net.sourceforge.pmd.symboltable;

import java.util.List;

public class LookupController {

    private List scopes;

    public LookupController(List scopes) {
        this.scopes = scopes;
    }

    public void lookup(NameOccurrence nameOccurrence) {
        lookup(nameOccurrence, scopes.size()-1);
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
