/*
 * User: tom
 * Date: Oct 3, 2002
 * Time: 11:08:37 AM
 */
package net.sourceforge.pmd.symboltable;

import java.util.*;

public class ClassScope extends AbstractScope {

    private String className;

    public ClassScope(String className) {
        this.className = className;
    }

    protected NameDeclaration findHere(NameOccurrence occurrence) {
        for (Iterator i = names.keySet().iterator(); i.hasNext();) {
            NameDeclaration nameDeclaration = (NameDeclaration)i.next();
            if (nameDeclaration.getImage().equals(occurrence.getObjectName())
             || (className + "." + nameDeclaration.getImage()).equals(occurrence.getImage())
            ) {
                return nameDeclaration;
            }
        }
        return null;
    }

    public String toString() {
        return "ClassScope:" + super.glomNames();
    }

}
