/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.symboltable;

import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTName;
import net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class MethodScope extends MethodOrLocalScope {

    private final PLSQLNode node;

    public MethodScope(PLSQLNode node) {
        this.node = node;
    }

    public MethodScope getEnclosingMethodScope() {
        return this;
    }

    @Override
    public Set<NameDeclaration> addNameOccurrence(NameOccurrence occ) {
        PLSQLNameOccurrence occurrence = (PLSQLNameOccurrence) occ;
        Set<NameDeclaration> declarations = findVariableHere(occurrence);
        if (!declarations.isEmpty() && !occurrence.isThisOrSuper()) {
            for (NameDeclaration decl : declarations) {
                getVariableDeclarations().get(decl).add(occurrence);
                Node n = occurrence.getLocation();
                if (n instanceof ASTName) {
                    InternalApiBridge.setNameDeclaration((ASTName) n, decl);
                } // TODO what to do with PrimarySuffix case?
            }
        }
        return declarations;
    }

    public String getName() {
        return node.getChild(1).getCanonicalImage();
    }

    @Override
    public String toString() {
        return "MethodScope:" + getVariableDeclarations().keySet();
    }
}
