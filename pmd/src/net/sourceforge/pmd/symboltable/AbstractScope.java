/*
 * User: tom
 * Date: Oct 3, 2002
 * Time: 11:13:59 AM
 */
package net.sourceforge.pmd.symboltable;

import java.util.*;

/**
 * Provides behavior common to all Scopes
 */
public abstract class AbstractScope implements Scope {

    private Scope parent;
    protected Map variableNames = new HashMap();
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

    public void addDeclaration(VariableNameDeclaration variableDecl) {
        if (variableNames.containsKey(variableDecl)) {
            throw new RuntimeException("Variable " + variableDecl + " is already in the symbol table");
        }
        variableNames.put(variableDecl, new ArrayList());
    }

    public void addDeclaration(MethodNameDeclaration methodDecl) {
        parent.addDeclaration(methodDecl);
    }

    public boolean contains(NameOccurrence occurrence) {
        return findVariableHere(occurrence) != null;
    }

    public Map getUsedVariableDeclarations() {
        Map used = new HashMap();
        for (Iterator i = variableNames.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration nameDeclaration = (VariableNameDeclaration)i.next();
            List usages = (List)variableNames.get(nameDeclaration);
            if (!usages.isEmpty()) {
                used.put(nameDeclaration, usages);
            }
        }
        return used;
    }

    public Iterator getUnusedVariableDeclarations() {
        List unused = new ArrayList();
        for (Iterator i = variableNames.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration nameDeclaration = (VariableNameDeclaration)i.next();
            if (((List)variableNames.get(nameDeclaration)).isEmpty()) {
                unused.add(nameDeclaration);
            }
        }
        return unused.iterator();
    }

    public NameDeclaration addVariableNameOccurrence(NameOccurrence occurrence) {
        NameDeclaration decl = findVariableHere(occurrence);
        if (decl != null) {
            List nameOccurrences = (List)variableNames.get(decl);
            nameOccurrences.add(occurrence);
        }
        return decl;
    }

    protected abstract NameDeclaration findVariableHere(NameOccurrence occurrence);

    protected String glomNames() {
        String result = "";
        for (Iterator i = variableNames.keySet().iterator(); i.hasNext();) {
            result += i.next().toString() +",";
        }
        return result;
    }

}
