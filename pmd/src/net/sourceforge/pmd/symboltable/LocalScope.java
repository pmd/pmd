/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 1:33:03 PM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.*;

import java.util.*;

public class LocalScope extends AbstractScope {

    public void addDeclaration(NameDeclaration nameDecl) {
        if (nameDecl.isExceptionBlockParameter()) {
            // this declaration needs to go somewhere... should this be delegated to the next
            // highest LocalScope?
            return;
        }
        super.addDeclaration(nameDecl);
    }

    protected NameDeclaration findHere(NameOccurrence occurrence) {
        if (occurrence.usesThisOrSuper()) {
            return null;
        }
        String objectName = (String)occurrence.getQualifiers().iterator().next();
        for (Iterator i = names.keySet().iterator(); i.hasNext();) {
            NameDeclaration nameDeclaration = (NameDeclaration)i.next();
            if (nameDeclaration.getImage().equals(objectName)) {
                return nameDeclaration;
            }
        }
        return null;
    }

    public String toString() {
        return "LocalScope:" + super.glomNames();
    }
}
