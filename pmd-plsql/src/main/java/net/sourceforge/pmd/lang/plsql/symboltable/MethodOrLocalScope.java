/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.symboltable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.symboltable.AbstractScope;
import net.sourceforge.pmd.lang.symboltable.Applier;
import net.sourceforge.pmd.lang.symboltable.ImageFinderFunction;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

abstract class MethodOrLocalScope extends AbstractScope {
    @Override
    public void addDeclaration(NameDeclaration declaration) {
        if (declaration instanceof VariableNameDeclaration && getDeclarations().keySet().contains(declaration)) {
            throw new RuntimeException(declaration + " is already in the symbol table");
        }
        super.addDeclaration(declaration);
    }

    public Map<VariableNameDeclaration, List<NameOccurrence>> getVariableDeclarations() {
        return getDeclarations(VariableNameDeclaration.class);
    }

    public Set<NameDeclaration> findVariableHere(PLSQLNameOccurrence occurrence) {
        Set<NameDeclaration> result = new HashSet<>();
        if (occurrence.isThisOrSuper() || occurrence.isMethodOrConstructorInvocation()) {
            return result;
        }
        ImageFinderFunction finder = new ImageFinderFunction(occurrence.getImage());
        Applier.apply(finder, getVariableDeclarations().keySet().iterator());
        if (finder.getDecl() != null) {
            result.add(finder.getDecl());
        }
        return result;
    }
}
