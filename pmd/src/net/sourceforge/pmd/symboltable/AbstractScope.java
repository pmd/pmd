/*
 * User: tom
 * Date: Oct 3, 2002
 * Time: 11:13:59 AM
 */
package net.sourceforge.pmd.symboltable;

import java.util.*;

public abstract class AbstractScope implements Scope {

    private Scope parent;
    protected Map names = new HashMap();
    protected Map methodNames = new HashMap();

    public Scope getEnclosingClassScope() {
        return parent.getEnclosingClassScope();
    }

    public void setParent(Scope parent) {
        this.parent = parent;
    }

    public Scope getParent() {
        return parent;
    }

    public void addDeclaration(NameDeclaration nameDecl) {
        if (names.containsKey(nameDecl)) {
            throw new RuntimeException("Variable " + nameDecl + " is already in the symbol table");
        }
        names.put(nameDecl, new ArrayList());
    }


    public boolean contains(NameOccurrence occurrence) {
        return findHere(occurrence) != null;
    }

    public Map getUsedDeclarations() {
        Map used = new HashMap();
        for (Iterator i = names.keySet().iterator(); i.hasNext();) {
            NameDeclaration nameDeclaration = (NameDeclaration)i.next();
            List usages = (List)names.get(nameDeclaration);
            if (!usages.isEmpty()) {
                used.put(nameDeclaration, usages);
            }
        }
        return used;
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

    public NameDeclaration addOccurrence(NameOccurrence occurrence) {
        NameDeclaration decl = findHere(occurrence);
        if (decl != null) {
            List nameOccurrences = (List)names.get(decl);
            nameOccurrences.add(occurrence);
        }
        return decl;
    }

    public void addMethodDeclaration(MethodNameDeclaration decl) {
        parent.addMethodDeclaration(decl);
    }

    protected abstract NameDeclaration findHere(NameOccurrence occurrence);

    protected String glomNames() {
        String result = "";
        for (Iterator i = names.keySet().iterator(); i.hasNext();) {
            result += i.next().toString() +",";
        }
        return result;
    }

}
