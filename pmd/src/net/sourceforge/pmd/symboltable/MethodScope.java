/*
 * User: tom
 * Date: Oct 9, 2002
 * Time: 5:45:14 PM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.util.Applier;

public class MethodScope extends AbstractScope {

    protected NameDeclaration findVariableHere(NameOccurrence occurrence) {
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
