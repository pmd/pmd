/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 1:33:03 PM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.*;

import java.util.*;

public class LocalScope extends AbstractScope implements Scope {

    private Map names = new HashMap();

    public void addDeclaration(NameDeclaration nameDecl) {
        // this declaration needs to go somewhere... should this be delegated to a higher scope?
        if (nameDecl.getNode().jjtGetParent() instanceof ASTFormalParameter) {
            return;
        }
        if (names.containsKey(nameDecl)) {
            throw new RuntimeException(nameDecl + " is already in the symbol table");
        }
        names.put(nameDecl, new ArrayList());
    }

    public boolean contains(NameOccurrence occurrence) {
        return findHere(occurrence) != null;
    }

    public void addOccurrence(NameOccurrence occurrence) {
        NameDeclaration decl = findHere(occurrence);
        if (decl != null) {
            List nameOccurrences = (List)names.get(decl);
            nameOccurrences.add(occurrence);
        }
    }

    public Iterator getUnusedDeclarations() {
        List unused = new ArrayList();
        for (Iterator i = names.keySet().iterator(); i.hasNext();) {
            NameDeclaration nameDeclaration = (NameDeclaration)i.next();
            if (((List)names.get(nameDeclaration)).isEmpty()) {
                unused.add(nameDeclaration);
            }
        }
        return unused.iterator();
    }

    private NameDeclaration findHere(NameOccurrence occurrence) {
        for (Iterator i = names.keySet().iterator(); i.hasNext();) {
            NameDeclaration nameDeclaration = (NameDeclaration)i.next();
            if (nameDeclaration.getImage().equals(occurrence.getObjectName())) {
                return nameDeclaration;
            }
        }
        return null;
    }

    public String toString() {
        String result = "";
        for (Iterator i = names.keySet().iterator(); i.hasNext();) {
            NameDeclaration nameDeclaration = (NameDeclaration)i.next();
            result += nameDeclaration.getImage() +",";
        }
        return result;
    }


}
