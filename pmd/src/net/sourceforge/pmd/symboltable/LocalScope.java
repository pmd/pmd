/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 1:33:03 PM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.*;

import java.util.*;

public class LocalScope extends AbstractScope {

    public void addDeclaration(VariableNameDeclaration nameDecl) {
        if (nameDecl.isExceptionBlockParameter()) {
            // this declaration needs to go somewhere... should this be delegated to the next
            // highest LocalScope?
            return;
        }
        super.addDeclaration(nameDecl);
    }

    protected VariableNameDeclaration findVariableHere(NameOccurrence occurrence) {
        if (occurrence.isThisOrSuper()) {
            return null;
        }
        for (Iterator i = variableNames.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration nameDeclaration = (VariableNameDeclaration)i.next();
            if (nameDeclaration.getImage().equals(occurrence.getImage())) {
                return nameDeclaration;
            }
        }
        return null;
    }

    public String toString() {
        return "LocalScope:" + super.glomNames();
    }
}
