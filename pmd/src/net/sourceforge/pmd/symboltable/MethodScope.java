/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.util.Applier;

public class MethodScope extends AbstractScope {

    public NameDeclaration findVariableHere(NameOccurrence occurrence) {
        if (occurrence.isThisOrSuper()) {
            return null;
        }
        ImageFinderFunction finder = new ImageFinderFunction(occurrence.getImage());
        Applier.apply(finder, variableNames.keySet().iterator());
        return finder.getDecl();
    }

    public String toString() {
        return "MethodScope:" + super.glomNames();
    }
}
