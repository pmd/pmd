/*
 * User: tom
 * Date: Oct 1, 2002
 * Time: 1:33:03 PM
 */
package net.sourceforge.pmd.symboltable;

import java.util.*;

public class Scope {

    private Map names = new HashMap();

    public void addDeclaration(NameDeclaration nameDecl) {
        if (names.containsKey(nameDecl)) {
            throw new RuntimeException(nameDecl + " is already in the symbol table");
        }
        names.put(nameDecl, new ArrayList());
    }

    public boolean contains(NameOccurrence occurrence) {
        return names.containsKey(occurrence.copyIntoNameDeclaration());
    }

    public void addOccurrence(NameOccurrence occurrence) {
        List nameOccurrences = (List)names.get(occurrence.copyIntoNameDeclaration());
        nameOccurrences.add(occurrence);
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
}
