/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.util.Applier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Provides behavior common to all Scopes
 */
public abstract class AbstractScope implements Scope {

    private Scope parent;
    protected Map variableNames = new HashMap();
    protected Map methodNames = new HashMap();

    public ClassScope getEnclosingClassScope() {
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

    public Map getVariableDeclarations(boolean lookingForUsed) {
        VariableUsageFinderFunction f = new VariableUsageFinderFunction(variableNames, lookingForUsed);
        Applier.apply(f, variableNames.keySet().iterator());
        return f.getUsed();
    }

    public NameDeclaration addVariableNameOccurrence(NameOccurrence occurrence) {
        NameDeclaration decl = findVariableHere(occurrence);
        if (decl != null && !occurrence.isThisOrSuper()) {
            List nameOccurrences = (List) variableNames.get(decl);
            nameOccurrences.add(occurrence);
        }
        return decl;
    }

    protected abstract NameDeclaration findVariableHere(NameOccurrence occurrence);

    protected String glomNames() {
        String result = "";
        for (Iterator i = variableNames.keySet().iterator(); i.hasNext();) {
            result += i.next().toString() + ",";
        }
        return result;
    }

}
