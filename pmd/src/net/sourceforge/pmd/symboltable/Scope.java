/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 1:33:03 PM
 */
package net.sourceforge.pmd.symboltable;

import java.util.*;

public class Scope {

    private Map nameDeclarations = new HashMap();

    public void addDeclaration(NameDeclaration nameDecl) {
        if (nameDeclarations.containsKey(nameDecl)) {
            throw new RuntimeException(nameDecl + " is already in the symbol table");
        }
        nameDeclarations.put(nameDecl, new ArrayList());
    }

    public boolean contains(NameOccurrence nameOccurrence) {
        return nameDeclarations.containsKey(nameOccurrence.copyIntoNameDeclaration());
    }

    public void addOccurrence(NameOccurrence nameOccurrence) {
        List nameOccurrences = (List)nameDeclarations.get(nameOccurrence.copyIntoNameDeclaration());
        nameOccurrences.add(nameOccurrence);
    }

    public Iterator getUnusedDeclarations() {
        List unused = new ArrayList();
        for (Iterator i = nameDeclarations.keySet().iterator(); i.hasNext();) {
            NameDeclaration nameDeclaration = (NameDeclaration)i.next();
            if (((List)nameDeclarations.get(nameDeclaration)).isEmpty()) {
                unused.add(nameDeclaration);
            }
        }
        return unused.iterator();
    }

    public String toString() {
        String x = "NameDeclaration table:";
        for (Iterator i = nameDeclarations.keySet().iterator(); i.hasNext();) {
            NameDeclaration nameDecl = (NameDeclaration)i.next();
            x += nameDecl.getImage() + ",";
        }
        return x;
    }
}
