/*
 * User: tom
 * Date: Oct 9, 2002
 * Time: 5:45:14 PM
 */
package net.sourceforge.pmd.symboltable;

import java.util.Iterator;

public class MethodScope extends AbstractScope {

    protected VariableNameDeclaration findHere(NameOccurrence occurrence) {
        if (occurrence.isThisOrSuper()) {
            return null;
        }
        for (Iterator i = names.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration nameDeclaration = (VariableNameDeclaration)i.next();
            if (nameDeclaration.getImage().equals(occurrence.getImage())) {
                return nameDeclaration;
            }
        }
        return null;
    }

    public String toString() {
        return "MethodScope:" + super.glomNames();
    }
}
