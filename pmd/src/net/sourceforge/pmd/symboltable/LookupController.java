/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 3:34:17 PM
 */
package net.sourceforge.pmd.symboltable;

public class LookupController {

    public void lookup(NameOccurrence nameOccurrence, Scope startingScope) {
        lookupRecurse(nameOccurrence, startingScope);
    }

    private void lookupRecurse(NameOccurrence nameOccurrence, Scope scope) {
        if (!scope.contains(nameOccurrence) && scope.getParent() != null) {
            lookupRecurse(nameOccurrence, scope.getParent());
        }
        if (scope.contains(nameOccurrence)) {
            scope.addOccurrence(nameOccurrence);
        }
    }
}
