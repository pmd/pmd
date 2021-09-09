/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.symboltable;

import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTName;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class LocalScope extends MethodOrLocalScope {

    @Override
    public Set<NameDeclaration> addNameOccurrence(NameOccurrence occ) {
        PLSQLNameOccurrence occurrence = (PLSQLNameOccurrence) occ;
        Set<NameDeclaration> declarations = findVariableHere(occurrence);
        if (!declarations.isEmpty() && !occurrence.isThisOrSuper()) {
            for (NameDeclaration decl : declarations) {
                List<NameOccurrence> nameOccurrences = getVariableDeclarations().get(decl);
                nameOccurrences.add(occurrence);
                Node n = occurrence.getLocation();
                if (n instanceof ASTName) {
                    InternalApiBridge.setNameDeclaration((ASTName) n, decl);
                } // TODO what to do with PrimarySuffix case?
            }
        }
        return declarations;
    }

    @Override
    public String toString() {
        return "LocalScope:" + getVariableDeclarations().keySet();
    }
}
