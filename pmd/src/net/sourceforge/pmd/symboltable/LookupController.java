/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 3:34:17 PM
 */
package net.sourceforge.pmd.symboltable;

import java.util.List;
import java.util.Iterator;

public class LookupController {

    public NameDeclaration lookup(NameOccurrence nameOccurrence) {
/*
        if (nameOccurrence.isQualified())  {
            List qualifiers = nameOccurrence.getQualifiers();
            for (Iterator i = qualifiers.iterator(); i.hasNext();) {
                NameDeclaration decl = lookup(nameOccurrence.getScope());
            }
        } else {
            return lookup(nameOccurrence, nameOccurrence.getScope());
        }
*/
        return lookup(nameOccurrence, nameOccurrence.getScope());
    }

    private NameDeclaration lookup(NameOccurrence nameOccurrence, Scope scope) {
        if (!scope.contains(nameOccurrence) && scope.getParent() != null) {
            return lookup(nameOccurrence, scope.getParent());
        }
        if (scope.contains(nameOccurrence)) {
            return scope.addOccurrence(nameOccurrence);
        }
        return null;
    }
}
