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
            NameDeclaration decl = (NameDeclaration)i.next();
            if (decl.getImage().equals(occurrence.getObjectName())
             || (className + "." + decl.getImage()).equals(occurrence.getImage())
            ) {
                return decl;
            }
        }
        return null;
    }

    public String toString() {
        return "ClassScope:" + super.glomNames();
    }

}
