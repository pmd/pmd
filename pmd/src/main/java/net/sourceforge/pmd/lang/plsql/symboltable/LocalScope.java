/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.symboltable;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTName;
import net.sourceforge.pmd.lang.symboltable.AbstractScope;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class LocalScope extends AbstractScope {

    @Override
    public NameDeclaration addNameOccurrence(NameOccurrence occ) {
        PLSQLNameOccurrence occurrence = (PLSQLNameOccurrence)occ;
        NameDeclaration decl = findVariableHere(occurrence);
        if (decl != null && !occurrence.isThisOrSuper()) {
            List<NameOccurrence> nameOccurrences = getVariableDeclarations().get(decl);
            nameOccurrences.add(occurrence);
            Node n = occurrence.getLocation();
            if (n instanceof ASTName) {
                ((ASTName) n).setNameDeclaration(decl);
            } // TODO what to do with PrimarySuffix case?
        }
        return decl;
    }

    public Map<VariableNameDeclaration, List<NameOccurrence>> getVariableDeclarations() {
        return getDeclarations(VariableNameDeclaration.class);
    }

    @Override
    public void addDeclaration(NameDeclaration declaration) {
        if (declaration instanceof VariableNameDeclaration && getDeclarations().keySet().contains(declaration)) {
            throw new RuntimeException(declaration + " is already in the symbol table");
        }
        super.addDeclaration(declaration);
    }

    public NameDeclaration findVariableHere(PLSQLNameOccurrence occurrence) {
        if (occurrence.isThisOrSuper() || occurrence.isMethodOrConstructorInvocation()) {
            return null;
        }
        ImageFinderFunction finder = new ImageFinderFunction(occurrence.getImage());
        Applier.apply(finder, getVariableDeclarations().keySet().iterator());
        return finder.getDecl();
    }

    public String toString() {
        return "LocalScope:" + getVariableDeclarations().keySet();
    }
}
