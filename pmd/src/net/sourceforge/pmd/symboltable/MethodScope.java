/*
 * User: tom
 * Date: Oct 9, 2002
 * Time: 5:45:14 PM
 */
package net.sourceforge.pmd.symboltable;

import java.util.Iterator;

public class MethodScope extends AbstractScope {

    protected NameDeclaration findHere(NameOccurrence occurrence) {
        if (occurrence.usesThisOrSuper()) {
            return null;
        }
        for (Iterator i = names.keySet().iterator(); i.hasNext();) {
            NameDeclaration nameDeclaration = (NameDeclaration)i.next();
            if (nameDeclaration.getImage().equals(occurrence.getObjectName())) {
                return nameDeclaration;
            }
        }
        return null;
    }

    public String toString() {
        return "MethodScope:" + super.glomNames();
    }
}
