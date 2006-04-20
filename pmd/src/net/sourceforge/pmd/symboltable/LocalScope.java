/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.util.Applier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalScope extends AbstractScope {

    protected Map variableNames = new HashMap();

    public NameDeclaration addVariableNameOccurrence(NameOccurrence occurrence) {
        NameDeclaration decl = findVariableHere(occurrence);
        if (decl != null && !occurrence.isThisOrSuper()) {
            List nameOccurrences = (List) variableNames.get(decl);
            nameOccurrences.add(occurrence);
            SimpleNode n = occurrence.getLocation();
            if (n instanceof ASTName) {
                ((ASTName) n).setNameDeclaration(decl);
            } // TODO what to do with PrimarySuffix case?
        }
        return decl;
    }

    public Map getVariableDeclarations() {
        VariableUsageFinderFunction f = new VariableUsageFinderFunction(variableNames);
        Applier.apply(f, variableNames.keySet().iterator());
        return f.getUsed();
    }

    public void addDeclaration(VariableNameDeclaration nameDecl) {
        if (variableNames.containsKey(nameDecl)) {
            throw new RuntimeException("Variable " + nameDecl + " is already in the symbol table");
        }
        variableNames.put(nameDecl, new ArrayList());
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
        return "LocalScope:" + glomNames(variableNames.keySet().iterator());
    }
}
