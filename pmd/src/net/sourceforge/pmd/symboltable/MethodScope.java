/*
 * User: tom
 * Date: Oct 9, 2002
 * Time: 5:45:14 PM
 */
package net.sourceforge.pmd.symboltable;

import java.util.Iterator;

public class MethodScope extends AbstractScope {

    protected NameDeclaration findHere(NameOccurrence occurrence) {
        // bail out if there's a qualifier, because if it
        // is, it's definitely not a function parameter or a local variable
        if (occurrence.getQualifier() != null) {
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
