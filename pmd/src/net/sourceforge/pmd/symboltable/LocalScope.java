/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.util.Applier;

public class LocalScope extends AbstractScope {

    public void addDeclaration(VariableNameDeclaration nameDecl) {
        if (nameDecl.isExceptionBlockParameter()) {
            // this declaration needs to go somewhere... should this be delegated to the next
            // highest LocalScope?
            return;
        }
        super.addDeclaration(nameDecl);
    }

    public NameDeclaration findVariableHere(NameOccurrence occurrence) {
        if (occurrence.isThisOrSuper()) {
            return null;
        }
        ImageFinderFunction finder = new ImageFinderFunction(occurrence.getImage());
        Applier.apply(finder, variableNames.keySet().iterator());
        return finder.getDecl();
    }

    public String toString() {
        return "LocalScope:" + super.glomNames();
    }
}
