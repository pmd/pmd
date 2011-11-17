/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTName;

public class LocalScope extends AbstractScope {

    protected Map<VariableNameDeclaration, List<NameOccurrence>> variableNames = new HashMap<VariableNameDeclaration, List<NameOccurrence>>();

    public NameDeclaration addVariableNameOccurrence(NameOccurrence occurrence) {
        NameDeclaration decl = findVariableHere(occurrence);
        if (decl != null && !occurrence.isThisOrSuper()) {
            List<NameOccurrence> nameOccurrences = variableNames.get(decl);
            nameOccurrences.add(occurrence);
            Node n = occurrence.getLocation();
            if (n instanceof ASTName) {
                ((ASTName) n).setNameDeclaration(decl);
            } // TODO what to do with PrimarySuffix case?
        }
        return decl;
    }

    public Map<VariableNameDeclaration, List<NameOccurrence>> getVariableDeclarations() {
        VariableUsageFinderFunction f = new VariableUsageFinderFunction(variableNames);
        Applier.apply(f, variableNames.keySet().iterator());
        return f.getUsed();
    }

    public void addDeclaration(VariableNameDeclaration nameDecl) {
        if (variableNames.containsKey(nameDecl)) {
            throw new RuntimeException("Variable " + nameDecl + " is already in the symbol table");
        }
        variableNames.put(nameDecl, new ArrayList<NameOccurrence>());
    }

    public NameDeclaration findVariableHere(NameOccurrence occurrence) {
        if (occurrence.isThisOrSuper() || occurrence.isMethodOrConstructorInvocation()) {
            return null;
        }
        ImageFinderFunction finder = new ImageFinderFunction(occurrence.getImage());
        Applier.apply(finder, variableNames.keySet().iterator());
        return finder.getDecl();
    }

    public String toString() {
        return "LocalScope:" + glomNames(variableNames.keySet());
    }
}
