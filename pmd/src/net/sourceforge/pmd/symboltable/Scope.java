/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 1:33:03 PM
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.SimpleNode;

import java.util.*;

/**
 * See JLS 6.3 for a description of scopes
 */
public class Scope {

    private Map names = new HashMap();

    public void addDeclaration(NameDeclaration nameDecl) {
        if (names.containsKey(nameDecl)) {
            throw new RuntimeException(nameDecl + " is already in the symbol table");
        }
        names.put(nameDecl, new ArrayList());
    }

    public boolean contains(NameOccurrence occurrence) {
        for (Iterator i = names.keySet().iterator(); i.hasNext();) {
            NameDeclaration nameDeclaration = (NameDeclaration)i.next();
            if (nameDeclaration.getImage().equals(occurrence.getImage())) {
                return true;
            }
            if (nameDeclaration.getKind().equals(Kind.LOCAL_VARIABLE) && nameDeclaration.getImage().equals(getObjectName(occurrence.getImage()))) {
                return true;
            }
        }
        return false;
    }

    public void addOccurrence(NameOccurrence occurrence) {
        for (Iterator i = names.keySet().iterator(); i.hasNext();) {
            NameDeclaration nameDeclaration = (NameDeclaration)i.next();
            if (nameDeclaration.getImage().equals(occurrence.getImage())) {
                List nameOccurrences = (List)names.get(nameDeclaration);
                nameOccurrences.add(occurrence);
            } else if (nameDeclaration.getKind().equals(Kind.LOCAL_VARIABLE) && nameDeclaration.getImage().equals(getObjectName(occurrence.getImage()))) {
                List nameOccurrences = (List)names.get(nameDeclaration);
                nameOccurrences.add(occurrence);
            }
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

    private String getObjectName(String name) {
        return (name.indexOf('.') == -1) ? name : name.substring(0, name.indexOf('.'));
    }
}
